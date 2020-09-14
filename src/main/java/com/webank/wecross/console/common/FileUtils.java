package com.webank.wecross.console.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.exception.ErrorCode;
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

    public static String readFileContent(String fileName) throws Exception {
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
            throw new Exception("Read file error: " + e.getMessage());
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

    private static List<String> getTransactionAccount(Toml toml) throws WeCrossConsoleException {
        List<String> transactionAccount = toml.getList("accounts");
        if (transactionAccount == null) {
            String errorMessage =
                    "Something wrong with parsing [accounts], please check configuration";
            throw new WeCrossConsoleException(ErrorCode.FIELD_MISSING, errorMessage);
        }

        return transactionAccount;
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


    public static void loadTransactionLog(List<Completer> completers){
        try {
            Toml toml = getToml(TRANSACTION_LOG_TOML);
            String transactionID = getTransactionID(toml);
            List<String> transactionAccount = getTransactionAccount(toml);
            List<String> transactionPath = getTransactionPath(toml);

            TransactionInfo transactionInfo = new TransactionInfo(transactionID, transactionAccount, transactionPath);
            ConsoleUtils.runtimeTransactionThreadLocal.set(transactionInfo);
            JlineUtils.addTransactionInfoCompleters(completers,transactionID);
        } catch (WeCrossConsoleException e) {
            logger.warn("Load transactionLog Toml file fail, error: {}.",e.getMessage());
        }
    }

    public static void writeTransactionLog(){
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(ConsoleUtils.runtimeTransactionThreadLocal.get(),new File(CONF,TRANSACTION_LOG_TOML));
        } catch (IOException e) {
            logger.error("Write TransactionLogTOML file error: {}",e.getMessage());
        }
    }

    public static void cleanTransactionLog(String filename) throws WeCrossConsoleException {
        File file = new File(CONF,filename);
        if(!file.exists()) {
            logger.error("Cannot find file: {}", filename);
            return;
        }
        if (!file.delete()){
            logger.error("Cannot delete file: {}", filename);
            throw new WeCrossConsoleException(ErrorCode.FIELD_MISSING,"Cannot delete file: "+filename);
        }
    }
}
