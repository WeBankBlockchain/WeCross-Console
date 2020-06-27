package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.utils.RPCUtils;

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

        // todo get bytes from "contacts/chaincode"
        byte[] codes = null;

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
        String name = path.split("\\.")[2] + ".abi";
        String account = params[2];
        String version = params[3];
        String orgName = params[4];
        String language = params[5];
        String policy = params[6];
        String initArgs = params[7];

        Object[] args = new Object[] {name, version, orgName, language, policy, initArgs};

        CommandResponse response =
                weCrossRPC.customCommand("instantiate", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }
}
