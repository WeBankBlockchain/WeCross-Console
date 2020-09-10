package com.webank.wecross.console.rpc;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.ResourceDetail;
import com.webank.wecrosssdk.rpc.common.Resources;
import com.webank.wecrosssdk.rpc.common.account.BCOSAccount;
import com.webank.wecrosssdk.rpc.common.account.ChainAccount;
import com.webank.wecrosssdk.rpc.common.account.FabricAccount;
import com.webank.wecrosssdk.rpc.common.account.UniversalAccount;
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
            AccountResponse response = weCrossRPC.listAccount().send();
            UniversalAccount account = response.getAccount();
            accountList.add(account.getName());
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
    public void listAccount(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.listAccountsHelp();
            return;
        }
        AccountResponse response = weCrossRPC.listAccount().send();
        if (response.getErrorCode() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            UniversalAccount account = response.getAccount();
            System.out.println(account.toFormatString());
        }
        logger.info("listAccount response: {}", response);
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
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "call");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.callHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "call");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
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
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 4, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, true);
    }

    @Override
    public void sendTransaction(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "sendTransaction");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.sendTransactionHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "sendTransaction");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
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
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 4, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, false);
    }

    @Override
    public void invoke(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "invoke");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.invokeHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "invoke");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
        }

        String account = params[2];
        String method = params[3];

        TransactionResponse response;
        if (params.length == 4) {
            // no param given means: null (not String[0])
            response = weCrossRPC.invoke(path, account, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .invoke(
                                    path,
                                    account,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 4, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, false);
    }

    @Override
    public String login(String[] params) throws Exception {
        if (params.length == 1) {
            UAResponse uaResponse = weCrossRPC.login();
            if (uaResponse == null) {
                throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "login");
            } else {
                PrintUtils.printUAResponse(uaResponse);
                return uaResponse.getUAReceipt().getUniversalAccount().getName();
            }
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.loginHelp();
            return null;
        }
        if (params.length < 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "login");
        }

        String username = params[1];
        String password = params[2];
        UAResponse uaResponse = weCrossRPC.login(username, password).send();
        PrintUtils.printUAResponse(uaResponse);
        return username;
    }

    @Override
    public void registerAccount(String[] params) throws Exception {
        if(params.length==1){
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "registerAccount");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.registerHelp();
            return;
        }
        if (params.length < 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "registerAccount");
        }
        String username = params[1];
        String password = params[2];
        UAResponse uaResponse = weCrossRPC.register(username,password).send();
        PrintUtils.printUAResponse(uaResponse);
    }

    @Override
    public void addChainAccount(String[] params) throws Exception {
        if(params.length==1){
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "addChainAccount");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.addChainAccountHelp();
            return;
        }
        if (params.length < 5
                || !ConsoleUtils.supportChainList.contains(params[1])) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "addChainAccount");
        }
        if (params[1].equals(ConsoleUtils.BCOSGMType)||params[1].equals(ConsoleUtils.BCOSType)){
            String type = params[1];
            String pubKey = params[2];
            String secKey = params[3];
            boolean isDefault = Boolean.parseBoolean(params[4]);
            ChainAccount chainAccount = new BCOSAccount(type,pubKey,secKey,isDefault);
            UAResponse uaResponse = weCrossRPC.addChainAccount(type,chainAccount).send();
            PrintUtils.printUAResponse(uaResponse);
            // TODO: listAccount
        }
        if(params[1].equals(ConsoleUtils.fabricType)){
            String type = params[1];
            String cert = params[2];
            String key = params[3];
            boolean isDefault = Boolean.parseBoolean(params[4]);
            ChainAccount chainAccount = new FabricAccount(type,cert,key,isDefault);
            UAResponse uaResponse = weCrossRPC.addChainAccount(type,chainAccount).send();
            PrintUtils.printUAResponse(uaResponse);
            // TODO: listAccount
        }
    }

    @Override
    public void setDefaultAccount(String[] params) throws Exception {

    }

    @Override
    public void logout(String[] params) throws Exception {
        if (params.length == 1){
            UAResponse uaResponse = weCrossRPC.logout().send();
            PrintUtils.printUAResponse(uaResponse);
        }
        if (params.length==2 && ("-h".equals(params[1]) || "--help".equals(params[1]))) {
            HelpInfo.logoutHelp();
            return;
        }
        if(params.length > 2){
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "logout");
        }
    }
}

