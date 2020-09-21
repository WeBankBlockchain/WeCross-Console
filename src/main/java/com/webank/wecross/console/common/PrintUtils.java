package com.webank.wecross.console.common;

import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.common.StatusCode;
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
                            + response.getReceipt().getErrorMessage()
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

    public static void printRoutineResponse(RoutineResponse response)
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
            System.out.println("Result: success!");
        }
    }

    public static void printRoutineInfoResponse(RoutineInfoResponse response) throws Exception {
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
            ConsoleUtils.printJson(response.getInfo());
        }
    }

    public static void printRoutineIDResponse(RoutineIDResponse response)
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
            System.out.println("Result: " + Arrays.toString(response.getIDs()));
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
