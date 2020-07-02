package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.common.ZipUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.utils.RPCUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class BCOSCommand {
    private WeCrossRPC weCrossRPC;

    public BCOSCommand(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    /**
     * deploy contract
     *
     * @params bcosDeploy [path] [account] [version]
     */
    public void deploy(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("bcosDeploy");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.BCOSDeployHelp();
            return;
        }
        if (params.length != 4) {
            HelpInfo.promptHelp("bcosDeploy");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String account = params[2];
        String version = params[3];

        String contractsPath =
                BCOSCommand.class.getClassLoader().getResource("contracts/solidity/").getPath();
        ZipUtils.zipDir(contractsPath);
        File file = new File("solidity.zip");
        byte[] contractBytes = Files.readAllBytes(file.toPath());
        file.delete();

        Object[] args = new Object[] {Base64.getEncoder().encodeToString(contractBytes), version};

        CommandResponse response = weCrossRPC.customCommand("deploy", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * register abi in cns
     *
     * @params bcosRegister [path] [account] [version] [address]
     */
    public void register(String[] params) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("bcosRegister");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.BCOSRegisterHelp();
            return;
        }
        if (params.length != 5) {
            HelpInfo.promptHelp("bcosRegister");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String name = path.split("\\.")[2] + ".abi";
        String account = params[2];
        String version = params[3];
        String address = params[4];

        String abiPath = BCOSCommand.class.getClassLoader().getResource("abi/" + name).getPath();
        String abi = new String(Files.readAllBytes(Paths.get(abiPath)), StandardCharsets.UTF_8);
        Object[] args = new Object[] {version, address, abi};

        CommandResponse response = weCrossRPC.customCommand("register", path, account, args).send();
        PrintUtils.printCommandResponse(response);
    }
}
