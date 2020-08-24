package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.rpc.RPCFace;
import com.webank.wecross.console.rpc.RPCImpl;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.RoutineIDResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineInfoResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineResponse;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.util.*;

public class TwoPcImpl implements TwoPcFace {
    private WeCrossRPC weCrossRPC;
    private RPCFace rpcFace = new RPCImpl();

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
        if (params.length < 5) {
            HelpInfo.promptHelp("callTransaction");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        String account = params[2];
        String transactionID = params[3];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            System.out.println(
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
            return;
        }

        String method = params[4];

        TransactionResponse response;
        if (params.length == 5) {
            // no param given means: null (not String[0])
            response =
                    weCrossRPC.callTransaction(transactionID, path, account, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .callTransaction(
                                    transactionID,
                                    path,
                                    account,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 5, params.length)))
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
        if (params.length < 6) {
            HelpInfo.promptHelp("execTransaction");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        String account = params[2];
        String transactionID = params[3];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            System.out.println(
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
            return;
        }

        String seq = params[4];
        if (!ConsoleUtils.isNaturalInteger(seq)) {
            System.out.println(
                    "Error: " + seq + " is not a valid seq, only natural integer allowed!");
            return;
        }

        String method = params[5];

        TransactionResponse response;
        if (params.length == 6) {
            // no param given means: null (not String[0])
            response =
                    weCrossRPC
                            .execTransaction(transactionID, seq, path, account, method, null)
                            .send();
        } else {
            response =
                    weCrossRPC
                            .execTransaction(
                                    transactionID,
                                    seq,
                                    path,
                                    account,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 6, params.length)))
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
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "startTransaction");
        }

        String transactionID = params[1];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_TXID,
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
        }

        List<String> accounts = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, accounts, paths);

        RoutineResponse response =
                weCrossRPC
                        .startTransaction(
                                transactionID,
                                accounts.toArray(new String[0]),
                                paths.toArray(new String[0]))
                        .send();

        PrintUtils.printRoutineResponse(response);
        Map<String, List<String>> txMap = new HashMap<>();
        txMap.put("accounts", accounts);
        txMap.put("paths", paths);
        ConsoleUtils.runtimeTransactionInfo.put(transactionID, txMap);
        ConsoleUtils.runtimeTransactionIDs.add(transactionID);
    }

    @Override
    public void commitTransaction(String[] params) throws Exception {
        // only support one console do one transaction
        if (params.length == 1) {
            if (!ConsoleUtils.runtimeTransactionIDs.isEmpty()
                    && !ConsoleUtils.runtimeTransactionInfo.isEmpty()) {
                String runtimeTXID = ConsoleUtils.runtimeTransactionIDs.get(0);
                RoutineResponse response =
                        weCrossRPC
                                .commitTransaction(
                                        runtimeTXID,
                                        ConsoleUtils.runtimeTransactionInfo
                                                .get(runtimeTXID)
                                                .get("accounts")
                                                .toArray(new String[0]),
                                        ConsoleUtils.runtimeTransactionInfo
                                                .get(runtimeTXID)
                                                .get("paths")
                                                .toArray(new String[0]))
                                .send();
                PrintUtils.printRoutineResponse(response);
                return;
            } else {
                throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "commitTransaction");
            }
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.commitTransactionHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "commitTransaction");
        }

        String transactionID = params[1];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_TXID,
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
        }

        List<String> accounts = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, accounts, paths);

        RoutineResponse response =
                weCrossRPC
                        .commitTransaction(
                                transactionID,
                                accounts.toArray(new String[0]),
                                paths.toArray(new String[0]))
                        .send();
        PrintUtils.printRoutineResponse(response);
    }

    @Override
    public void rollbackTransaction(String[] params) throws Exception {
        if (params.length == 1) {
            if (!ConsoleUtils.runtimeTransactionIDs.isEmpty()
                    && !ConsoleUtils.runtimeTransactionInfo.isEmpty()) {
                String runtimeTXID = ConsoleUtils.runtimeTransactionIDs.get(0);
                RoutineResponse response =
                        weCrossRPC
                                .rollbackTransaction(
                                        runtimeTXID,
                                        ConsoleUtils.runtimeTransactionInfo
                                                .get(runtimeTXID)
                                                .get("accounts")
                                                .toArray(new String[0]),
                                        ConsoleUtils.runtimeTransactionInfo
                                                .get(runtimeTXID)
                                                .get("paths")
                                                .toArray(new String[0]))
                                .send();
                PrintUtils.printRoutineResponse(response);
                return;
            } else {
                throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "rollbackTransaction");
            }
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.rollbackTransactionHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "rollbackTransaction");
        }

        String transactionID = params[1];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_TXID,
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
        }

        List<String> accounts = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, accounts, paths);

        RoutineResponse response =
                weCrossRPC
                        .rollbackTransaction(
                                transactionID,
                                accounts.toArray(new String[0]),
                                paths.toArray(new String[0]))
                        .send();
        PrintUtils.printRoutineResponse(response);
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
        if (params.length < 4) {
            HelpInfo.promptHelp("getTransactionInfo");
            return;
        }

        String transactionID = params[1];
        if (!ConsoleUtils.isNumeric(transactionID)) {
            System.out.println(
                    "Error: " + transactionID + " is not a valid id, only number allowed!");
            return;
        }

        List<String> accounts = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        parseTransactionParam(params, accounts, paths);

        RoutineInfoResponse response =
                weCrossRPC
                        .getTransactionInfo(
                                transactionID,
                                accounts.toArray(new String[0]),
                                paths.toArray(new String[0]))
                        .send();
        PrintUtils.printRoutineInfoResponse(response);
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
        if (params.length != 4) {
            HelpInfo.promptHelp("getTransactionIDs");
            return;
        }

        String path = params[1];
        String account = params[2];
        int option = Integer.parseInt(params[3]);

        RoutineIDResponse response = weCrossRPC.getTransactionIDs(path, account, option).send();
        PrintUtils.printRoutineIDResponse(response);
    }

    private void parseTransactionParam(String[] params, List<String> accounts, List<String> paths)
            throws Exception {
        Set<String> allAccounts = rpcFace.getAccounts();
        Set<String> allPaths = rpcFace.getPaths();

        if (allAccounts.contains(params[2])) {
            accounts.add(params[2]);
        } else {
            throw new Exception("account " + params[2] + " not found");
        }

        boolean isAccount = true;
        boolean isPath = false;
        for (int i = 3; i < params.length; i++) {
            if (isAccount) {
                if (allAccounts.contains(params[i])) {
                    if (accounts.contains(params[i])) {
                        throw new Exception("duplicated account " + params[i]);
                    }
                    accounts.add(params[i]);
                } else {
                    if (!params[i].contains(".")) {
                        throw new Exception("account " + params[i] + " not found");
                    } else {
                        isAccount = false;
                        isPath = true;
                    }
                }
            }
            if (isPath) {
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
}
