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
                listAccountHelp();
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

    public static void listAccountHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List your Universal Account's information");
        System.out.println("Usage: listAccount [arg]");
        System.out.println("arg -- '-d': To list a detailed info");
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

    public static void listXATransactionsHelp() {
        ConsoleUtils.singleLine();
        System.out.println("List XA transactions");
        System.out.println("Usage: listXATransactions [size]");
        System.out.println("size -- the size of XA transactions to list, must range in [1, 1024]");
        ConsoleUtils.singleLine();
    }

    public static void detailHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get the resource information");
        System.out.println("Usage: detail [path]");
        System.out.println(
                "note: if 'isTemporary' filed in properties is 'true', it means this resource may not exist");
        System.out.println("      or this resource can be unregistered in chain.");
        ConsoleUtils.singleLine();
    }

    public static void callHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call constant method of smart contract");
        System.out.println("Usage: call [path] [method] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void sendTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call non-constant method of smart contract");
        System.out.println("Usage: sendTransaction [path] [method] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void invokeHelp() {
        ConsoleUtils.singleLine();
        System.out.println(
                "Call non-constant method of smart contract, if an xa Transaction is running, it will auto-transfer to command execTransaction");
        System.out.println("Usage: invoke [path] [method] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void newProposalHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Create a htlc transfer proposal");
        System.out.println("Usage: newHTLCProposal [path] [...args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println(
                "args -- hash, secret, role, sender0, receiver0, amount0, timelock0, sender1, receiver1, amount1, timelock1");
        System.out.println("[note]: 1. only sender can create this contract");
        System.out.println(
                "[note]: 2. if you are initiator who holds the secret, [role] would be true, and *0 is your info. Else, the [role] is false and *1 is your info");
        ConsoleUtils.singleLine();
    }

    public static void loginHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Login SDK if you have already registered");
        System.out.println("Usage: login [username] [password]");
        System.out.println("[One more thing]: You can just type-in 'login' to login ssh-style-ly");
        System.out.println(
                "[One last thing]: You can set your userInfo into TOML file 'application.toml' like 'application-sample.toml', ");
        System.out.println("                  and just type-in 'login' to login");
        ConsoleUtils.singleLine();
    }

    public static void registerHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Register a Universal Account");
        System.out.println("Usage: registerAccount [username] [password]");
        System.out.println(
                "[One more thing]: You can just type-in 'registerAccount' to register an account ssh-style-ly");
        ConsoleUtils.singleLine();
    }

    public static void logoutHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Logout SDK");
        System.out.println("Usage: logout [without any args]");
        ConsoleUtils.singleLine();
    }

    public static void addChainAccountHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Add a Chain Account to your Universal Account");
        System.out.println(
                "Usage: addChainAccount [chainType] [firstKeyPath] [secondKeyPath] [extraData] [isDefault]");
        System.out.println("chainType -- supported chain type");
        System.out.println(
                "             supported chain type now:" + ConsoleUtils.supportChainList);
        System.out.println(
                "firstKeyPath  -- if ([chainType] == BCOS*);   then it stand for pubKeyPath");
        System.out.println(
                "            else if ([chainType] == Fabric*); then it stand for certPath");
        System.out.println(
                "secondKeyPath -- if ([chainType] == BCOS*);   then it stand for secretKeyPath");
        System.out.println(
                "            else if ([chainType] == Fabric*); then it stand for keyPath");
        System.out.println("extraData -- if ([chainType] == BCOS*);   then it stand for address");
        System.out.println(
                "        else if ([chainType] == Fabric*); then it stand for membershipID");
        System.out.println(
                "isDefault -- whether this new chain Account is the default sendTransaction chain account in this [chainType]");
        ConsoleUtils.singleLine();
        System.out.println("Example:");
        System.out.println(
                "    addChainAccount BCOS2.0 path/to/pubKey path/to/secretKey address true");
        System.out.println(
                "    addChainAccount GM_BCOS2.0 path/to/pubKey path/to/secretKey address false");
        System.out.println(
                "    addChainAccount Fabric1.4 path/to/cert path/to/key membershipID true");
        System.out.println(
                "    addChainAccount Fabric2.0 path/to/cert path/to/key membershipID true");
        ConsoleUtils.singleLine();
    }

    public static void setDefaultAccountHelp() {
        ConsoleUtils.singleLine();
        System.out.println(
                "Set the chain account to be the default account to send transaction in a certain [chainType]");
        System.out.println("Usage: setDefaultAccount [chainType][keyID]");
        System.out.println("chainType -- supported chain type");
        System.out.println("    support chain type now:" + ConsoleUtils.supportChainList);
        System.out.println("keyID -- the primary key stand for chain account");
        System.out.println("[note]: you can do command listAccount to check chain account's keyID");
        ConsoleUtils.singleLine();
    }

    public static void setDefaultChainAccountHelp() {
        ConsoleUtils.singleLine();
        System.out.println(
                "Set the chain account to be the default account to send transaction in a certain [chain]");
        System.out.println("Usage: setDefaultChainAccount [chainName][keyID]");
        System.out.println("chainName -- the full name of the chain");
        System.out.println("    chainName is like: payment.fabric-mychannel");
        System.out.println("keyID -- the primary key stand for chain account");
        System.out.println("[note]: you can do command listAccount to check chain account's keyID");
        ConsoleUtils.singleLine();
    }

    public static void genTimeLockHelp() {
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
        System.out.println("Usage: callTransaction [path] [method] [args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void execTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Call non-constant method of smart contract during transaction");
        System.out.println("Usage: execTransaction [path] [method] [args]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void startTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Start an xa transaction");
        System.out.println("Usage: startTransaction [path_1] ... [path_n]");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void commitTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Commit an xa transaction");
        System.out.println("Usage: commitTransaction [path_1] ... [path_n]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println(
                "[note]: It will auto-complete args if you call \"commitTransaction\" without args");
        ConsoleUtils.singleLine();
    }

    public static void rollbackTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Rollback an xa transaction");
        System.out.println("Usage: rollbackTransaction [path_1] ... [path_n]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println(
                "[note]: It will auto-complete args if you call \"rollbackTransaction\" without args");
        ConsoleUtils.singleLine();
    }

    public static void autoCommitTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Transactional call same method of multi resources in one transaction");
        System.out.println("Usage: autoCommitTransaction [path...] [method] [args]");
        System.out.println(
                "[path] -- a array of paths, what are the contract resource in wecross router");
        System.out.println("method -- the method in contract");
        System.out.println("args -- variable parameter list");
        ConsoleUtils.singleLine();
    }

    public static void loadTransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Load a specified transaction context");
        System.out.println("Usage: loadTransaction [transactionID] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void getXATransactionHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get info of specified XA transaction");
        System.out.println("Usage: getXATransaction [transactionID] [path_1] ... [path_n]");
        System.out.println("transactionID -- transaction identifier");
        System.out.println("path -- the path of the contract resource in wecross router");
        ConsoleUtils.singleLine();
    }

    public static void getCurrentTransactionIDHelp() {
        ConsoleUtils.singleLine();
        System.out.println("get Current xa Transaction ID");
        System.out.println("Usage: getCurrentTransaction [without args]");
        ConsoleUtils.singleLine();
    }

    public static void BCOSDeployHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Deploy contract and register contract info to CNS/BFS in BCOS chain ");
        System.out.println("If you deploy Solidity contract:");
        System.out.println("\tUsage: bcosDeploy [Path] [Source file path] [Class name] [Version]");
        System.out.println(
                "\tPath -- e.g: [zone.chain.res], specify which the path to be deployed");
        System.out.println(
                "\tSource file path from conf/ -- The solidity source code file path, e.g: HelloWorld.sol");
        System.out.println("\tContract name -- The contract to be deploy");
        System.out.println(
                "\tVersion -- The contract version, if chain version is BCOS3.0, should not use version");
        System.out.println("\tExample:");
        System.out.println(
                "    \tbcosDeploy payment.bcos.HelloWorld contracts/solidity/HelloWorld.sol HelloWorld 1.0");
        ConsoleUtils.singleLine();
        System.out.println("If you deploy WASM contract:");
        System.out.println("\tUsage: bcosDeploy [Path] [BIN file path] [ABI file path]");
        System.out.println(
                "\tPath -- e.g: [zone.chain.res], specify which the path to be deployed");
        System.out.println(
                "\tBIN file path from conf/ -- The binary file after contract being compiled via cargo-liquid, e.g: hello_world.wasm");
        System.out.println(
                "\tABI file path from conf/ -- The ABI file after contract being compiled via cargo-liquid, e.g: hello_world.abi");
        System.out.println("\tExample:");
        System.out.println(
                "    \tbcosDeploy payment.bcos3.HelloWorld contracts/liquid/hello_world.wasm  contracts/liquid/hello_world.abi");
    }

    public static void BCOSRegisterHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Register contract info to CNS in BCOS chain");
        System.out.println(
                "Usage: bcosRegister [Path] [Source file path] [Contract address] [Contract name] [Version]");
        System.out.println("Path -- e.g: [zone.chain.res], specify which the path to be register");
        System.out.println(
                "Source file path from conf/ -- The solidity source code/solidity abi file path, e.g: HelloWorld.sol or HelloWorld.abi");
        System.out.println("Contract address -- contract address");
        System.out.println("Contract name -- contract name to be register");
        System.out.println("Version -- The contract version");
        System.out.println("Example:");
        System.out.println(
                "    bcosRegister payment.bcos.HelloWorld contracts/solidity/HelloWorld.sol 0x2c8595f82dc930208314030abc6f5c4ddbc8864f HelloWorld 1.0");
        System.out.println(
                "    bcosRegister payment.bcos.HelloWorld /data/app/HelloWorld.abi 0x2c8595f82dc930208314030abc6f5c4ddbc8864f HelloWorld 1.0");
        ConsoleUtils.singleLine();
    }

    public static void fabricInstallHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Install chaincode in fabric chain");
        System.out.println("Usage: fabricInstall [path] [version] [orgName] [language]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be installed by name");
        System.out.println("orgName -- organization");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language GO_LANG/JAVA");
        System.out.println("Example:");
        System.out.println(
                "    fabricInstall payment.fabric.sacc Org1 contracts/chaincode/sacc 1.0 GO_LANG");
        System.out.println(
                "    fabricInstall payment.fabric.sacc Org2 contracts/chaincode/sacc 1.0 GO_LANG");
        ConsoleUtils.singleLine();
    }

    public static void fabricInstantiateHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Instantiate chaincode in fabric chain");
        System.out.println(
                "Usage: fabricInstantiate [path] [version] [orgName] [language] [policy] [initArgs]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be instantiated by name");
        System.out.println("orgNames -- every organization which has installed the chaincode");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language");
        System.out.println(
                "policy -- endorsement policy file name (default means OR(every endorser))");
        System.out.println("initArgs -- args of int function");
        System.out.println("Example:");
        System.out.println(
                "    fabricInstantiate payment.fabric.sacc [\"Org1\",\"Org2\"] contracts/chaincode/sacc 1.0 GO_LANG default [\"a\",\"10\"]");
        System.out.println(
                "    fabricInstantiate payment.fabric.sacc [\"Org1\",\"Org2\"] contracts/chaincode/sacc 1.0 GO_LANG policy.yaml [\"a\",\"10\"]");
        ConsoleUtils.singleLine();
    }

    public static void fabricUpgradeHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Upgrade chaincode in fabric chain");
        System.out.println(
                "Usage: fabricUpgrade [path] [version] [orgName] [language] [policy] [initArgs]");
        System.out.println(
                "path -- [zone.chain.contractName], specify which contract to be instantiated by name");
        System.out.println("orgNames -- every organization which has installed the chaincode");
        System.out.println("sourcePath -- chaincode project dir from conf/");
        System.out.println("version -- contract version");
        System.out.println("language -- contract language");
        System.out.println(
                "policy -- endorsement policy file name (default means OR(every endorser))");
        System.out.println("initArgs -- args of int function");
        System.out.println("Example:");
        System.out.println(
                "    fabricUpgrade payment.fabric.sacc [\"Org1\",\"Org2\"] contracts/chaincode/sacc 2.0 GO_LANG default [\"a\",\"10\"]");
        System.out.println(
                "    fabricUpgrade payment.fabric.sacc [\"Org1\",\"Org2\"] contracts/chaincode/sacc 2.0 GO_LANG policy.yaml [\"a\",\"10\"]");
        ConsoleUtils.singleLine();
    }

    public static void checkTransferStatusHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Check htlc transfer status by hash");
        System.out.println("Usage: checkTransferStatus [path] [hash]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("hash -- transfer contract-id");
        ConsoleUtils.singleLine();
    }

    public static void getBlockHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Get block by number in specific chain");
        System.out.println("Usage: getBlock [path] [number]");
        System.out.println("path -- the path of the contract resource in wecross router");
        System.out.println("number -- block number");
        ConsoleUtils.singleLine();
    }

    public static void quitHelp() {
        ConsoleUtils.singleLine();
        System.out.println("Quit console");
        System.out.println("Usage: quit or exit");
        ConsoleUtils.singleLine();
    }
}
