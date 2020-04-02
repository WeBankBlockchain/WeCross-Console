package com.webank.wecross.console.common;

public class HelpInfo {

    public static void promptHelp(String command) {
        System.out.println("Try '" + command + " -h or --help' for more information.");
        System.out.println();
    }

    public static boolean promptNoParams(String[] params, String funcName) {
        if (params.length == 2) {
            if ("-h".equals(params[1]) || "--help".equals(params[1])) {
                helpNoParams(funcName);
                return true;
            } else {
                promptHelp(funcName);
                return true;
            }
        } else if (params.length > 2) {
            promptHelp(funcName);
            return true;
        } else {
            return false;
        }
    }

    public static void helpNoParams(String func) {
        switch (func) {
            case "help":
            case "h":
                help();
                break;
            case "supportedStubs":
                supportedStubsHelp();
                break;
            case "listAccounts":
                listAccountsHelp();
                break;
            case "listLocalResources":
                listLocalResourcesHelp();
                break;
            case "listResources":
                listAllResourcesHelp();
                break;
            case "generateSecretAndHash":
                generateSecretAndHashHelp();
            case "quit":
            case "q":
                quitHelp();
                break;

            default:
                break;
        }
    }

    public static void help() {
        ConsoleUtils.singleLine();
        System.out.println("Provide help information");
        System.out.println("Usage: help");
        ConsoleUtils.singleLine();
    }

    public static void supportedStubsHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List supported stubs of WeCross router");
        System.out.println("Usage: supportedStubs");
        ConsoleUtils.singleLine();
    }

    public static void listAccountsHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List all accounts stored in WeCross router");
        System.out.println("Usage: listAccounts");
        ConsoleUtils.singleLine();
    }

    public static void listResourcesHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List all resources configured by WeCross router");
        System.out.println("Usage: list [option]");
        System.out.println("option -- 1: ignore remote source");
        System.out.println("option -- 0: not ignore remote source");
        ConsoleUtils.singleLine();
    }

    public static void listLocalResourcesHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List local resources configured by WeCross router.");
        System.out.println("Usage: listLocalResources ");
        ConsoleUtils.singleLine();
    }

    public static void listAllResourcesHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List all resources including remote resources");
        System.out.println("Usage: listResources");
        ConsoleUtils.singleLine();
    }

    public static void statusHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Check if the resource exists.");
        System.out.println("Usage: status [path]");
        System.out.println("path: the path of resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void detailHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get the resource information");
        System.out.println("Usage: detail [path]");
        ConsoleUtils.singleLine();
    }

    public static void callHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call constant method of smart contract");
        System.out.println("Usage:  call [path] [accountName] [method] [...args]");
        System.out.println("path:   the path of the contract resource in wecross router");
        System.out.println("accountName:   choose an account to sign");
        System.out.println("method: the method in contract");
        System.out.println("args:   variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void sendTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call non-constant method of smart contract");
        System.out.println("Usage:  sendTransaction [path] [accountName] [method] [...args]");
        System.out.println("path:   the path of the contract resource in wecross router");
        System.out.println("accountName:   choose an account to sign");
        System.out.println("method: the method in contract");
        System.out.println("args:   variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void newContractHelp() {
        ConsoleUtils.singleLine();
        System.out.println("New a HTLCImpl contract");
        System.out.println("Usage:  newContract [path] [accountName] [...args]");
        System.out.println("path:   the path of the contract resource in wecross router");
        System.out.println("accountName:   choose an account to sign");
        System.out.println(
                "args:   hash, secret, role, sender0, receiver0, amount0, timelock0, sender1, receiver1, amount1, timelock1");
        ConsoleUtils.singleLine();
    }

    public static void generateTimelockHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Generate two valid timelocks");
        System.out.println("Usage:   generateTimelock [interval]");
        System.out.println(
                "interval: [timelock0 - interval] = timelock1 = [now + interval], interval >= 300(seconds)");
        ConsoleUtils.singleLine();
    }

    public static void generateSecretAndHashHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Generate a random secret and its hash");
        System.out.println("Usage:  generateSecretAndHash");
        ConsoleUtils.singleLine();
    }

    public static void quitHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Quit console");
        System.out.println("Usage: quit or exit");
        ConsoleUtils.singleLine();
    }
}
