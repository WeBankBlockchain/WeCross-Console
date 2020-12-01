package com.webank.wecross.console.common;

public class WelcomeInfo {

    public static void welcome() {
        ConsoleUtils.doubleLine();
        System.out.println("Welcome to WeCross console(" + Version.Version + ")!");
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
        sb.append("quit                             Quit console.\n");
        sb.append("registerAccount                  Register a Universal Account.\n");
        sb.append("login                            Login SDK if you have already registered.\n");
        sb.append("logout                           Logout SDK.\n");
        sb.append(
                "addChainAccount                  Add a Chain Account to your Universal Account.\n");
        sb.append(
                "setDefaultAccount                Set the chain account to be the default account to send transaction.\n");
        sb.append("supportedStubs                   List supported stubs of WeCross router.\n");
        sb.append("listAccount                      List your Universal Account's information.\n");
        sb.append(
                "listLocalResources               List local resources configured by WeCross server.\n");
        sb.append(
                "listResources                    List all resources including remote resources.\n");
        sb.append("status                           Check if the resource exists.\n");
        sb.append("detail                           Get resource information.\n");
        sb.append("call                             Call constant method of smart contract.\n");
        sb.append(
                "invoke                           Call non-constant method of smart contract, will auto-transfer to command execTransaction during transaction.\n");
        sb.append("sendTransaction                  Call non-constant method of smart contract.\n");
        sb.append(
                "callTransaction                  Call constant method of smart contract during transaction.\n");
        sb.append(
                "execTransaction                  Call non-constant method of smart contract during transaction.\n");
        sb.append("startTransaction                 Start an xa transaction.\n");
        sb.append("commitTransaction                Commit an xa transaction.\n");
        sb.append("rollbackTransaction              Rollback an xa transaction.\n");
        sb.append("loadTransaction                  Load a specified transaction context.\n");
        sb.append("getXATransaction                 Get info of specified XA transaction.\n");
        sb.append("listXATransaction                List XA transactions in route.\n");
        sb.append("bcosDeploy                       Deploy contract in BCOS chain.\n");
        sb.append("bcosRegister                     Register contract abi in BCOS chain.\n");
        sb.append("fabricInstall                    Install chaincode in fabric chain.\n");
        sb.append("fabricInstantiate                Instantiate chaincode in fabric chain.\n");
        sb.append("fabricUpgrade                    Upgrade chaincode in fabric chain.\n");
        sb.append("genTimelock                      Generate two valid timelocks.\n");
        sb.append("genSecretAndHash                 Generate a secret and its hash.\n");
        sb.append("newHTLCProposal                  Create a htlc transfer proposal .\n");
        sb.append("checkTransferStatus              Check htlc transfer status by hash.\n");
        sb.append("getCurrentTransactionID          Get Current xa Transaction ID.\n");
        sb.append(
                "WeCross.getResource              Init resource by path, and assign it to a custom variable.\n");
        sb.append("[resource].[command]             Equal to: command [path].\n");

        System.out.println(sb.toString());
        ConsoleUtils.singleLine();
        System.out.println();
    }
}
