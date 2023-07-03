package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.FileUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.ResourceDetail;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.rpc.methods.response.ResourceResponse;
import com.webank.wecrosssdk.utils.RPCUtils;
import java.io.File;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class BCOSCommand {
    private static final Logger logger = LoggerFactory.getLogger(BCOSCommand.class);
    private WeCrossRPC weCrossRPC;

    public BCOSCommand(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    /**
     * deploy contract
     *
     * @params BCOSDeploy [path] [filePath] [className] [version]
     */
    public void deploy(String[] params) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "bcosDeploy");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.BCOSDeployHelp();
            return;
        }
        if (params.length < 4) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "bcosDeploy");
        }

        String path = params[1];
        String chain = path.substring(0, path.lastIndexOf('.') + 1);
        RPCUtils.checkPath(path);
        String stubType = "";
        ResourceResponse resources = weCrossRPC.listResources(false).send();
        for (ResourceDetail resourceDetail : resources.getResources().getResourceDetails()) {
            if (resourceDetail.getPath().startsWith(chain)) {
                stubType = resourceDetail.getStubType();
                break;
            }
        }
        if (stubType.equals("")) {
            throw new WeCrossConsoleException(ErrorCode.INVALID_PATH, path);
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Object> args = new ArrayList<>();
        // BCOSDeployWasm [path] [abi] [bin]
        if (stubType.contains("WASM")) {
            String name = path.split("\\.")[2];
            String binContent = FileUtils.readFileContent(params[2]);
            String abiContent = FileUtils.readFileContent(params[3]);
            args.addAll(Arrays.asList(name, abiContent, binContent));
            for (int i = 4; i < params.length; i++) {
                // for constructor
                args.add(ConsoleUtils.parseString(params[i]));
            }
        } else {
            // Solidity
            String cnsName = path.split("\\.")[2];
            String sourcePath = params[2];
            String contractName = params[3];

            org.springframework.core.io.Resource resource =
                    resolver.getResource("file:" + sourcePath);
            if (!resource.exists()) {
                resource = resolver.getResource("classpath:" + sourcePath);
                if (!resource.exists()) {
                    logger.error("Source file: {} not exists", sourcePath);
                    throw new Exception("Source file: " + sourcePath + " not exists");
                }
            }

            String filename = resource.getFilename();
            String realPath = resource.getFile().getAbsolutePath();
            String dir =
                    realPath.substring(0, realPath.lastIndexOf(File.separator)) + File.separator;

            String sourceContent = FileUtils.mergeSource(dir, filename, resolver, new HashSet<>());

            if (stubType.contains("BCOS3")) {
                args.addAll(Arrays.asList(cnsName, sourceContent, contractName));
                for (int i = 4; i < params.length; i++) {
                    // for constructor
                    args.add(ConsoleUtils.parseString(params[i]));
                }
            } else {
                String version = params[4];
                args.addAll(Arrays.asList(cnsName, sourceContent, contractName, version));
                for (int i = 5; i < params.length; i++) {
                    // for constructor
                    args.add(ConsoleUtils.parseString(params[i]));
                }
            }
        }
        CommandResponse response = weCrossRPC.customCommand("deploy", path, args.toArray()).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * register abi in cns
     *
     * @params bcosRegister [path] [filePath] [address] [contractName] [version]
     */
    public void register(String[] params) throws Exception {
        if (params.length == 1) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "bcosRegister");
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.BCOSRegisterHelp();
            return;
        }

        if (params.length < 6) {
            throw new WeCrossConsoleException(ErrorCode.PARAM_MISSING, "bcosRegister");
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String cnsName = path.split("\\.")[2];
        String sourcePath = params[2];
        String address = params[3];
        String contractName = params[4];
        String version = params[5];

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource resource = resolver.getResource("file:" + sourcePath);
        if (!resource.exists()) {
            resource = resolver.getResource("classpath:" + sourcePath);
            if (!resource.exists()) {
                logger.error("Source file: {} not exists", sourcePath);
                throw new Exception("Source file: " + sourcePath + " not exists");
            }
        }

        String filename = resource.getFilename();
        String realPath = resource.getFile().getAbsolutePath();
        String dir = realPath.substring(0, realPath.lastIndexOf(File.separator)) + File.separator;

        String sourceContent = FileUtils.mergeSource(dir, filename, resolver, new HashSet<>());

        List<Object> args =
                new ArrayList<>(
                        Arrays.asList(
                                cnsName,
                                sourcePath.endsWith("abi") ? "abi" : "sol",
                                sourceContent,
                                address,
                                contractName,
                                version));

        CommandResponse response =
                weCrossRPC.customCommand("register", path, args.toArray()).send();
        PrintUtils.printCommandResponse(response);
    }
}
