package com.webank.wecross.console.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.common.TransactionContext;
import com.webank.wecrosssdk.rpc.methods.response.*;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintUtils {
    private static Logger logger = LoggerFactory.getLogger(PrintUtils.class);

    public static void printTransactionResponse(TransactionResponse response, boolean isCall)
            throws WeCrossConsoleException {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            logger.warn("TxError: " + response.getReceipt().toString());
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getReceipt().getErrorCode()
                            + "), message("
                            + response.getReceipt().getMessage()
                            + ")");
        } else {
            if (!isCall) {
                System.out.println("Txhash  : " + response.getReceipt().getHash());
                System.out.println("BlockNum: " + response.getReceipt().getBlockNumber());
                System.out.println(
                        "Result  : " + Arrays.toString(response.getReceipt().getResult()));
            } else {
                System.out.println("Result: " + Arrays.toString(response.getReceipt().getResult()));
            }
        }
    }

    public static void printRoutineResponse(XAResponse response) throws WeCrossConsoleException {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            TransactionContext.txThreadLocal.remove();
            TransactionContext.pathInTransactionThreadLocal.remove();
            FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getXARawResponse().getStatus() != StatusCode.SUCCESS) {
            TransactionContext.txThreadLocal.remove();
            TransactionContext.pathInTransactionThreadLocal.remove();
            FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    Arrays.toString(response.getXARawResponse().getChainErrorMessages().toArray()));
        } else {
            System.out.println("Result: success!");
        }
    }

    public static void printRollbackResponse(XAResponse response) throws WeCrossConsoleException {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getXARawResponse().getStatus() != StatusCode.SUCCESS) {
            System.out.println(
                    Arrays.toString(response.getXARawResponse().getChainErrorMessages().toArray()));
        } else {
            System.out.println("Result: success!");
        }
    }

    public static void printRoutineInfoResponse(XATransactionResponse response) throws Exception {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getRawXATransactionResponse().getXaResponse().getStatus()
                != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    Arrays.toString(
                            response.getRawXATransactionResponse()
                                    .getXaResponse()
                                    .getChainErrorMessages()
                                    .toArray()));
        } else {
            ConsoleUtils.printJson(
                    response.getRawXATransactionResponse().getXaTransaction().toString());
        }
    }

    public static void printRoutineIDResponse(XATransactionListResponse response)
            throws Exception {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(
                    "Result: "
                            + objectMapper
                                    .writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(
                                            response.getRawXATransactionListResponse()
                                                    .getXaList()));
        }
    }

    public static void printCommandResponse(CommandResponse response)
            throws WeCrossConsoleException {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            System.out.println("Result: " + response.getResult());
        }
    }

    public static void printUAResponse(UAResponse response) throws WeCrossConsoleException {
        if (response == null) {
            throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getUAReceipt().getErrorCode() != StatusCode.SUCCESS) {
            logger.warn("UAResponse: " + response.getUAReceipt().toString());
            throw new WeCrossConsoleException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error: code("
                            + response.getUAReceipt().getErrorCode()
                            + "), message("
                            + response.getUAReceipt().getMessage()
                            + ")");
        } else {
            System.out.println("Result: " + response.getUAReceipt().getMessage());
            if (response.getUAReceipt().getUniversalAccount() != null) {
                ConsoleUtils.doubleLine();
                System.out.println(response.getUAReceipt().getUniversalAccount().toFormatString());
            }
        }
    }
}
