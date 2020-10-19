package com.webank.wecross.console.common;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.jline.builtins.Completers.DirectoriesCompleter;
import org.jline.builtins.Completers.FilesCompleter;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Attributes;
import org.jline.terminal.Attributes.ControlChar;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class JlineUtils {

    private static Set<String> paths = new HashSet<>();
    private static Set<String> pathVars = new HashSet<>();
    private static Set<String> resourceVars = new HashSet<>();
    private static Set<String> contractMethods = new HashSet<>();
    private static Set<String> orgs = new HashSet<>();
    private static ArgumentCompleter commitCompleter = new ArgumentCompleter();
    private static ArgumentCompleter rollbackCompleter = new ArgumentCompleter();
    private static final List<String> pathVarSupportedCommands =
            Arrays.asList(
                    "status",
                    "detail",
                    "call",
                    "sendTransaction",
                    "invoke",
                    "newHTLCProposal",
                    "checkTransferStatus",
                    "callTransaction",
                    "execTransaction");

    private static final List<String> callContractCommands =
            Arrays.asList("call", "sendTransaction");

    private static final List<String> pathVarNotSupportedCommands =
            Arrays.asList(
                    "bcosDeploy",
                    "bcosRegister",
                    "fabricInstall",
                    "fabricInstantiate",
                    "fabricUpgrade",
                    "getTransactionIDs",
                    "login",
                    "registerAccount",
                    "logout",
                    "addChainAccount",
                    "setDefaultAccount");

    private static final List<String> fabricCommands =
            Arrays.asList("fabricInstall", "fabricInstantiate", "fabricUpgrade");

    private static final List<String> bcosCommands = Arrays.asList("bcosDeploy", "bcosRegister");

    private static final List<String> allCommands =
            Arrays.asList(
                    "help",
                    "quit",
                    "supportedStubs",
                    "listAccount",
                    "listLocalResources",
                    "listResources",
                    "status",
                    "detail",
                    "call",
                    "invoke",
                    "sendTransaction",
                    "newHTLCProposal",
                    "genTimelock",
                    "checkTransferStatus",
                    "genSecretAndHash",
                    "callTransaction",
                    "execTransaction",
                    "startTransaction",
                    "commitTransaction",
                    "rollbackTransaction",
                    "getTransactionInfo",
                    "getTransactionIDs",
                    "bcosDeploy",
                    "bcosRegister",
                    "fabricInstall",
                    "fabricInstantiate",
                    "fabricUpgrade",
                    "login",
                    "registerAccount",
                    "logout",
                    "addChainAccount",
                    "setDefaultAccount");

    public static void addCommandCompleters(List<Completer> completers) {
        // commands
        for (String command : allCommands) {
            completers.add(
                    new ArgumentCompleter(
                            new IgnoreCaseCompleter(command), NullCompleter.INSTANCE));
        }
    }

    public static List<Completer> getNoAuthCompleters() {
        List<Completer> completers = new ArrayList<>();
        addCommandCompleters(completers);
        return completers;
    }

    public static void updateCompleters(
            List<Completer> completers,
            Set<String> paths,
            Set<String> resourceVars,
            Set<String> pathVars) {
        if (!completers.isEmpty()) {
            completers.clear();
        }

        JlineUtils.paths.addAll(paths);
        JlineUtils.pathVars.addAll(pathVars);
        JlineUtils.resourceVars.addAll(resourceVars);

        addCommandCompleters(completers);
        addPathsCompleters(completers, paths);
        addVarsCompleters(completers, resourceVars, pathVars);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"),
                        NullCompleter.INSTANCE);
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);

        ArgumentCompleter addChainAccountCompleter =
                new ArgumentCompleter(
                        new StringsCompleter("addChainAccount"),
                        new StringsCompleter(ConsoleUtils.supportChainList),
                        new FilesCompleter(Paths.get(System.getProperty("user.dir"), "conf"), true),
                        new FilesCompleter(Paths.get(System.getProperty("user.dir"), "conf"), true),
                        new StringsCompleter(Arrays.asList("true", "false")),
                        NullCompleter.INSTANCE);
        completers.add(addChainAccountCompleter);

        ArgumentCompleter setDefaultAccountCompleter =
                new ArgumentCompleter(
                        new StringsCompleter("setDefaultAccount"),
                        new StringsCompleter(ConsoleUtils.supportChainList),
                        NullCompleter.INSTANCE);
        completers.add(setDefaultAccountCompleter);
    }

    public static void addPathsCompleters(List<Completer> completers, Set<String> paths) {
        for (String path : paths) {
            addPathCompleters(completers, path);
        }
    }

    public static void addVarsCompleters(
            List<Completer> completers, Set<String> resourceVars, Set<String> pathVars) {

        for (String var : resourceVars) {
            addResourceVarCompleters(completers, var);
        }

        for (String var : pathVars) {
            addPathVarCompleters(completers, var);
        }
    }

    public static void updatePathsCompleters(List<Completer> completers, Set<String> paths) {
        for (String path : paths) {
            if (!JlineUtils.paths.contains(path)) {
                addPathCompleters(completers, path);
            }
        }
    }

    public static void addTransactionInfoCompleters(List<Completer> completers) {
        if (ConsoleUtils.runtimeTransactionThreadLocal.get() != null) {
            String runtimeTransaction = ConsoleUtils.runtimeTransactionThreadLocal.get().toString();
            commitCompleter =
                    new ArgumentCompleter(
                            new StringsCompleter("commitTransaction"),
                            new StringsCompleter(runtimeTransaction),
                            NullCompleter.INSTANCE);
            rollbackCompleter =
                    new ArgumentCompleter(
                            new StringsCompleter("rollbackTransaction"),
                            new StringsCompleter(runtimeTransaction),
                            NullCompleter.INSTANCE);
            completers.add(rollbackCompleter);
            completers.add(commitCompleter);
        }
    }

    public static void removeTransactionInfoCompleters(List<Completer> completers) {
        if (ConsoleUtils.runtimeTransactionThreadLocal.get() != null) {
            completers.removeIf(completer -> completer.equals(commitCompleter));
            completers.removeIf(completer -> completer.equals(rollbackCompleter));
        }
    }

    public static void addPathCompleters(List<Completer> completers, String path) {
        JlineUtils.paths.add(path);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(),
                        new StringsCompleter("="),
                        new StringsCompleter(path),
                        NullCompleter.INSTANCE);
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);

        ArgumentCompleter argumentCompleter2 =
                new ArgumentCompleter(
                        new StringsCompleter(),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"),
                        new StringsCompleter(path),
                        NullCompleter.INSTANCE);
        argumentCompleter2.setStrict(false);
        completers.add(argumentCompleter2);

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarSupportedCommands),
                        new StringsCompleter(path),
                        NullCompleter.INSTANCE));

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarNotSupportedCommands),
                        new StringsCompleter(path),
                        NullCompleter.INSTANCE));

        ArgumentCompleter bcosArgumentCompleter =
                new ArgumentCompleter(
                        new StringsCompleter(bcosCommands),
                        new StringsCompleter(path),
                        new FilesCompleter(Paths.get(System.getProperty("user.dir"), "conf")),
                        NullCompleter.INSTANCE);
        completers.add(bcosArgumentCompleter);

        ArgumentCompleter fabricArgumentCompleter =
                new ArgumentCompleter(
                        new StringsCompleter(fabricCommands),
                        new StringsCompleter(path),
                        new StringsCompleter(orgs),
                        new DirectoriesCompleter(
                                Paths.get(System.getProperty("user.dir"), "conf"), true),
                        new StringsCompleter(),
                        new StringsCompleter("GO_LANG", "JAVA"),
                        NullCompleter.INSTANCE);
        fabricArgumentCompleter.setStrict(false);
        completers.add(fabricArgumentCompleter);
    }

    public static void addResourceVarCompleters(List<Completer> completers, String resourceVar) {
        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".call"), NullCompleter.INSTANCE));
        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".sendTransaction"),
                        NullCompleter.INSTANCE));

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".status"), NullCompleter.INSTANCE));
        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".detail"), NullCompleter.INSTANCE));
    }

    public static void addPathVarCompleters(List<Completer> completers, String pathVar) {
        ArgumentCompleter argumentCompleter2 =
                new ArgumentCompleter(
                        new StringsCompleter(),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"),
                        new StringsCompleter(pathVar),
                        NullCompleter.INSTANCE);
        argumentCompleter2.setStrict(false);
        completers.add(argumentCompleter2);

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarSupportedCommands),
                        new StringsCompleter(pathVar),
                        NullCompleter.INSTANCE));
    }

    public static void addOrgCompleters(List<Completer> completers, String org) {
        if (!JlineUtils.orgs.contains(org)) {
            JlineUtils.orgs.add(org);
            completers.add(
                    new ArgumentCompleter(
                            new StringsCompleter(fabricCommands),
                            new StringsCompleter(paths),
                            new StringsCompleter(org),
                            new DirectoriesCompleter(
                                    Paths.get(System.getProperty("user.dir"), "conf"), true),
                            new StringsCompleter(contractMethods),
                            NullCompleter.INSTANCE));
        }
    }

    public static void addContractMethodCompleters(
            List<Completer> completers, String contractMethod) {
        if (!JlineUtils.contractMethods.contains(contractMethod)) {
            JlineUtils.contractMethods.add(contractMethod);
            completers.add(
                    new ArgumentCompleter(
                            new StringsCompleter(callContractCommands),
                            new StringsCompleter(paths),
                            new StringsCompleter(contractMethod),
                            NullCompleter.INSTANCE));
        }
    }

    public static LineReader getLineReader(List<Completer> completers) throws IOException {

        Terminal terminal =
                TerminalBuilder.builder()
                        .nativeSignals(true)
                        .signalHandler(Terminal.SignalHandler.SIG_IGN)
                        .build();
        Attributes termAttribs = terminal.getAttributes();

        // disable CTRL+D shortcut to exit
        // disable CTRL+C shortcut
        termAttribs.setControlChar(ControlChar.VEOF, 0);
        termAttribs.setControlChar(ControlChar.VINTR, 0);

        terminal.setAttributes(termAttribs);
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new AggregateCompleter(completers))
                .build()
                .option(LineReader.Option.HISTORY_IGNORE_SPACE, false)
                .option(LineReader.Option.HISTORY_REDUCE_BLANKS, false);
    }
}
