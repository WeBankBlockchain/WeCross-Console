package com.webank.wecross.console.rpc;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.ResourceDetail;
import com.webank.wecrosssdk.rpc.common.Resources;
import com.webank.wecrosssdk.rpc.methods.Response;
import com.webank.wecrosssdk.rpc.methods.response.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCImpl implements RPCFace {

    private WeCrossRPC weCrossRPC;

    private Logger logger = LoggerFactory.getLogger(RPCImpl.class);

    @Override
    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    @Override
    public Set<String> getPaths() {
        Set<String> paths = new HashSet<>();
        try {
            ResourceResponse response = weCrossRPC.listResources(false).send();
            Resources resources = response.getResources();
            for (ResourceDetail resourceInfo : resources.getResourceDetails()) {
                paths.add(resourceInfo.getPath());
            }
        } catch (Exception e) {
            logger.warn("Get paths failed when starting console,", e);
        }
        return paths;
    }

    @Override
    public Set<String> getAccounts() {
        Set<String> accountList = new HashSet<>();
        try {
            AccountResponse response = weCrossRPC.listAccounts().send();
            List<Map<String, String>> accountInfos = response.getAccounts().getAccountInfos();
            for (Map<String, String> accountInfo : accountInfos) {
                accountList.add(accountInfo.get("name"));
            }
        } catch (Exception e) {
            logger.warn("error,", e);
        }
        return accountList;
    }

    @Override
    public void supportedStubs(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.supportedStubsHelp();
            return;
        }

        StubResponse stubResponse = weCrossRPC.supportedStubs().send();
        if (stubResponse.getErrorCode() != 0) {
            ConsoleUtils.printJson(stubResponse.toString());
        } else {
            System.out.println(Arrays.toString(stubResponse.getStubs().getStubTypes()));
        }
        logger.info("supportedStubs response: {}", stubResponse);
    }

    @Override
    public void listAccounts(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.listAccountsHelp();
            return;
        }

        AccountResponse response = weCrossRPC.listAccounts().send();
        if (response.getErrorCode() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            List<Map<String, String>> accountInfos = response.getAccounts().getAccountInfos();
            for (Map<String, String> accountInfo : accountInfos) {
                System.out.println(
                        "name: " + accountInfo.get("name") + ", type: " + accountInfo.get("type"));
            }
            System.out.println("total: " + accountInfos.size());
        }
        logger.info("list acc response: {}", response);
    }

    @Override
    public void listResources(String[] params) throws Exception {
        if (params.length != 2) {
            HelpInfo.promptHelp("listResources");
            return;
        }
        String option = params[1];
        if ("-h".equals(option) || "--help".equals(option)) {
            HelpInfo.listResourcesHelp();
            return;
        }

        boolean ignoreRemote = option.equals("1");

        ResourceResponse resourcesResponse = weCrossRPC.listResources(ignoreRemote).send();
        if (resourcesResponse.getErrorCode() != 0) {
            ConsoleUtils.printJson(resourcesResponse.toString());
        } else {
            ResourceDetail[] resourceDetails =
                    resourcesResponse.getResources().getResourceDetails();
            for (ResourceDetail resourceDetail : resourceDetails) {
                System.out.println(
                        "path: "
                                + resourceDetail.getPath()
                                + ", type: "
                                + resourceDetail.getStubType()
                                + ", distance: "
                                + resourceDetail.getDistance());
            }
            System.out.println("total: " + resourceDetails.length);
        }
        logger.info("listResources response: {}", resourcesResponse);
    }

    @Override
    public void getResourceStatus(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length != 2) {
            HelpInfo.promptHelp("status");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.statusHelp();
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        Response response = weCrossRPC.status(path).send();
        if (response.getErrorCode() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            System.out.println(response.getData());
        }
        logger.info("getResourceStatus response: {}", response);
    }

    @Override
    public void getResourceInfo(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length != 2) {
            HelpInfo.promptHelp("info");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.detailHelp();
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        ResourceDetailResponse response = weCrossRPC.detail(path).send();
        if (response.getErrorCode() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            ConsoleUtils.printJson(response.getResourceDetail().toString());
        }
        logger.info("getResourceInfo response: {}", response);
    }

    @Override
    public void call(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("call");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.callHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("call");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        String account = params[2];
        String method = params[3];

        TransactionResponse response;
        if (params.length == 4) {
            // no param given means: null (not String[0])
            response = weCrossRPC.call(path, account, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .call(
                                    path,
                                    account,
                                    method,
                                    ConsoleUtils.parseAgrs(
                                            Arrays.copyOfRange(params, 4, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, true);
    }

    @Override
    public void sendTransaction(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("sendTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.sendTransactionHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("sendTransaction");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }

        String account = params[2];
        String method = params[3];

        TransactionResponse response;
        if (params.length == 4) {
            // no param given means: null (not String[0])
            response = weCrossRPC.sendTransaction(path, account, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .sendTransaction(
                                    path,
                                    account,
                                    method,
                                    ConsoleUtils.parseAgrs(
                                            Arrays.copyOfRange(params, 4, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, false);
    }
}
