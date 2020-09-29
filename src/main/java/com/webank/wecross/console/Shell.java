package com.webank.wecross.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.webank.wecross.console.common.*;
import com.webank.wecross.console.custom.BCOSCommand;
import com.webank.wecross.console.custom.FabricCommand;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.mock.MockWeCross;
import com.webank.wecross.console.routine.HTLCFace;
import com.webank.wecross.console.routine.TwoPcFace;
import com.webank.wecross.console.rpc.RPCFace;
import com.webank.wecrosssdk.utils.RPCUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.*;
import org.jline.keymap.KeyMap;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shell {
    private static Logger logger = LoggerFactory.getLogger(Shell.class);

    private static RPCFace rpcFace;
    private static HTLCFace htlcFace;
    private static TwoPcFace twoPcFace;
    private static BCOSCommand bcosCommand;
    private static FabricCommand fabricCommand;
    private static String loginUser;
    private static String runtimeTransaction;

    public static void main(String[] args) {

        LineReader lineReader = null;
        GroovyShell groovyShell = null;
        MockWeCross mockWeCross;
        ObjectMapper mapper = null;
        Set<String> resourceVars = new HashSet<>();
        Set<String> pathVars = new HashSet<>();
        Map<String, String> pathMaps = new HashMap<>();
        Initializer initializer = new Initializer();
        List<Completer> completers = null;

        try {
            initializer.init();
            rpcFace = initializer.getRpcFace();
            htlcFace = initializer.getHtlcFace();
            twoPcFace = initializer.getTwoPcFace();
            bcosCommand = initializer.getBcosCommand();
            fabricCommand = initializer.getFabricCommand();
        } catch (WeCrossConsoleException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        try {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            Binding binding = new Binding();
            groovyShell = new GroovyShell(binding);
            mockWeCross = new MockWeCross(initializer.getWeCrossRPC());
            groovyShell.setProperty("WeCross", mockWeCross);

            completers = JlineUtils.getCompleters(rpcFace.getPaths(), resourceVars, pathVars);
            FileUtils.loadTransactionLog(completers);
            if (ConsoleUtils.runtimeTransactionThreadLocal.get() != null) {
                runtimeTransaction =
                        ConsoleUtils.runtimeTransactionThreadLocal.get().getTransactionID();
            }
            lineReader = JlineUtils.getLineReader(completers);

            KeyMap<org.jline.reader.Binding> keymap = lineReader.getKeyMaps().get(LineReader.MAIN);
            keymap.bind(new Reference("beginning-of-line"), "\033[1~");
            keymap.bind(new Reference("end-of-line"), "\033[4~");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        WelcomeInfo.welcome();

        while (true) {
            logger.info(Arrays.toString(args));
            try {
                String prompt =
                        (runtimeTransaction == null)
                                ? ""
                                : "[Transaction running: " + runtimeTransaction + "]\n";
                prompt += (loginUser == null) ? "[WeCross]> " : "[WeCross." + loginUser + "]>";
                String request = lineReader.readLine(prompt);

                String[] params;
                params = ConsoleUtils.tokenizeCommand(request);
                if (params.length < 1) {
                    System.out.println();
                    continue;
                }
                if (params[0].trim().equals("")) {
                    System.out.println();
                    continue;
                }
                if ("quit".equals(params[0])
                        || "q".equals(params[0])
                        || "exit".equals(params[0])
                        || "e".equals(params[0])) {
                    if (HelpInfo.promptNoParams(params, "q")) {
                        continue;
                    } else if (params.length > 2) {
                        HelpInfo.promptHelp("q");
                        continue;
                    }
                    break;
                }
                switch (params[0]) {
                    case "h":
                    case "help":
                        {
                            WelcomeInfo.help(params);
                            break;
                        }
                    case "supportedStubs":
                        {
                            rpcFace.supportedStubs(params);
                            break;
                        }
                    case "listAccount":
                        {
                            rpcFace.listAccount(params);
                            break;
                        }
                    case "listLocalResources":
                        {
                            if (HelpInfo.promptNoParams(params, "listLocalResources")) {
                                continue;
                            }
                            String[] listParams = {"listResources", "1"};
                            rpcFace.listResources(listParams);
                            JlineUtils.updatePathsCompleters(completers, rpcFace.getPaths());
                            break;
                        }
                    case "listResources":
                        {
                            if (HelpInfo.promptNoParams(params, "listResources")) {
                                continue;
                            }
                            String[] listParams = {"listResources", "0"};
                            rpcFace.listResources(listParams);
                            JlineUtils.updatePathsCompleters(completers, rpcFace.getPaths());
                            break;
                        }
                    case "status":
                        {
                            rpcFace.getResourceStatus(params, pathMaps);
                            break;
                        }
                    case "detail":
                        {
                            rpcFace.getResourceInfo(params, pathMaps);
                            break;
                        }
                    case "call":
                        {
                            rpcFace.call(params, pathMaps);
                            if (params.length >= 4) {
                                JlineUtils.addContractMethodCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "send":
                    case "sendTransaction":
                        {
                            rpcFace.sendTransaction(params, pathMaps);
                            if (params.length >= 4) {
                                JlineUtils.addContractMethodCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "invoke":
                        {
                            rpcFace.invoke(params, pathMaps);
                            if (params.length >= 4) {
                                JlineUtils.addContractMethodCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "genTimelock":
                        {
                            htlcFace.genTimelock(params);
                            break;
                        }
                    case "genSecretAndHash":
                        {
                            htlcFace.genSecretAndHash(params);
                            break;
                        }
                    case "newHTLCProposal":
                        {
                            htlcFace.newProposal(params, pathMaps);
                            break;
                        }
                    case "checkTransferStatus":
                        {
                            htlcFace.checkTransferStatus(params, pathMaps);
                            break;
                        }
                    case "callTransaction":
                        {
                            twoPcFace.callTransaction(params, pathMaps);
                            break;
                        }
                    case "execTransaction":
                        {
                            twoPcFace.execTransaction(params, pathMaps);
                            break;
                        }
                    case "startTransaction":
                        {
                            twoPcFace.startTransaction(params);
                            if (params.length >= 4) {
                                runtimeTransaction =
                                        ConsoleUtils.runtimeTransactionThreadLocal
                                                .get()
                                                .getTransactionID();
                                JlineUtils.addTransactionInfoCompleters(completers);
                            }
                            break;
                        }
                    case "commitTransaction":
                        {
                            // only support one console do one transaction
                            twoPcFace.commitTransaction(params);
                            if (ConsoleUtils.runtimeTransactionThreadLocal.get() != null
                                    && params.length != 2) {
                                runtimeTransaction = null;
                                JlineUtils.removeTransactionInfoCompleters(completers);
                                ConsoleUtils.runtimeTransactionThreadLocal.remove();
                            }
                            break;
                        }
                    case "rollbackTransaction":
                        {
                            // only support one console do one transaction
                            twoPcFace.rollbackTransaction(params);
                            if (ConsoleUtils.runtimeTransactionThreadLocal != null
                                    && params.length != 2) {
                                runtimeTransaction = null;
                                JlineUtils.removeTransactionInfoCompleters(completers);
                                ConsoleUtils.runtimeTransactionThreadLocal.remove();
                            }
                            break;
                        }
                    case "getTransactionInfo":
                        {
                            twoPcFace.getTransactionInfo(params);
                            break;
                        }
                    case "getTransactionIDs":
                        {
                            twoPcFace.getTransactionIDs(params);
                            break;
                        }
                    case "bcosDeploy":
                        {
                            bcosCommand.deploy(params);
                            if (params.length >= 6 && isPath(params[1])) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                            }
                            break;
                        }
                    case "bcosRegister":
                        {
                            bcosCommand.register(params);
                            if (params.length >= 6 && isPath(params[1])) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                            }
                            break;
                        }
                    case "fabricInstall":
                        {
                            fabricCommand.install(params);
                            if (params.length == 7 && isPath(params[1])) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                                JlineUtils.addOrgCompleters(completers, params[2]);
                            }
                            break;
                        }
                    case "fabricInstantiate":
                        {
                            fabricCommand.instantiate(params);
                            if (params.length == 9) {
                                JlineUtils.addOrgCompleters(completers, params[2]);
                            }

                            break;
                        }
                    case "fabricUpgrade":
                        {
                            fabricCommand.upgrade(params);
                            if (params.length == 9) {
                                JlineUtils.addOrgCompleters(completers, params[2]);
                            }
                            break;
                        }
                    case "login":
                        {
                            rpcFace.login(params, lineReader);
                            loginUser = ConsoleUtils.runtimeUsernameThreadLocal.get();
                            break;
                        }
                    case "registerAccount":
                        {
                            rpcFace.registerAccount(params, lineReader);
                            break;
                        }
                    case "addChainAccount":
                        {
                            rpcFace.addChainAccount(params);
                            if (params.length == 6) {

                                rpcFace.internalLogin(lineReader);
                                loginUser = ConsoleUtils.runtimeUsernameThreadLocal.get();
                            }
                            break;
                        }
                    case "setDefaultAccount":
                        {
                            rpcFace.setDefaultAccount(params);
                            if (params.length == 3) {
                                rpcFace.internalLogin(lineReader);
                                loginUser = ConsoleUtils.runtimeUsernameThreadLocal.get();
                            }
                            break;
                        }
                    case "logout":
                        {
                            rpcFace.logout(params);
                            loginUser = ConsoleUtils.runtimeUsernameThreadLocal.get();
                            break;
                        }
                    default:
                        {
                            try {
                                List<String> newResourceVars = new ArrayList<>();
                                List<String> newPathVars = new ArrayList<>();
                                if (ConsoleUtils.parseVars(
                                        params, newResourceVars, newPathVars, pathMaps)) {
                                    if (!newResourceVars.isEmpty()) {
                                        JlineUtils.addResourceVarCompleters(
                                                completers, newResourceVars.get(0));
                                    }
                                    if (!newPathVars.isEmpty()) {
                                        JlineUtils.addPathVarCompleters(
                                                completers, newPathVars.get(0));
                                    }
                                }
                                logger.info("Origin command: {}", Arrays.toString(params));
                                String newCommand = ConsoleUtils.parseCommand(params);
                                logger.info("Groovy command: {}", newCommand);
                                mapper.writeValueAsString(groovyShell.evaluate(newCommand));
                            } catch (WeCrossConsoleException e) {
                                System.out.println(e.getMessage());
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                System.out.println("Error: unsupported command");
                            }
                            break;
                        }
                }
                System.out.println();
            } catch (WeCrossConsoleException e) {
                if (e.getErrorCode() == ErrorCode.PARAM_MISSING) {
                    HelpInfo.promptHelp(e.getMessage());
                } else {
                    logger.info("Exception: ", e);
                    System.out.println(e.getMessage());
                }
            } catch (Exception e) {
                logger.info("Exception: ", e);
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
        System.exit(0);
    }

    private static boolean isPath(String path) {
        try {
            RPCUtils.checkPath(path);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
