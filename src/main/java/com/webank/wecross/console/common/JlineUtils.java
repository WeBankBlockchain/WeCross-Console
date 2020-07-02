package com.webank.wecross.console.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

    private static List<String> resourceCommands =
            Arrays.asList(
                    "status",
                    "detail",
                    "call",
                    "sendTransaction",
                    "newHTLCProposal",
                    "checkTransferStatus");

    public static List<Completer> getCompleters(
            List<String> paths,
            List<String> accounts,
            Set<String> resourceVars,
            Set<String> pathVars) {

        List<Completer> completers = new ArrayList<>();

        addCommandCompleters(completers);
        addPathCompleters(completers, paths, accounts);
        addVarCompleters(completers, resourceVars, pathVars, accounts);

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
            List<String> paths,
            List<String> accounts,
            Set<String> resourceVars,
            Set<String> pathVars) {
        if (!completers.isEmpty()) {
            completers.clear();
        }

        addCommandCompleters(completers);
        addPathCompleters(completers, paths, accounts);
        addVarCompleters(completers, resourceVars, pathVars, accounts);
    }

    public static void addCommandCompleters(List<Completer> completers) {
        // commands
        List<String> commands =
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
                        "bcosDeploy",
                        "bcosRegister",
                        "fabricInstall",
                        "fabricInstantiate");

        for (String command : commands) {
            completers.add(
                    new ArgumentCompleter(
                            new IgnoreCaseCompleter(command), new StringsCompleter()));
        }
    }

    public static void addPathCompleters(
            List<Completer> completers, List<String> paths, List<String> accounts) {

        for (String path : paths) {
            ArgumentCompleter argumentCompleter1 =
                    new ArgumentCompleter(
                            new StringsCompleter(""),
                            new StringsCompleter("="),
                            new StringsCompleter(path),
                            new StringsCompleter());
            argumentCompleter1.setStrict(false);
            completers.add(argumentCompleter1);

            for (String account : accounts) {
                ArgumentCompleter argumentCompleter2 =
                        new ArgumentCompleter(
                                new StringsCompleter(""),
                                new StringsCompleter("="),
                                new StringsCompleter("WeCross.getResource"),
                                new StringsCompleter(path),
                                new StringsCompleter(account),
                                new StringsCompleter());
                argumentCompleter2.setStrict(false);
                completers.add(argumentCompleter2);
            }
        }

        for (String command : resourceCommands) {
            for (String path : paths) {
                for (String account : accounts) {
                    completers.add(
                            new ArgumentCompleter(
                                    new StringsCompleter(command),
                                    new StringsCompleter(path),
                                    new StringsCompleter(account),
                                    new StringsCompleter()));
                }
            }
        }
    }

    public static void addVarCompleters(
            List<Completer> completers,
            Set<String> resourceVars,
            Set<String> pathVars,
            List<String> accounts) {

        for (String var : resourceVars) {
            completers.add(
                    new ArgumentCompleter(
                            new StringsCompleter(var + ".call"), new StringsCompleter()));
            completers.add(
                    new ArgumentCompleter(
                            new StringsCompleter(var + ".sendTransaction"),
                            new StringsCompleter()));
        }

        for (String var : resourceVars) {
            completers.add(new ArgumentCompleter(new StringsCompleter(var + ".status")));
            completers.add(new ArgumentCompleter(new StringsCompleter(var + ".detail")));
        }

        // pathVars
        for (String var : pathVars) {
            for (String account : accounts) {
                ArgumentCompleter argumentCompleter2 =
                        new ArgumentCompleter(
                                new StringsCompleter(""),
                                new StringsCompleter("="),
                                new StringsCompleter("WeCross.getResource"),
                                new StringsCompleter(var),
                                new StringsCompleter(account),
                                new StringsCompleter());
                argumentCompleter2.setStrict(false);
                completers.add(argumentCompleter2);
            }
        }

        for (String command : resourceCommands) {
            for (String var : pathVars) {
                for (String account : accounts) {
                    completers.add(
                            new ArgumentCompleter(
                                    new StringsCompleter(command),
                                    new StringsCompleter(var),
                                    new StringsCompleter(account),
                                    new StringsCompleter()));
                }
            }
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
