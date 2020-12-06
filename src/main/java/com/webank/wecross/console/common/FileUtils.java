package com.webank.wecross.console.common;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.routine.XAFace;
import com.webank.wecrosssdk.rpc.common.TransactionContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.reader.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {

    public static final String TRANSACTION_LOG_TOML = "transactionLog.toml";
    public static final String CONF = "conf";
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static Toml getToml(String fileName) throws WeCrossConsoleException {
        try {
            Path path;
            if (fileName.indexOf("classpath:") != 0) {
                path = Paths.get(fileName);
            } else {
                PathMatchingResourcePatternResolver resolver =
                        new PathMatchingResourcePatternResolver();
                if (!resolver.getResource(fileName).exists()) {
                    throw new WeCrossConsoleException(
                            ErrorCode.TX_LOG_NOT_EXIST,
                            TRANSACTION_LOG_TOML + " doesn't exist, clean transaction context.");
                }
                path = Paths.get(resolver.getResource(fileName).getURI());
            }
            return new Toml().read(path.toFile());
        } catch (IOException e) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Something wrong with parsing " + fileName + ": " + e);
        }
    }

    public static String mergeSource(
            String currentDir,
            String sourceFile,
            PathMatchingResourcePatternResolver resolver,
            Set<String> dependencies)
            throws Exception {
        StringBuffer sourceBuffer = new StringBuffer();

        String fullPath = currentDir + sourceFile;
        String dir = fullPath.substring(0, fullPath.lastIndexOf(File.separator)) + File.separator;

        org.springframework.core.io.Resource sourceResource =
                resolver.getResource("file:" + fullPath);
        if (!sourceResource.exists()) {
            logger.error("Source file: {} not found!", fullPath);

            throw new IOException("Source file:" + fullPath + " not found");
        }

        Pattern pattern = Pattern.compile("^\\s*import\\s+[\"'](.+)[\"']\\s*;\\s*$");
        try (Scanner scanner = new Scanner(sourceResource.getInputStream(), "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("pragma experimental ABIEncoderV2;")) {
                    if (!dependencies.contains("pragma experimental ABIEncoderV2;")) {
                        dependencies.add("pragma experimental ABIEncoderV2;");
                        sourceBuffer.append(line);
                        sourceBuffer.append(System.lineSeparator());
                    }
                    continue;
                }

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String depSourcePath = matcher.group(1);
                    String nextPath = dir + depSourcePath;
                    if (!dependencies.contains(nextPath)) {
                        dependencies.add(nextPath);
                        sourceBuffer.append(
                                mergeSource(dir, depSourcePath, resolver, dependencies));
                    }
                } else {
                    sourceBuffer.append(line);
                    sourceBuffer.append(System.lineSeparator());
                }
            }
        }

        return sourceBuffer.toString();
    }

    public static String readFileContent(String fileName) throws IOException {
        try {
            Path path;

            if (fileName.indexOf("classpath:") != 0) {
                path = Paths.get(fileName);
            } else {
                // Start with "classpath:"
                PathMatchingResourcePatternResolver resolver =
                        new PathMatchingResourcePatternResolver();
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

    public static String getTransactionID(Toml toml) throws WeCrossConsoleException {
        String transactionID = toml.getString("transactionID");
        if (transactionID == null) {
            String errorMessage =
                    "Something wrong with parsing [transactionID], please check configuration";
            throw new WeCrossConsoleException(ErrorCode.INVALID_TXID, errorMessage);
        }

        return transactionID;
    }

    public static List<String> getTransactionPath(Toml toml) throws WeCrossConsoleException {
        List<String> transactionPath = toml.getList("paths");
        if (transactionPath == null) {
            String errorMessage =
                    "Something wrong with parsing [paths], please check configuration";
            throw new WeCrossConsoleException(ErrorCode.INVALID_PATH, errorMessage);
        }

        return transactionPath;
    }

    public static void loadTransactionLog(List<Completer> completers, XAFace xaFace) {
        try {
            Toml toml = getToml("classpath:" + TRANSACTION_LOG_TOML);
            String transactionID = getTransactionID(toml);
            List<String> transactionPath = getTransactionPath(toml);

            if (!xaFace.isTransactionInfoExist(
                    transactionID, transactionPath.toArray(new String[0]))) {
                logger.error(
                        "loadTransactionLog error: the transaction in toml file had already been committed/rollbacked or even doesn't exist.");
                FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
                return;
            }
            TransactionContext.txThreadLocal.set(transactionID);
            TransactionContext.pathInTransactionThreadLocal.set(transactionPath);
        } catch (WeCrossConsoleException e) {
            if (e.getErrorCode() == ErrorCode.TX_LOG_NOT_EXIST) {
                logger.info("Load transactionLog Toml file fail, {}", e.getMessage());
            } else {
                logger.warn("Load transactionLog Toml file fail, error: {}.", e.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Load transactionLog Toml file fail, error: {}", e.getMessage());
        }
    }

    public static void writeTransactionLog() {
        TomlWriter writer = new TomlWriter();
        TransactionInfo transactionInfo =
                new TransactionInfo(
                        TransactionContext.currentXATransactionID(),
                        TransactionContext.pathInTransactionThreadLocal.get());
        try {
            writer.write(transactionInfo, new File(CONF, TRANSACTION_LOG_TOML));
        } catch (IOException e) {
            logger.error("Write TransactionLogTOML file error: {}", e.getMessage());
        }
    }

    public static void cleanFile(String prefix, String filename) throws WeCrossConsoleException {
        File file = new File(prefix, filename);
        if (!file.exists()) {
            logger.error("Cannot find file: {}", filename);
            return;
        }
        if (!file.delete()) {
            logger.error("Cannot delete file: {}", filename);
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR, "Cannot delete file: " + filename);
        }
    }

    public static void writeFile(String fileName, String content, boolean append)
            throws WeCrossConsoleException {
        try (FileWriter writer = new FileWriter(fileName, append)) {
            writer.write(content + "\n");
        } catch (IOException e) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR, "Load file " + fileName + " fail, error: " + e);
        }
    }
}
