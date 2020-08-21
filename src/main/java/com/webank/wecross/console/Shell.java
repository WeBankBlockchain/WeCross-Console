package com.webank.wecross.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.webank.wecross.console.common.*;
import com.webank.wecross.console.custom.BCOSCommand;
import com.webank.wecross.console.custom.FabricCommand;
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

    public static void main(String[] args) {

        LineReader lineReader;
        GroovyShell groovyShell;
        MockWeCross mockWeCross;
        ObjectMapper mapper;
        Set<String> resourceVars = new HashSet<>();
        Set<String> pathVars = new HashSet<>();
        Map<String, String> pathMaps = new HashMap<>();
        Initializer initializer = new Initializer();
        List<Completer> completers;

        try {
            initializer.init();
            rpcFace = initializer.getRpcFace();
            htlcFace = initializer.getHtlcFace();
            twoPcFace = initializer.getTwoPcFace();
            bcosCommand = initializer.getBcosCommand();
            fabricCommand = initializer.getFabricCommand();
        } catch (WeCrossConsoleException e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            Binding binding = new Binding();
            groovyShell = new GroovyShell(binding);
            mockWeCross = new MockWeCross(initializer.getWeCrossRPC());
            groovyShell.setProperty("WeCross", mockWeCross);

            completers =
                    JlineUtils.getCompleters(
                            rpcFace.getPaths(), rpcFace.getAccounts(), resourceVars, pathVars);
            lineReader = JlineUtils.getLineReader(completers);

            KeyMap<org.jline.reader.Binding> keymap = lineReader.getKeyMaps().get(LineReader.MAIN);
            keymap.bind(new Reference("beginning-of-line"), "\033[1~");
            keymap.bind(new Reference("end-of-line"), "\033[4~");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        WelcomeInfo.welcome();

        while (true) {
            logger.info(Arrays.toString(args));
            try {

                String prompt = "[WeCross]> ";
                String request = lineReader.readLine(prompt);

                String[] params;
                params = ConsoleUtils.tokenizeCommand(request);
                if (params.length < 1) {
                    System.out.print("");
                    continue;
                }
                if ("".equals(params[0].trim())) {
                    System.out.print("");
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
                    case "listAccounts":
                        {
                            rpcFace.listAccounts(params);
                            JlineUtils.updateAccountsCompleters(completers, rpcFace.getAccounts());
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
                            if (rpcFace.call(params, pathMaps) == StatusCode.SUCCESS) {
                                JlineUtils.addContractMethodCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "send":
                    case "sendTransaction":
                        {
                            if (rpcFace.sendTransaction(params, pathMaps) == StatusCode.SUCCESS) {
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
                            if (twoPcFace.startTransaction(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                String[] paramsArgs = new String[params.length - 1];
                                System.arraycopy(params, 1, paramsArgs, 0, params.length - 1);
                                JlineUtils.addTransactionInfoCompleters(
                                        completers,
                                        ConsoleUtils.jointArgsToStringWithSpace(paramsArgs));
                            }
                            break;
                        }
                    case "commitTransaction":
                        {
                            if (twoPcFace.commitTransaction(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                String[] paramsArgs = new String[params.length - 1];
                                System.arraycopy(params, 1, paramsArgs, 0, params.length - 1);
                                JlineUtils.removeTransactionInfoCompleters(
                                        completers,
                                        ConsoleUtils.jointArgsToStringWithSpace(paramsArgs));
                            }
                            break;
                        }
                    case "rollbackTransaction":
                        {
                            if (twoPcFace.rollbackTransaction(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                String[] paramsArgs = new String[params.length - 1];
                                System.arraycopy(params, 1, paramsArgs, 0, params.length - 1);
                                JlineUtils.removeTransactionInfoCompleters(
                                        completers,
                                        ConsoleUtils.jointArgsToStringWithSpace(paramsArgs));
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
                            if (bcosCommand.deploy(params) == StatusCode.SUCCESS
                                    && params.length > 2) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                            }
                            break;
                        }
                    case "bcosRegister":
                        {
                            if (bcosCommand.register(params) == StatusCode.SUCCESS
                                    && params.length > 2) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                            }
                            break;
                        }
                    case "fabricInstall":
                        {
                            if (fabricCommand.install(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                JlineUtils.addPathCompleters(completers, params[1]);
                                JlineUtils.addOrgCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "fabricInstantiate":
                        {
                            if (fabricCommand.instantiate(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                JlineUtils.addOrgCompleters(completers, params[3]);
                            }
                            break;
                        }
                    case "fabricUpgrade":
                        {
                            if (fabricCommand.upgrade(params) == StatusCode.SUCCESS
                                    && params.length >= 4) {
                                JlineUtils.addOrgCompleters(completers, params[3]);
                            }
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
                                System.out.println("Error: unsupported command");
                            }
                            break;
                        }
                }
                System.out.println();
            } catch (Exception e) {
                logger.info("Exception: ", e);
                System.out.println("Error: " + e.getMessage());
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
