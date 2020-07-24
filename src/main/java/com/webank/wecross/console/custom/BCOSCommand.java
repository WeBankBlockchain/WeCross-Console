package com.webank.wecross.console.custom;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.response.CommandResponse;
import com.webank.wecrosssdk.utils.RPCUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class BCOSCommand {
    private static final Logger logger = LoggerFactory.getLogger(BCOSCommand.class);
    private WeCrossRPC weCrossRPC;

    public BCOSCommand(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    public String mergeSource(
            String currentDir, String sourceFile, PathMatchingResourcePatternResolver resolver)
            throws Exception {
        StringBuffer sourceBuffer = new StringBuffer();

        String fullPath = currentDir + sourceFile;
        String dir = fullPath.substring(0, fullPath.lastIndexOf(File.separator)) + File.separator;

        org.springframework.core.io.Resource sourceResource =
                resolver.getResource("file:" + fullPath);
        if (!sourceResource.exists()) {
            logger.error("Source file: {} not found!", fullPath);

            throw new Exception("Source file:" + fullPath + " not found");
        }

        Pattern pattern = Pattern.compile("^\\s*import\\s+[\"'](.+)[\"']\\s*;\\s*$");
        Scanner scanner = new Scanner(sourceResource.getInputStream(), "UTF-8");
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String depSourcePath = matcher.group(1);
                    sourceBuffer.append(mergeSource(dir, depSourcePath, resolver));
                } else {
                    sourceBuffer.append(line);
                    sourceBuffer.append(System.lineSeparator());
                }
            }
        } finally {
            scanner.close();
        }

        return sourceBuffer.toString();
    }

    /**
     * deploy contract
     *
     * @params BCOSDeploy [path] [account] [filePath] [className] [version]
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
        if (params.length < 6) {
            HelpInfo.promptHelp("bcosDeploy");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String cnsName = path.split("\\.")[2];
        String account = params[2];
        String sourcePath = params[3];
        String className = params[4];
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

        String sourceContent = mergeSource(dir, filename, resolver);

        List<Object> args =
                new ArrayList<>(Arrays.asList(cnsName, sourceContent, className, version));
        for (int i = 6; i < params.length; i++) {
            // for constructor
            args.add(ConsoleUtils.parseString(params[i]));
        }

        CommandResponse response =
                weCrossRPC.customCommand("deploy", path, account, args.toArray()).send();
        PrintUtils.printCommandResponse(response);
    }

    /**
     * register abi in cns
     *
     * @params bcosRegister [path] [account] [filePath] [address] [version]
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

        if (params.length < 6) {
            HelpInfo.promptHelp("bcosRegister");
            return;
        }

        String path = params[1];
        RPCUtils.checkPath(path);
        String cnsName = path.split("\\.")[2];
        String account = params[2];
        String sourcePath = params[3];
        String address = params[4];
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

        String sourceContent = mergeSource(dir, filename, resolver);

        List<Object> args =
                new ArrayList<>(
                        Arrays.asList(
                                cnsName,
                                sourcePath.endsWith("abi") ? "abi" : "sol",
                                sourceContent,
                                address,
                                version));

        CommandResponse response =
                weCrossRPC.customCommand("register", path, account, args.toArray()).send();
        PrintUtils.printCommandResponse(response);
    }
}
