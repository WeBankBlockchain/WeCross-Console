package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.*;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.utils.RPCUtils;
import java.io.File;

public class FabricCommand {
    private WeCrossRPC weCrossRPC;

    public FabricCommand(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    /**
     * install contract
     *
     * @params fabricInstall [path] [orgName] [sourcePath] [version] [language]
     */
    public void install(String[] params) throws Exception {
        // The command is
        // fabricInstall payment.fabric.sacc Org1 contracts/chaincode/sacc 1.0
        // GO_LANG
        // fabricInstall payment.fabric.sacc Org2 contracts/chaincode/sacc 1.0
        // GO_LANG
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricInstall");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.fabricInstallHelp();
            return;
        }
        if (params.length != 6) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricInstall");
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2];
        String account = ConsoleUtils.getRuntimeUsername();
        String orgName = params[2];
        String sourcePath = uniformPath(params[3]);
        String version = params[4];
        String language = params[5];

        String codes;
        if (language.equals("GO_LANG")) {
            codes = TarUtils.generateTarGzInputStreamEncodedStringFoGoChaincode(sourcePath);
        } else {
            codes = TarUtils.generateTarGzInputStreamEncodedString(sourcePath);
        }

        Object[] args = new Object[] {name, version, orgName, language, codes};

        CommandResponse response = weCrossRPC.customCommand("install", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * instantiate chaincode
     *
     * @params fabricInstantiate [path] [orgNames] [sourcePath] [version] [language] [policyFile]
     *     [initArgs]
     */
    public void instantiate(String[] params) throws Exception {
        // The command is:
        // fabricInstantiate payment.fabric.sacc ["Org1","Org2"]
        // contracts/chaincode/sacc 1.0 GO_LANG policy.yaml ["a","10"]

        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricInstantiate");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.fabricInstantiateHelp();
            return;
        }
        if (params.length != 8) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricInstantiate");
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2];
        String account = ConsoleUtils.getRuntimeUsername();
        String orgNames = params[2];
        String sourcePath = uniformPath(params[3]);
        String version = params[4];
        String language = params[5];
        String policyFile = params[6];
        String initArgs = params[7];

        String policy;
        if (policyFile.equals("default")) {
            policy = "";
        } else {
            policy = FileUtils.readFileToBytesString(sourcePath + File.separator + policyFile);
        }

        Object[] args = new Object[] {name, version, orgNames, language, policy, initArgs};

        CommandResponse response =
                weCrossRPC.customCommand("instantiate", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * upgrade chaincode
     *
     * @params fabricUpgrade [path] [orgNames] [sourcePath] [version] [language] [policyFile]
     *     [initArgs]
     */
    public void upgrade(String[] params) throws Exception {
        // The command is:
        // upgrade payment.fabric.sacc ["Org1","Org2"]
        // contracts/chaincode/sacc 2.0 GO_LANG policy.yaml ["a","10"]

        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricUpgrade");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.fabricUpgradeHelp();
            return;
        }
        if (params.length != 8) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "fabricUpgrade");
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2];
        String account = ConsoleUtils.getRuntimeUsername();
        String orgNames = params[2];
        String sourcePath = uniformPath(params[3]);
        String version = params[4];
        String language = params[5];
        String policyFile = params[6];
        String initArgs = params[7];

        String policy;
        if (policyFile.equals("default")) {
            policy = "";
        } else {
            policy = FileUtils.readFileToBytesString(sourcePath + File.separator + policyFile);
        }

        Object[] args = new Object[] {name, version, orgNames, language, policy, initArgs};

        CommandResponse response = weCrossRPC.customCommand("upgrade", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }

    private String uniformPath(String path) {
        if (path.startsWith("/") || path.startsWith("\\") || path.startsWith(File.pathSeparator)) {
            return "file:" + path;
        } else {
            return "classpath:" + path;
        }
    }
}
