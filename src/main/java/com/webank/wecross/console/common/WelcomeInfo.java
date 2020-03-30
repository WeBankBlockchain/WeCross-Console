package com.webank.wecross.console.common;

public class WelcomeInfo {

    public static void welcome() {
        ConsoleUtils.doubleLine();
        System.out.println("Welcome to WeCross console(" + ConsoleVersion.Version + ")!");
        System.out.println("Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.");
        System.out.println();
        ConsoleUtils.doubleLine();
    }

    public static void help(String[] params) {
        if (HelpInfo.promptNoParams(params, "help")) {
            return;
        }
        if (params.length > 2) {
            HelpInfo.promptHelp("help");
            return;
        }
        ConsoleUtils.singleLine();
        StringBuilder sb = new StringBuilder();
        sb.append("quit                               Quit console.\n");
        sb.append("supportedStubs                     List supported stubs of WeCross router.\n");
        sb.append(
                "listAccounts                       List all accounts stored in WeCross router.\n");
        sb.append(
                "listLocalResources                 List local resources configured by WeCross server.\n");
        sb.append(
                "listResources                      List all resources including remote resources.\n");
        sb.append("status                             Check if the resource exists.\n");
        sb.append("detail                             Get the resource information.\n");
        sb.append("call                               Call constant method of smart contract.\n");
        sb.append(
                "sendTransaction                    Call non-constant method of smart contract.\n");
        sb.append("generateTimelock                   Generate two valid timelocks.\n");
        sb.append("generateSecretAndHash              Generate a secret and its hash.\n");
        sb.append("newContract                        New a HTLC contract.\n");
        sb.append(
                "WeCross.getResource                Init resource by path, and assign it to a custom variable.\n");
        sb.append("[resource].[command]               Equal to command: command [path].\n");

        System.out.println(sb.toString());
        ConsoleUtils.singleLine();
        System.out.println();
    }
}
