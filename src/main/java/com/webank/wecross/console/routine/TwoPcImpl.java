package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.AccountResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineInfoResponse;
import com.webank.wecrosssdk.rpc.methods.response.RoutineResponse;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoPcImpl implements TwoPcFace {
    private WeCrossRPC weCrossRPC;

    private Logger logger = LoggerFactory.getLogger(TwoPcImpl.class);

    @Override
    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
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
                                    ConsoleUtils.parseAgrs(
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
        String seq = params[4];
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
                                    ConsoleUtils.parseAgrs(
                                            Arrays.copyOfRange(params, 6, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, true);
    }

    @Override
    public void startTransaction(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("startTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.startTransactionHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("startTransaction");
            return;
        }

        String transactionID = params[1];
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
    }

    @Override
    public void commitTransaction(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("commitTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.commitTransactionHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("commitTransaction");
            return;
        }

        String transactionID = params[1];
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
            HelpInfo.promptHelp("rollbackTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.rollbackTransactionHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("rollbackTransaction");
            return;
        }

        String transactionID = params[1];
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

    private void parseTransactionParam(String[] params, List<String> accounts, List<String> paths) {
        List<String> allAccounts = getAccounts();
        boolean isAccount = true;
        for (int i = 2; i < params.length; i++) {
            if (isAccount && allAccounts.contains(params[i])) {
                accounts.add(params[i]);
            } else {
                isAccount = false;
                paths.add(params[i]);
            }
        }
    }

    private List<String> getAccounts() {
        List<String> accountList = new ArrayList<>();
        try {
            AccountResponse response = weCrossRPC.listAccounts().send();
            List<Map<String, String>> accountInfos = response.getAccounts().getAccountInfos();
            for (Map<String, String> accountInfo : accountInfos) {
                accountList.add(accountInfo.get("name"));
            }
        } catch (Exception e) {
            logger.warn("errorl,", e);
        }
        return accountList;
    }
}
