package com.webank.wecross.console.common;

import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecrosssdk.rpc.methods.response.*;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintUtils {
    private static Logger logger = LoggerFactory.getLogger(PrintUtils.class);

    public static int printTransactionResponse(TransactionResponse response, boolean isCall) {
        if (response == null) {
            System.out.println("Error: no response");
            return ErrorCode.NO_RESPONSE;
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
            return ErrorCode.INTERNAL_ERROR;
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getReceipt().getErrorCode()
                            + "), message("
                            + response.getReceipt().getErrorMessage()
                            + ")");
            logger.warn("TxError: " + response.getReceipt().toString());
            return ErrorCode.INTERNAL_ERROR;
        } else {
            if (!isCall) {
                System.out.println("Txhash  : " + response.getReceipt().getHash());
                System.out.println("BlockNum: " + response.getReceipt().getBlockNumber());
                System.out.println(
                        "Result  : " + Arrays.toString(response.getReceipt().getResult()));
            } else {
                System.out.println("Result: " + Arrays.toString(response.getReceipt().getResult()));
            }
            return StatusCode.SUCCESS;
        }
    }

    public static int printRoutineResponse(RoutineResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
            return ErrorCode.NO_RESPONSE;
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
            return ErrorCode.INTERNAL_ERROR;
        } else {
            System.out.println("Result: success!");
            return StatusCode.SUCCESS;
        }
    }

    public static int printRoutineInfoResponse(RoutineInfoResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
            return ErrorCode.NO_RESPONSE;
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
            return ErrorCode.INTERNAL_ERROR;
        } else {
            ConsoleUtils.printJson(response.getInfo());
            return StatusCode.SUCCESS;
        }
    }

    public static int printRoutineIDResponse(RoutineIDResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
            return ErrorCode.NO_RESPONSE;
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
            return ErrorCode.INTERNAL_ERROR;
        } else {
            System.out.println("Result: " + Arrays.toString(response.getIDs()));
            return StatusCode.SUCCESS;
        }
    }

    public static int printCommandResponse(CommandResponse response) {
        if (response == null) {
            System.out.println("Error: no response");
            return ErrorCode.NO_RESPONSE;
        } else if (response.getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "Error: code("
                            + response.getErrorCode()
                            + "), message("
                            + response.getMessage()
                            + ")");
            return ErrorCode.INTERNAL_ERROR;
        } else {
            System.out.println("Result: " + response.getResult());
            return StatusCode.SUCCESS;
        }
    }
}
