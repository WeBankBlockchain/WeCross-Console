package com.webank.wecross.console.common;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.routine.TwoPcFace;
import com.webank.wecrosssdk.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.reader.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {

    public static final String TRANSACTION_LOG_TOML = "transactionLog.toml";
    public static final String CONF = "conf";
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private static Toml getToml(String fileName) throws WeCrossConsoleException {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            return new Toml().read(resolver.getResource(fileName).getInputStream());
        } catch (Exception e) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Something wrong with parsing " + fileName + ": " + e);
        }
    }

    public static String mergeSource(
            String currentDir, String sourceFile, PathMatchingResourcePatternResolver resolver)
            throws IOException {
        StringBuilder sourceBuffer = new StringBuilder();

        String fullPath = currentDir + sourceFile;
        String dir = fullPath.substring(0, fullPath.lastIndexOf(File.separator)) + File.separator;

        org.springframework.core.io.Resource sourceResource = resolver.getResource("file:" + fullPath);
        if (!sourceResource.exists()) {
            logger.error("Source file: {} not found!", fullPath);

            throw new IOException("Source file:" + fullPath + " not found");
        }

        Pattern pattern = Pattern.compile("^\\s*import\\s+[\"'](.+)[\"']\\s*;\\s*$");
        try (Scanner scanner = new Scanner(sourceResource.getInputStream(), "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String depSourcePath = matcher.group(1);
                    sourceBuffer.append(mergeSource(dir, depSourcePath, resolver));
                } else {
                    sourceBuffer.append(line);
                    sourceBuffer.append(System.lineSeparator());
                }
            }
        }

        return sourceBuffer.toString();
    }

    public static String readSourceFile(String path) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource resource = resolver.getResource("file:" + path);
        if (!resource.exists()) {
            resource = resolver.getResource("classpath:" + path);
            if (!resource.exists()) {
                logger.error("Source file: {} not exists", path);
                throw new IOException("Source file: " + path + " not exists");
            }
        }

        String filename = resource.getFilename();
        String realPath = resource.getFile().getAbsolutePath();
        String dir = realPath.substring(0, realPath.lastIndexOf(File.separator)) + File.separator;

        return mergeSource(dir, filename, resolver);
    }

    public static String readFileContent(String fileName) throws IOException {
        try {
            Path path;

            if (fileName.indexOf("classpath:") != 0) {
                path = Paths.get(fileName);
            } else {
                // Start with "classpath:"
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                path = Paths.get(resolver.getResource(fileName).getURI());
            }
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            logger.error("Read file error: {}", e.getMessage());
            throw new IOException("Read file error: " + e.getMessage());
        }
    }

    public static String readFileToBytesString(String filePath) throws Exception {
        String content = readFileContent(filePath);
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    private static String getTransactionID(Toml toml) throws WeCrossConsoleException {
        String transactionID = toml.getString("transactionID");
        if (transactionID == null) {
            String errorMessage =
                    "Something wrong with parsing [transactionID], please check configuration";
            throw new WeCrossConsoleException(ErrorCode.FIELD_MISSING, errorMessage);
        }

        return transactionID;
    }

    private static List<String> getTransactionPath(Toml toml) throws WeCrossConsoleException {
        List<String> transactionPath = toml.getList("paths");
        if (transactionPath == null) {
            String errorMessage =
                    "Something wrong with parsing [paths], please check configuration";
            throw new WeCrossConsoleException(ErrorCode.FIELD_MISSING, errorMessage);
        }

        return transactionPath;
    }

    public static void loadTransactionLog(List<Completer> completers, TwoPcFace twoPcFace) {
        try {
            Toml toml = getToml(TRANSACTION_LOG_TOML);
            String transactionID = getTransactionID(toml);
            List<String> transactionPath = getTransactionPath(toml);

            if (!twoPcFace.isTransactionInfoExist(
                    transactionID, transactionPath.toArray(new String[0]))) {
                logger.error(
                        "loadTransactionLog error: the transaction in toml file had already been committed/rollbacked or even doesn't exist.");
                return;
            }
            TransactionInfo transactionInfo = new TransactionInfo(transactionID, transactionPath);
            ConsoleUtils.runtimeTransactionThreadLocal.set(transactionInfo);
            JlineUtils.addTransactionInfoCompleters(completers);
        } catch (WeCrossConsoleException e) {
            logger.warn("Load transactionLog Toml file fail, error: {}.", e.getMessage());
        } catch (Exception e) {
            logger.warn("Load transactionLog Toml file fail, error is " + e.getMessage());
        }
    }

    public static void writeTransactionLog() {
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(
                    ConsoleUtils.runtimeTransactionThreadLocal.get(),
                    new File(CONF, TRANSACTION_LOG_TOML));
        } catch (IOException e) {
            logger.error("Write TransactionLogTOML file error: {}", e.getMessage());
        }
    }

    public static void cleanTransactionLog(String filename) throws WeCrossConsoleException {
        File file = new File(CONF, filename);
        if (!file.exists()) {
            logger.error("Cannot find file: {}", filename);
            return;
        }
        if (!file.delete()) {
            logger.error("Cannot delete file: {}", filename);
            throw new WeCrossConsoleException(
                    ErrorCode.FIELD_MISSING, "Cannot delete file: " + filename);
        }
    }
}
