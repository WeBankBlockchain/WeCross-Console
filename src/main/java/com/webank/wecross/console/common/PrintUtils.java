package com.webank.wecross.console.common;

import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.methods.response.*;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintUtils {
    private static Logger logger = LoggerFactory.getLogger(PrintUtils.class);

    public static void printTransactionResponse(TransactionResponse response, boolean isCall) {
        if (response == null) {
            System.out.println("Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getReceipt().getErrorCode()
                            + "), message("
                            + response.getReceipt().getErrorMessage()
                            + ")");
            logger.warn("TxError: " + response.getReceipt().toString());
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

    public static void printRoutineResponse(RoutineResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            System.out.println("Result: success!");
        }
    }

    public static void printRoutineInfoResponse(RoutineInfoResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            ConsoleUtils.printJson(response.getInfo());
        }
    }

    public static void printRoutineIDResponse(RoutineIDResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            System.out.println("Result: " + Arrays.toString(response.getIDs()));
        }
    }

    public static void printCommandResponse(CommandResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
        } else {
            System.out.println("Result: " + response.getResult());
        }
    }
}
