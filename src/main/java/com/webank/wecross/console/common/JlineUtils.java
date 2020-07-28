package com.webank.wecross.console.common;

import java.io.IOException;
import java.util.*;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Attributes;
import org.jline.terminal.Attributes.ControlChar;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class JlineUtils {

    private static Set<String> paths = new HashSet<>();
    private static Set<String> accounts = new HashSet<>();
    private static Set<String> pathVars = new HashSet<>();
    private static Set<String> resourceVars = new HashSet<>();

    private static List<String> pathVarSupportedCommands =
            Arrays.asList(
                    "status",
                    "detail",
                    "call",
                    "sendTransaction",
                    "newHTLCProposal",
                    "checkTransferStatus",
                    "callTransaction",
                    "execTransaction");

    private static List<String> pathVarNotSupportedCommands =
            Arrays.asList(
                    "bcosDeploy",
                    "bcosRegister",
                    "fabricInstall",
                    "fabricInstantiate",
                    "getTransactionIDs");

    private static List<String> allCommands =
            Arrays.asList(
                    "help",
                    "quit",
                    "supportedStubs",
                    "listAccounts",
                    "listLocalResources",
                    "listResources",
                    "status",
                    "detail",
                    "call",
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
                    "fabricInstantiate");

    public static void addCommandCompleters(List<Completer> completers) {
        // commands
        for (String command : allCommands) {
            completers.add(
                    new ArgumentCompleter(
                            new IgnoreCaseCompleter(command), new StringsCompleter()));
        }
    }

    public static List<Completer> getCompleters(
            Set<String> paths,
            Set<String> accounts,
            Set<String> resourceVars,
            Set<String> pathVars) {
        JlineUtils.paths.addAll(paths);
        JlineUtils.accounts.addAll(accounts);
        JlineUtils.pathVars.addAll(pathVars);
        JlineUtils.resourceVars.addAll(resourceVars);

        List<Completer> completers = new ArrayList<>();

        addCommandCompleters(completers);
        addPathsCompleters(completers, paths);
        addVarsCompleters(completers, resourceVars, pathVars);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"));
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);

        return completers;
    }

    public static void updateCompleters(
            List<Completer> completers,
            Set<String> paths,
            Set<String> accounts,
            Set<String> resourceVars,
            Set<String> pathVars) {
        if (!completers.isEmpty()) {
            completers.clear();
        }

        JlineUtils.paths.addAll(paths);
        JlineUtils.accounts.addAll(accounts);
        JlineUtils.pathVars.addAll(pathVars);
        JlineUtils.resourceVars.addAll(resourceVars);

        addCommandCompleters(completers);
        addPathsCompleters(completers, paths);
        addVarsCompleters(completers, resourceVars, pathVars);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"));
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);
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

    public static void updateAccountsCompleters(List<Completer> completers, Set<String> accounts) {
        if (!completers.isEmpty()) {
            completers.clear();
        }
        if (!JlineUtils.accounts.isEmpty()) {
            JlineUtils.accounts.clear();
        }
        JlineUtils.accounts = accounts;

        addCommandCompleters(completers);
        addPathsCompleters(completers, paths);
        addVarsCompleters(completers, resourceVars, pathVars);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"));
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);
    }

    public static void addPathCompleters(List<Completer> completers, String path) {
        JlineUtils.paths.add(path);

        ArgumentCompleter argumentCompleter1 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter(path),
                        new StringsCompleter());
        argumentCompleter1.setStrict(false);
        completers.add(argumentCompleter1);

        ArgumentCompleter argumentCompleter2 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"),
                        new StringsCompleter(path),
                        new StringsCompleter(accounts),
                        new StringsCompleter());
        argumentCompleter2.setStrict(false);
        completers.add(argumentCompleter2);

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarSupportedCommands),
                        new StringsCompleter(path),
                        new StringsCompleter(accounts),
                        new StringsCompleter()));

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarNotSupportedCommands),
                        new StringsCompleter(path),
                        new StringsCompleter(accounts),
                        new StringsCompleter()));
    }

    public static void addResourceVarCompleters(List<Completer> completers, String resourceVar) {
        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".call"), new StringsCompleter()));
        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(resourceVar + ".sendTransaction"),
                        new StringsCompleter()));

        completers.add(new ArgumentCompleter(new StringsCompleter(resourceVar + ".status")));
        completers.add(new ArgumentCompleter(new StringsCompleter(resourceVar + ".detail")));
    }

    public static void addPathVarCompleters(List<Completer> completers, String pathVar) {
        ArgumentCompleter argumentCompleter2 =
                new ArgumentCompleter(
                        new StringsCompleter(""),
                        new StringsCompleter("="),
                        new StringsCompleter("WeCross.getResource"),
                        new StringsCompleter(pathVar),
                        new StringsCompleter(accounts),
                        new StringsCompleter());
        argumentCompleter2.setStrict(false);
        completers.add(argumentCompleter2);

        completers.add(
                new ArgumentCompleter(
                        new StringsCompleter(pathVarSupportedCommands),
                        new StringsCompleter(pathVar),
                        new StringsCompleter(accounts),
                        new StringsCompleter()));
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
