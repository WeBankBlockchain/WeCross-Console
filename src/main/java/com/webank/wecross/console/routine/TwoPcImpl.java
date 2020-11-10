package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.*;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.rpc.RPCFace;
import com.webank.wecross.console.rpc.RPCImpl;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.RoutineIDResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineInfoResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineResponse;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import com.webank.wecrosssdk.utils.RPCUtils;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoPcImpl implements TwoPcFace {
    private WeCrossRPC weCrossRPC;
    private final Logger logger = LoggerFactory.getLogger(TwoPcImpl.class);
    private final RPCFace rpcFace = new RPCImpl();

    @Override
    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
        rpcFace.setWeCrossRPC(weCrossRPC);
    }

    @Override
    public void callTransaction(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("callTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.callTransactionHelp();
            return;
        }
        if (params.length < 3) {
            HelpInfo.promptHelp("callTransaction");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        TransactionInfo transactionInfo = ConsoleUtils.runtimeTransactionThreadLocal.get();
        if (transactionInfo == null) {
            System.out.println("There is no Transaction running now, please check again.");
            return;
        }
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "callTransaction: Path is invalid.");
        }
        if (!transactionInfo.getPaths().contains(path)) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH,
                    "callTransaction: Path is not in transaction, please check command 'call'.");
        }
        String transactionID = transactionInfo.getTransactionID();
        String method = params[2];
        TransactionResponse response;
        if (params.length == 3) {
            // no param given means: null (not String[0])
            response = weCrossRPC.callTransaction(transactionID, path, method, (String[]) null).send();
        } else {
            response =
                    weCrossRPC
                            .callTransaction(
                                    transactionID,
                                    path,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 3, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, true);
    }

    @Override
    public void execTransaction(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("execTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.execTransactionHelp();
            return;
        }
        if (params.length < 3) {
            HelpInfo.promptHelp("execTransaction");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        TransactionInfo transactionInfo = ConsoleUtils.runtimeTransactionThreadLocal.get();
        if (transactionInfo == null) {
            System.out.println("There is no Transaction running now, please check again.");
            return;
        }
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "execTransaction: Path is invalid.");
        }
        if (!transactionInfo.getPaths().contains(path)) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH,
                    "execTransaction: Path is not in transaction, please check command 'sendTransaction'.");
        }
        String transactionID = transactionInfo.getTransactionID();

        String method = params[2];

        TransactionResponse response;
        if (params.length == 3) {
            // no param given means: null (not String[0])
            response = weCrossRPC.execTransaction(transactionID, path, method, (String[]) null).send();
        } else {
            response =
                    weCrossRPC
                            .execTransaction(
                                    transactionID,
                                    path,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 3, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, true);
    }

    @Override
    public void startTransaction(String[] params) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "startTransaction");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.startTransactionHelp();
            return;
        }

        String transactionID = RPCUtils.genTransactionID();
        if (ConsoleUtils.runtimeTransactionThreadLocal.get() != null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_TXID,
                    "There is a transaction "
                            + ConsoleUtils.runtimeTransactionThreadLocal.get().getTransactionID()
                            + " running now, please commit/rollback it first.");
        }

        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, paths, 1);

        RoutineResponse response =
                weCrossRPC.startTransaction(transactionID, paths.toArray(new String[0])).send();

        PrintUtils.printRoutineResponse(response);
        System.out.println("Transaction ID is: " + transactionID);
        TransactionInfo transactionInfo = new TransactionInfo(transactionID, paths);
        ConsoleUtils.runtimeTransactionThreadLocal.set(transactionInfo);
        FileUtils.writeTransactionLog();
    }

    @Override
    public void commitTransaction(String[] params) throws Exception {
        // only support one console do one transaction
        TransactionInfo transactionInfo = ConsoleUtils.runtimeTransactionThreadLocal.get();
        if (params.length == 1) {
            if (transactionInfo != null) {
                System.out.println(
                        "Transaction running now, transactionID is: "
                                + transactionInfo.getTransactionID());
                System.out.print("Are you sure commit it now?(y/n)  ");
                String readIn;
                Scanner in = new Scanner(System.in);
                do {
                    readIn = in.nextLine();
                } while (readIn.equals("\t"));
                if (!readIn.equals("y") && !readIn.equals("Y")) {
                    throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Cancel commit.\n");
                } else {
                    System.out.println(
                            "Committing transaction: "
                                    + transactionInfo.getTransactionID()
                                    + "...\n");
                }
                RoutineResponse response =
                        weCrossRPC
                                .commitTransaction(
                                        transactionInfo.getTransactionID(),
                                        ConsoleUtils.runtimeTransactionThreadLocal
                                                .get()
                                                .getPaths()
                                                .toArray(new String[0]))
                                .send();
                PrintUtils.printRoutineResponse(response);
                FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
                return;
            } else {
                System.out.println("There is no transaction running now.");
                throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "commitTransaction");
            }
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.commitTransactionHelp();
            return;
        }

        if (ConsoleUtils.runtimeUsernameThreadLocal.get() == null) {
            System.out.println("Command commitTransaction needs Auth, please login.");
            return;
        }
        if (transactionInfo == null) {
            System.out.println("There is no Transaction running now, please check again.");
            return;
        }
        String transactionID = transactionInfo.getTransactionID();
        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, paths, 1);

        RoutineResponse response =
                weCrossRPC.commitTransaction(transactionID, paths.toArray(new String[0])).send();
        PrintUtils.printRoutineResponse(response);
        FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
    }

    @Override
    public void rollbackTransaction(String[] params) throws Exception {
        TransactionInfo transactionInfo = ConsoleUtils.runtimeTransactionThreadLocal.get();
        if (params.length == 1) {
            if (transactionInfo != null) {
                System.out.println(
                        "Transaction running now, transactionID is: "
                                + transactionInfo.getTransactionID());
                System.out.print("Are you sure rollback transaction now?(y/n)  ");
                String readIn;
                Scanner in = new Scanner(System.in);
                do {
                    readIn = in.nextLine();
                } while (Objects.equals(readIn, "\t"));
                if (!readIn.equals("y") && !readIn.equals("Y")) {
                    throw new WeCrossConsoleException(ErrorCode.NO_RESPONSE, "Cancel rollback.\n");
                } else {
                    System.out.println(
                            "Rollback transaction: "
                                    + transactionInfo.getTransactionID()
                                    + "...\n");
                }
                RoutineResponse response =
                        weCrossRPC
                                .rollbackTransaction(
                                        transactionInfo.getTransactionID(),
                                        ConsoleUtils.runtimeTransactionThreadLocal
                                                .get()
                                                .getPaths()
                                                .toArray(new String[0]))
                                .send();
                PrintUtils.printRoutineResponse(response);
                FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
                return;
            } else {
                System.out.println("There is no transaction running now.");
                throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "rollbackTransaction");
            }
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.rollbackTransactionHelp();
            return;
        }
        if (ConsoleUtils.runtimeUsernameThreadLocal.get() == null) {
            System.out.println("Command rollbackTransaction needs Auth, please login.");
            return;
        }
        if (transactionInfo == null) {
            System.out.println("There is no Transaction running now, please check again.");
            return;
        }
        String transactionID = transactionInfo.getTransactionID();

        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, paths, 1);

        RoutineResponse response =
                weCrossRPC.rollbackTransaction(transactionID, paths.toArray(new String[0])).send();
        PrintUtils.printRoutineResponse(response);
        FileUtils.cleanFile(FileUtils.CONF, FileUtils.TRANSACTION_LOG_TOML);
    }

    @Override
    public void getTransactionInfo(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("getTransactionInfo");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.getTransactionInfoHelp();
            return;
        }
        if (params.length < 3) {
            HelpInfo.promptHelp("getTransactionInfo");
            return;
        }

        String transactionID = params[1];

        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, paths, 2);

        RoutineInfoResponse response =
                weCrossRPC.getTransactionInfo(transactionID, paths.toArray(new String[0])).send();
        PrintUtils.printRoutineInfoResponse(response);
    }

    @Override
    public boolean isTransactionInfoExist(String txID, String[] paths) throws Exception {
        if (paths.length < 1) {
            return false;
        }

        RoutineInfoResponse response = weCrossRPC.getTransactionInfo(txID, paths).send();
        if (response == null || response.getInfo() == null) {
            logger.error("Transaction ID does not exist.");
            return false;
        }
        if (!response.getInfo().contains("status=0")) {
            logger.error("Transaction {} has been rollback/commit.", txID);
            return false;
        }
        return true;
    }

    @Override
    public void getTransactionIDs(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("getTransactionIDs");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.getTransactionIDsHelp();
            return;
        }
        if (params.length != 3) {
            HelpInfo.promptHelp("getTransactionIDs");
            return;
        }

        String path = params[1];
        int option = Integer.parseInt(params[2]);

        RoutineIDResponse response = weCrossRPC.getTransactionIDs(path, option).send();
        PrintUtils.printRoutineIDResponse(response);
    }

    @Override
    public void getCurrentTransactionID(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.getCurrentTransactionIDHelp();
            return;
        }
        String currentTransactionID = weCrossRPC.getCurrentTransactionID();
        if (currentTransactionID == null) {
            System.out.println("There is no Transaction running now, please check again.");
        } else {
            System.out.println(
                    "There is a Transaction running now, ID is: " + currentTransactionID + ".");
        }
    }

    private void parseTransactionParam(String[] params, List<String> paths, int startPos)
            throws Exception {
        Set<String> allPaths = rpcFace.getPaths();

        for (int i = startPos; i < params.length; i++) {
            if (!allPaths.contains(params[i])) {
                throw new Exception("resource " + params[i] + " not found");
            }

            if (paths.contains(params[i])) {
                throw new Exception("duplicated resource " + params[i]);
            }
            paths.add(params[i]);
        }
    }
}
