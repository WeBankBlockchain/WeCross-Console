package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.common.TarUtils;
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
     * @params fabricInstall [path] [account] [version] [orgName] [language]
     */
    public void install(String[] params) throws Exception {
        // The command is
        // fabricInstall payment.fabric.sacc fabric_admin_org1 1.0 Org1 GO_LANG
        // fabricInstall payment.fabric.sacc fabric_admin_org2 1.0 Org2 GO_LANG
        if (params.length == 1) {
            HelpInfo.promptHelp("fabricInstall");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.fabricInstallHelp();
            return;
        }
        if (params.length != 6) {
            HelpInfo.promptHelp("fabricInstall");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2];
        String account = params[2];
        String version = params[3];
        String orgName = params[4];
        String language = params[5];

        String codes =
                TarUtils.generateTarGzInputStreamEncodedString(
                        "classpath:contracts/chaincode/" + name);
        Object[] args = new Object[] {name, version, orgName, language, codes};

        CommandResponse response = weCrossRPC.customCommand("install", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * instantiate chaincode
     *
     * @params fabricInstantiate [path] [account] [version] [orgName] [language] [policy] [initArgs]
     */
    public void instantiate(String[] params) throws Exception {
        // The command is:
        // fabricInstantiate payment.fabric.sacc fabric_admin 1.0 ["Org1","Org2"] GO_LANG
        // OR("Org1MSP.peer","Org2MSP.peer") ["a","10"]

        if (params.length == 1) {
            HelpInfo.promptHelp("fabricInstantiate");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.fabricInstantiateHelp();
            return;
        }
        if (params.length != 8) {
            HelpInfo.promptHelp("fabricInstantiate");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2];
        String account = params[2];
        String version = params[3];
        String orgNames = params[4];
        String language = params[5];
        String policyFile = params[6];
        String initArgs = params[7];

        String policy;
        if (policyFile.equals("default")) {
            policy = "";
        } else {
            policy =
                    FileUtils.readFileToBytesString(
                            "classpath:contracts/chaincode/" + name + File.separator + policyFile);
        }

        Object[] args = new Object[] {name, version, orgNames, language, policy, initArgs};

        CommandResponse response =
                weCrossRPC.customCommand("instantiate", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }
}
