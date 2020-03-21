package com.webank.wecross.console.rpc;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.ResourceDetail;
import com.webank.wecrosssdk.rpc.common.Resources;
import com.webank.wecrosssdk.rpc.methods.Response;
import com.webank.wecrosssdk.rpc.methods.response.AccountResponse;
import com.webank.wecrosssdk.rpc.methods.response.ResourceDetailResponse;
import com.webank.wecrosssdk.rpc.methods.response.ResourceResponse;
import com.webank.wecrosssdk.rpc.methods.response.StubResponse;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        try {
            ResourceResponse response = weCrossRPC.listResources(false).send();
            Resources resources = response.getResources();
            for (ResourceDetail resourceInfo : resources.getResourceDetails()) {
                paths.add(resourceInfo.getPath());
            }
        } catch (Exception e) {
            logger.warn(
                    "Get paths failed when starting console: {}, exception: {}", e.getMessage(), e);
        }
        return paths;
    }

    @Override
    public void supportedStubs(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.promptHelp("supportedStubs");
            return;
        }

        StubResponse stubResponse = weCrossRPC.supportedStubs().send();
        if (stubResponse.getResult() != 0) {
            ConsoleUtils.printJson(stubResponse.toString());
        } else {
            ConsoleUtils.printJson(stubResponse.getStubs().toString());
        }
        logger.info("supportedStubs response: {}", stubResponse);
    }

    @Override
    public void listAccounts(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.promptHelp("listAccounts");
            return;
        }

        AccountResponse response = weCrossRPC.listAccounts().send();
        if (response.getResult() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            ConsoleUtils.printJson(Arrays.toString(response.getAccounts().getAccountInfos()));
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
        if (resourcesResponse.getResult() != 0) {
            ConsoleUtils.printJson(resourcesResponse.toString());
        } else {
            ConsoleUtils.printJson(
                    Arrays.toString(resourcesResponse.getResources().getResourceDetails()));
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

        String path = parsePath(params, pathMaps);
        if (path == null) return;

        Response response = weCrossRPC.status(path).send();
        if (response.getResult() != 0) {
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

        String path = parsePath(params, pathMaps);
        if (path == null) return;

        ResourceDetailResponse response = weCrossRPC.detail(path).send();
        if (response.getResult() != 0) {
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

        String path = parsePath(params, pathMaps);
        if (path == null) return;

        String accountName = params[2];
        String method = params[3];

        TransactionResponse response;
        if (params.length == 4) {
            response = weCrossRPC.call(path, accountName, method).send();
        } else {
            response =
                    weCrossRPC
                            .call(
                                    path,
                                    accountName,
                                    method,
                                    Arrays.copyOfRange(params, 4, params.length))
                            .send();
        }
        ConsoleUtils.printTransactionResponse(response);
    }

    @Override
    public void sendTransaction(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("sendTransaction");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.callHelp();
            return;
        }
        if (params.length < 4) {
            HelpInfo.promptHelp("sendTransaction");
            return;
        }

        String path = parsePath(params, pathMaps);
        if (path == null) return;

        String accountName = params[2];
        String method = params[3];

        TransactionResponse response;
        if (params.length == 4) {
            response = weCrossRPC.sendTransaction(path, accountName, method).send();
        } else {
            response =
                    weCrossRPC
                            .sendTransaction(
                                    path,
                                    accountName,
                                    method,
                                    Arrays.copyOfRange(params, 4, params.length))
                            .send();
        }
        ConsoleUtils.printTransactionResponse(response);
    }

    private String parsePath(String[] params, Map<String, String> pathMaps) {
        String path = ConsoleUtils.parseString(params[1]);
        if (!ConsoleUtils.isValidPath(path)) {
            if (!ConsoleUtils.isValidPathVar(params[1], pathMaps)) {
                System.out.println("Please provide a valid path");
                HelpInfo.statusHelp();
                return null;
            }
            path = pathMaps.get(params[1]);
        }
        return path;
    }
}
