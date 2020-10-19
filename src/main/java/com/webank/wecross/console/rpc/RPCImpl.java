package com.webank.wecross.console.rpc;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.ResourceDetail;
import com.webank.wecrosssdk.rpc.common.Resources;
import com.webank.wecrosssdk.rpc.common.account.BCOSAccount;
import com.webank.wecrosssdk.rpc.common.account.ChainAccount;
import com.webank.wecrosssdk.rpc.common.account.FabricAccount;
import com.webank.wecrosssdk.rpc.common.account.UniversalAccount;
import com.webank.wecrosssdk.rpc.methods.Response;
import com.webank.wecrosssdk.rpc.methods.response.*;
import java.io.Console;
import java.io.File;
import java.util.*;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCImpl implements RPCFace {

    private WeCrossRPC weCrossRPC;

    private final Logger logger = LoggerFactory.getLogger(RPCImpl.class);

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
        if (params.length > 2 || (params.length == 2 && !params[1].equals("-d"))) {
            HelpInfo.listAccountHelp();
            return;
        }
        AccountResponse response = weCrossRPC.listAccount().send();
        if (response.getErrorCode() != 0) {
            ConsoleUtils.printJson(response.toString());
        } else {
            UniversalAccount account = response.getAccount();
            if (params.length == 1) {
                System.out.println(account.toFormatString());
            } else if (params.length == 2 && params[1].equals("-d")) {
                System.out.println(account.toDetailString());
            }
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
        if (params.length < 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "call");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
        }

        String method = params[2];

        TransactionResponse response;
        if (params.length == 3) {
            // no param given means: null (not String[0])
            response = weCrossRPC.call(path, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .call(
                                    path,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 3, params.length)))
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
        if (params.length < 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "sendTransaction");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
        }

        String method = params[2];

        TransactionResponse response;
        if (params.length == 3) {
            // no param given means: null (not String[0])
            response = weCrossRPC.sendTransaction(path, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .sendTransaction(
                                    path,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 3, params.length)))
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
        if (params.length < 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "invoke");
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            throw new WeCrossConsoleException(
                    ErrorCode.INVALID_PATH, "Error: path is invalid, please check again!");
        }

        String method = params[2];

        TransactionResponse response;
        if (params.length == 3) {
            // no param given means: null (not String[0])
            response = weCrossRPC.invoke(path, method, null).send();
        } else {
            response =
                    weCrossRPC
                            .invoke(
                                    path,
                                    method,
                                    ConsoleUtils.parseArgs(
                                            Arrays.copyOfRange(params, 3, params.length)))
                            .send();
        }
        PrintUtils.printTransactionResponse(response, false);
    }

    @Override
    public void login(String[] params, LineReader lineReader) throws Exception {
        if (params.length == 1) {
            UAResponse uaResponse = weCrossRPC.login();
            // connect success but do not config TOML file
            if (uaResponse == null) {
                lineReader.getTerminal().puts(InfoCmp.Capability.clear_screen);
                lineReader.getTerminal().flush();
                Console consoleSys = System.console();
                String username = consoleSys.readLine("username: ");
                String password = new String(consoleSys.readPassword("password: "));
                UAResponse response = weCrossRPC.login(username, password).send();
                PrintUtils.printUAResponse(response);
                ConsoleUtils.runtimeUsernameThreadLocal.set(username);
            } else if (uaResponse.getErrorCode() != StatusCode.SUCCESS) {
                // config TOML file but do not login successfully
                logger.error("RPC.login fail.");
                PrintUtils.printUAResponse(uaResponse);
            } else {
                PrintUtils.printUAResponse(uaResponse);
                ConsoleUtils.runtimeUsernameThreadLocal.set(
                        uaResponse.getUAReceipt().getUniversalAccount().getUsername());
            }
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.loginHelp();
            return;
        }
        if (params.length != 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "login");
        }

        String username = params[1];
        String password = params[2];
        UAResponse uaResponse = weCrossRPC.login(username, password).send();
        PrintUtils.printUAResponse(uaResponse);
        ConsoleUtils.runtimeUsernameThreadLocal.set(username);
    }

    @Override
    public void internalLogin(LineReader lineReader) throws Exception {
        UAResponse uaResponse = weCrossRPC.login();
        // connect success but do not config TOML file
        if (uaResponse == null) {
            lineReader.getTerminal().puts(InfoCmp.Capability.clear_screen);
            lineReader.getTerminal().flush();
            System.out.println("Universal Account info has been changed, please login again.");
            Console consoleSys = System.console();
            String username = consoleSys.readLine("username: ");
            String password = new String(consoleSys.readPassword("password: "));
            weCrossRPC.login(username, password).send();
            ConsoleUtils.runtimeUsernameThreadLocal.set(username);
        } else {
            ConsoleUtils.runtimeUsernameThreadLocal.set(
                    uaResponse.getUAReceipt().getUniversalAccount().getUsername());
        }
    }

    @Override
    public void registerAccount(String[] params, LineReader lineReader) throws Exception {
        if (params.length == 1) {
            lineReader.getTerminal().puts(InfoCmp.Capability.clear_screen);
            lineReader.getTerminal().flush();
            Console consoleSys = System.console();
            String username = consoleSys.readLine("username: ");
            String password = new String(consoleSys.readPassword("password: "));
            UAResponse response = weCrossRPC.register(username, password).send();
            PrintUtils.printUAResponse(response);
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.registerHelp();
            return;
        }
        if (params.length != 3) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "registerAccount");
        }
        String username = params[1];
        String password = params[2];
        UAResponse uaResponse = weCrossRPC.register(username, password).send();
        PrintUtils.printUAResponse(uaResponse);
    }

    /**
     * add Chain Account
     *
     * @param params addChainAccount [type][pubKey][secKey][ext][default]
     * @throws Exception
     */
    @Override
    public void addChainAccount(String[] params) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "addChainAccount");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.addChainAccountHelp();
            return;
        }
        if (params.length < 6 || !ConsoleUtils.supportChainList.contains(params[1])) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "addChainAccount");
        }
        if (params[1].equals(ConsoleUtils.BCOSGMType) || params[1].equals(ConsoleUtils.BCOSType)) {
            String type = params[1];
            String pubKeyPath = params[2];
            String secKeyPath = params[3];
            String ext = params[4];
            boolean isDefault = Boolean.parseBoolean(params[5]);
            String pubKey = FileUtils.readSourceFile(pubKeyPath);
            String secKey = FileUtils.readSourceFile(secKeyPath);

            ChainAccount chainAccount = new BCOSAccount(type, pubKey, secKey, ext, isDefault);
            UAResponse uaResponse = weCrossRPC.addChainAccount(type, chainAccount).send();
            PrintUtils.printUAResponse(uaResponse);
        }
        if (params[1].equals(ConsoleUtils.fabricType)) {
            String type = params[1];
            String certPath = params[2];
            String keyPath = params[3];
            String ext = params[4];
            boolean isDefault = Boolean.parseBoolean(params[5]);

            String cert = FileUtils.readSourceFile(certPath);
            String key = FileUtils.readSourceFile(keyPath);
            ChainAccount chainAccount = new FabricAccount(type, cert, key, ext, isDefault);
            UAResponse uaResponse = weCrossRPC.addChainAccount(type, chainAccount).send();
            PrintUtils.printUAResponse(uaResponse);
        }
    }

    @Override
    public void setDefaultAccount(String[] params) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "setDefaultAccount");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.setDefaultAccountHelp();
            return;
        }
        if (params.length != 3 || !ConsoleUtils.supportChainList.contains(params[1])) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "setDefaultAccount");
        }
        String type = params[1];
        String keyID = params[2];
        if (keyID.startsWith("keyID:")) {
            keyID = keyID.substring(5);
        }
        if (!ConsoleUtils.isNumeric(keyID)) {
            throw new WeCrossConsoleException(ErrorCode.ILLEGAL_PARAM, "Invalid keyID");
        }
        UAResponse uaResponse = weCrossRPC.setDefaultAccount(type, Integer.valueOf(keyID)).send();
        PrintUtils.printUAResponse(uaResponse);
    }

    @Override
    public void logout(String[] params) throws Exception {
        if (params.length == 1) {
            UAResponse uaResponse = weCrossRPC.logout().send();
            PrintUtils.printUAResponse(uaResponse);
            ConsoleUtils.runtimeUsernameThreadLocal.remove();
        }
        if (params.length == 2 && ("-h".equals(params[1]) || "--help".equals(params[1]))) {
            HelpInfo.logoutHelp();
            return;
        }
        if (params.length > 2) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "logout");
        }
    }

    private String uniformPath(String path) {
        if (path.startsWith("/") || path.startsWith("\\") || path.startsWith(File.pathSeparator)) {
            return "file:" + path;
        } else {
            return "classpath:" + path;
        }
    }
}
