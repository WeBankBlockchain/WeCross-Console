package com.webank.wecross.console.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;
import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.Hash;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.LoginRequest;
import com.webank.wecross.console.common.LoginSalt;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.common.RSAUtility;
import com.webank.wecross.console.common.RegisterRequest;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.common.Constant;
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
import com.webank.wecrosssdk.utils.ConfigUtils;
import java.io.Console;
import java.io.File;
import java.security.PublicKey;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCImpl implements RPCFace {

    private WeCrossRPC weCrossRPC;

    private final Logger logger = LoggerFactory.getLogger(RPCImpl.class);
    private Hash hash = new Hash();

    @Override
    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    @Override
    public Set<String> getPaths() throws WeCrossConsoleException {
        Set<String> paths = new HashSet<>();
        try {
            ResourceResponse response = weCrossRPC.listResources(false).send();
            Resources resources = response.getResources();
            for (ResourceDetail resourceInfo : resources.getResourceDetails()) {
                paths.add(resourceInfo.getPath());
            }
        } catch (Exception e) {
            logger.warn("Get paths failed, error: ", e);
            throw new WeCrossConsoleException(ErrorCode.INTERNAL_ERROR, e.getMessage());
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
            Set<ResourceDetail> set = new TreeSet<>(Comparator.comparing(ResourceDetail::getPath));
            set.addAll(Arrays.asList(resourceDetails));
            for (ResourceDetail resourceDetail : set) {
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
            response = weCrossRPC.call(path, method, (String[]) null).send();
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
            response = weCrossRPC.sendTransaction(path, method, (String[]) null).send();
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
            response = weCrossRPC.invoke(path, method, (String[]) null).send();
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

    private UAResponse loginWithEncryptParams(String username, String password) throws Exception {
        PubResponse pubResponse = weCrossRPC.queryPub().send();
        AuthCodeResponse authCodeResponse = weCrossRPC.queryAuthCode().send();
        String pub = pubResponse.getData().getPub();
        AuthCodeReceipt.AuthCodeInfo authCode = authCodeResponse.getData().getAuthCode();

        String confusedPassword = hash.sha256(LoginSalt.LoginSalt + password);
        logger.info("pub: {}, token: {}", pub, authCode.getRandomToken());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(confusedPassword);
        loginRequest.setUsername(username);
        loginRequest.setRandomToken(authCode.getRandomToken());

        PublicKey publicKey = RSAUtility.createPublicKey(pub);
        ObjectMapper objectMapper = new ObjectMapper();
        String params =
                RSAUtility.encryptBase64(objectMapper.writeValueAsBytes(loginRequest), publicKey);
        UAResponse uaResponse = weCrossRPC.login(username, password, params).send();
        if (logger.isDebugEnabled()) {
            logger.debug("UAResponse: {}", uaResponse);
        }

        return uaResponse;
    }

    private UAResponse registerWithEncryptParams(String username, String password)
            throws Exception {
        PubResponse pubResponse = weCrossRPC.queryPub().send();
        AuthCodeResponse authCodeResponse = weCrossRPC.queryAuthCode().send();
        String pub = pubResponse.getData().getPub();
        AuthCodeReceipt.AuthCodeInfo authCode = authCodeResponse.getData().getAuthCode();

        String confusedPassword = hash.sha256(LoginSalt.LoginSalt + password);
        logger.info("pub: {}, token: {}", pub, authCode.getRandomToken());

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword(confusedPassword);
        registerRequest.setUsername(username);
        registerRequest.setRandomToken(authCode.getRandomToken());

        PublicKey publicKey = RSAUtility.createPublicKey(pub);
        ObjectMapper objectMapper = new ObjectMapper();
        String params =
                RSAUtility.encryptBase64(
                        objectMapper.writeValueAsBytes(registerRequest), publicKey);
        UAResponse uaResponse = weCrossRPC.register(username, password, params).send();
        if (logger.isDebugEnabled()) {
            logger.debug("UAResponse: {}", uaResponse);
        }

        return uaResponse;
    }

    private UAResponse loginWithoutArgs(WeCrossRPC weCrossRPC) throws Exception {
        Toml toml = ConfigUtils.getToml(Constant.APPLICATION_CONFIG_FILE);
        String username = toml.getString("login.username");
        String password = toml.getString("login.password");
        if (username == null || password == null) {
            logger.info(
                    "loginWithoutArgs: TOML file did not config [login] message, can not auto-login.");
            return null;
        }
        UAResponse uaResponse = loginWithEncryptParams(username, password);
        ConsoleUtils.runtimeUsernameThreadLocal.set(username);
        ConsoleUtils.runtimePasswordThreadLocal.set(password);
        return uaResponse;
    }

    @Override
    public void login(String[] params) throws Exception {
        if (params.length == 1) {
            UAResponse uaResponse = loginWithoutArgs(weCrossRPC);
            // connect success but do not config TOML file
            if (uaResponse == null) {
                Console consoleSys = System.console();
                String username = consoleSys.readLine("username: ");
                String password = new String(consoleSys.readPassword("password: "));
                UAResponse response = loginWithEncryptParams(username, password);
                PrintUtils.printUAResponse(response);
                ConsoleUtils.runtimeUsernameThreadLocal.set(username);
                ConsoleUtils.runtimePasswordThreadLocal.set(password);
            } else if (uaResponse.getErrorCode() != StatusCode.SUCCESS) {
                // config TOML file but do not login successfully
                logger.error("RPC.login fail.");
                PrintUtils.printUAResponse(uaResponse);
            } else {
                PrintUtils.printUAResponse(uaResponse);
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
        UAResponse uaResponse = loginWithEncryptParams(username, password);
        PrintUtils.printUAResponse(uaResponse);
        ConsoleUtils.runtimeUsernameThreadLocal.set(username);
        ConsoleUtils.runtimePasswordThreadLocal.set(password);
    }

    @Override
    public void internalLogin() throws Exception {
        System.out.println("Universal Account info has been changed, now auto-login again.");
        String username = ConsoleUtils.runtimeUsernameThreadLocal.get();
        String password = ConsoleUtils.runtimePasswordThreadLocal.get();
        UAResponse uaResponse = loginWithEncryptParams(username, password);
        PrintUtils.printUAResponse(uaResponse);
    }

    @Override
    public void registerAccount(String[] params) throws Exception {
        if (params.length == 1) {
            Console consoleSys = System.console();
            System.out.println(
                    "\033[31;1m"
                            + "tips: username can contain alphabet, digit and some special characters: [-_]");
            System.out.println("      and the length is in range [4,16]. \033[0m");
            String username = consoleSys.readLine("\033[32;1musername: \033[0m");
            System.out.println(
                    "\033[31;1m"
                            + "tips: password can contain alphabet, digit and some special characters: [@+!%*#?]");
            System.out.println("      and the length is in range [1,16]. \033[0m");
            String password = new String(consoleSys.readPassword("\033[32;1mpassword: \033[0m"));

            UAResponse response = registerWithEncryptParams(username, password);
            PrintUtils.printUAResponse(response);
            System.out.print(
                    "Will you save account you've just registered to conf/registerAccount.txt?(y/n)  ");
            String readIn;
            Scanner in = new Scanner(System.in);
            do {
                readIn = in.nextLine();
            } while (Objects.equals(readIn, "\t"));
            if (readIn.equals("y") || readIn.equals("Y")) {
                System.out.println("Saving to conf/registerAccount.tx ...");
                String content = "username: " + username + "\npassword: " + password;
                FileUtils.writeFile("conf/registerAccount.txt", content, true);
            }
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
        UAResponse uaResponse = registerWithEncryptParams(username, password);
        ;
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
            boolean isDefault = checkBooleanString(params[5]);
            String pubKey = FileUtils.readFileContent(pubKeyPath);
            String secKey = FileUtils.readFileContent(secKeyPath);

            ChainAccount chainAccount = new BCOSAccount(type, pubKey, secKey, ext, isDefault);
            UAResponse uaResponse = weCrossRPC.addChainAccount(type, chainAccount).send();
            PrintUtils.printUAResponse(uaResponse);
        }
        if (params[1].equals(ConsoleUtils.fabricType)) {
            String type = params[1];
            String certPath = params[2];
            String keyPath = params[3];
            String ext = params[4];
            boolean isDefault = checkBooleanString(params[5]);

            String cert = FileUtils.readFileContent(certPath);
            String key = FileUtils.readFileContent(keyPath);
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
            ConsoleUtils.runtimePasswordThreadLocal.remove();
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

    private boolean checkBooleanString(String str) throws WeCrossConsoleException {
        if (!"true".equalsIgnoreCase(str) && !"false".equalsIgnoreCase(str)) {
            throw new WeCrossConsoleException(
                    ErrorCode.ILLEGAL_PARAM,
                    "Boolean value you input is wrong, please check again.");
        }
        return Boolean.parseBoolean(str);
    }
}
