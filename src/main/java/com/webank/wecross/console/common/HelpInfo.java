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
            case "genSecretAndHash":
                genSecretAndHashHelp();
            case "quit":
            case "q":
            case "exit":
            case "e":
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
        System.out.println("path -- the path of resource in wecross router");
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
        System.out.println("Usage: call [path] [account] [method] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void sendTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call non-constant method of smart contract");
        System.out.println("Usage: sendTransaction [path] [account] [method] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void newProposaltHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Create a htlc transfer proposal");
        System.out.println("Usage: newHTLCProposal [path] [account] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- sender's account name");
        System.out.println(
                "args -- hash, secret, role, sender0, receiver0, amount0, timelock0, sender1, receiver1, amount1, timelock1");
        System.out.println(
                "[note]: 1. only sender can create this contract, so [account] must be sender's account name");
        System.out.println(
                "[note]: 2. if you are initiator who holds the secret, [role] would be true, and *0 is your info. Else, the [role] is false and *1 is your info");
        ConsoleUtils.singleLine();
    }

    public static void genTimelockHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Generate two valid timelocks");
        System.out.println("Usage: genTimelock [interval]");
        System.out.println(
                "interval -- [timelock0 - interval] = timelock1 = [now + interval], interval >= 300(seconds)");
        ConsoleUtils.singleLine();
    }

    public static void genSecretAndHashHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Generate a random secret and its hash");
        System.out.println("Usage: genSecretAndHash");
        ConsoleUtils.singleLine();
    }

    public static void callTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call constant method of smart contract during transaction");
        System.out.println(
                "Usage: callTransaction [path] [account] [transactionID] [method] [args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void execTransactionHelp() {
        System.out.println("Call non-constant method of smart contract during transaction");
        System.out.println(
                "Usage: execTransaction [path] [account] [transactionID] [seq] [method] [args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("seq -- sequence for each step in transaction");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
    }

    public static void startTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Start a 2pc transaction");
        System.out.println(
                "Usage: startTransaction [transactionID] [account_1] ... [account_n] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("account -- choose an account to sign");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void commitTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Commit a 2pc transaction");
        System.out.println(
                "Usage: commitTransaction [transactionID] [account_1] ... [account_n] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("account -- choose an account to sign");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void rollbackTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Rollback a 2pc transaction");
        System.out.println(
                "Usage: rollbackTransaction [transactionID] [account_1] ... [account_n] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("account -- choose an account to sign");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void getTransactionInfoHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get info of specified transaction");
        System.out.println(
                "Usage: getTransactionInfo [transactionID] [account_1] ... [account_n] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("account -- choose an account to sign");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void getTransactionIDsHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get transaction ids of 2pc");
        System.out.println("Usage: getTransactionIDs [path] [account] [option]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("option -- 0 all, 1 finished, 2 unfinished");
        ConsoleUtils.singleLine();
    }

    public static void BCOSDeployHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Deploy contract and register contract info to CNS in BCOS chain ");
        System.out.println(
                "Usage: bcosDeploy [Path] [Account] [Source file path] [Class name] [Version]");
        System.out.println("Path -- e.g: [zone.chain.res], specify which the path to be deployed");
        System.out.println("Account -- Choose an account to send transaction");
        System.out.println(
                "Source file path -- The solidity source code file path, e.g: HelloWorld.sol");
        System.out.println("Class name -- The contract to be deploy");
        System.out.println("Version -- The contract version");
        System.out.println("Example:");
        System.out.println(
                "    bcosDeploy payment.bcos.HelloWorld bcos_user1 conf/contracts/solidity/HelloWorld.sol HelloWorld 1.0");
        ConsoleUtils.singleLine();
    }

    public static void BCOSRegisterHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Register contract info to CNS in BCOS chain");
        System.out.println(
                "Usage: bcosRegister [Path] [Account] [Source file path] [Contract address] [Version]");
        System.out.println("Path -- e.g: [zone.chain.res], specify which the path to be register");
        System.out.println("Account -- Choose an account to send transaction");
        System.out.println(
                "Source file path -- The solidity source code/solidity abi file path, e.g: HelloWorld.sol or HelloWorld.abi");
        System.out.println("Contract address -- contract address");
        System.out.println("Version -- The contract version");
        System.out.println("Example:");
        System.out.println(
                "    bcosRegister payment.bcos.HelloWorld bcos_user1 conf/contracts/solidity/HelloWorld.sol 0x2c8595f82dc930208314030abc6f5c4ddbc8864f 1.0");
        System.out.println(
                "    bcosRegister payment.bcos.HelloWorld bcos_user1 /data/app/HelloWorld.abi 0x2c8595f82dc930208314030abc6f5c4ddbc8864f 1.0");
        ConsoleUtils.singleLine();
    }

    public static void fabricInstallHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Install chaincode in fabric chain");
        System.out.println("Usage: fabricInstall [path] [account] [version] [orgName] [language]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be installed by name");
        System.out.println("account -- choose an account to sign");
        System.out.println("orgName -- organization");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language GO_LANG/JAVA");
        System.out.println("Example:");
        System.out.println(
                "    fabricInstall payment.fabric.sacc fabric_admin_org1 Org1 contracts/chaincode/sacc 1.0 GO_LANG");
        System.out.println(
                "    fabricInstall payment.fabric.sacc fabric_admin_org2 Org2 contracts/chaincode/sacc 1.0 GO_LANG");
        ConsoleUtils.singleLine();
    }

    public static void fabricInstantiateHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Instantiate chaincode in fabric chain");
        System.out.println(
                "Usage: fabricInstantiate [path] [account] [version] [orgName] [language] [policy] [initArgs]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be instantiated by name");
        System.out.println("account -- choose an account to sign");
        System.out.println("orgNames -- every organization which has installed the chaincode");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language");
        System.out.println(
                "policy -- endorsement policy file name (default means OR(every endorser))");
        System.out.println("initArgs -- args of int function");
        System.out.println("Example:");
        System.out.println(
                "    fabricInstantiate payment.fabric.sacc fabric_admin [\"Org1\",\"Org2\"] contracts/chaincode/sacc 1.0 GO_LANG default [\"a\",\"10\"]");
        System.out.println(
                "    fabricInstantiate payment.fabric.sacc fabric_admin [\"Org1\",\"Org2\"] contracts/chaincode/sacc 1.0 GO_LANG policy.yaml [\"a\",\"10\"]");
        ConsoleUtils.singleLine();
    }

    public static void fabricUpgradeHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Upgrade chaincode in fabric chain");
        System.out.println(
                "Usage: fabricUpgrade [path] [account] [version] [orgName] [language] [policy] [initArgs]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be instantiated by name");
        System.out.println("account -- choose an account to sign");
        System.out.println("orgNames -- every organization which has installed the chaincode");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language");
        System.out.println(
                "policy -- endorsement policy file name (default means OR(every endorser))");
        System.out.println("initArgs -- args of int function");
        System.out.println("Example:");
        System.out.println(
                "    fabricUpgrade payment.fabric.sacc fabric_admin [\"Org1\",\"Org2\"] contracts/chaincode/sacc 2.0 GO_LANG default [\"a\",\"10\"]");
        System.out.println(
                "    fabricUpgrade payment.fabric.sacc fabric_admin [\"Org1\",\"Org2\"] contracts/chaincode/sacc 2.0 GO_LANG policy.yaml [\"a\",\"10\"]");
        ConsoleUtils.singleLine();
    }

    public static void checkTransferStatusHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Check htlc transfer status by hash");
        System.out.println("Usage: checkTransferStatus [path] [account] [hash]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("account -- choose an account to sign");
        System.out.println("hash -- transfer contract-id");
        ConsoleUtils.singleLine();
    }

    public static void quitHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Quit console");
        System.out.println("Usage: quit or exit");
        ConsoleUtils.singleLine();
    }
}
