package com.webank.wecross.console.common;

import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineInfoResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineResponse;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintUtils {
    private static Logger logger = LoggerFactory.getLogger(PrintUtils.class);

    public static void printTransactionResponse(TransactionResponse response, boolean isCall) {
        if (response == null) {
            System.out.println("Response: null");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.getReceipt().toString());
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
            System.out.println("Response: null");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
        } else {
            System.out.println("Result: " + response.getResult());
        }
    }

    public static void printRoutineInfoResponse(RoutineInfoResponse response) {
        if (response == null) {
            System.out.println("Response: null");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
        } else {
            ConsoleUtils.printJson(response.getInfo());
        }
    }

    public static void printCommandResponse(CommandResponse response) {
        if (response == null) {
            System.out.println("Response: null");
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
        } else {
            System.out.println("Result: " + response.getResult());
        }
    }
}
