package com.webank.wecross.console.mock;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecrosssdk.exception.WeCrossSDKException;
import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.io.Serializable;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockResource implements Serializable {

    private Logger logger = LoggerFactory.getLogger(MockResource.class);

    private Resource resource;

    public MockResource(WeCrossRPC weCrossRPC, String path) {
        try {
            resource = ResourceFactory.build(weCrossRPC, path);
        } catch (WeCrossSDKException e) {
            System.out.println(e.getMessage());
        }
    }

    public void status() {
        try {
            System.out.println(resource.status());
        } catch (WeCrossSDKException e) {
            System.out.println(e.getMessage());
        }
    }

    public void detail() {
        try {
            ConsoleUtils.printJson(resource.detail().toString());
        } catch (WeCrossSDKException e) {
            System.out.println(e.getMessage());
        }
    }

    public void call(String method, String... args) {
        try {
            System.out.println("Result: " + Arrays.toString(resource.call(method, args)));
        } catch (WeCrossSDKException e) {
            logger.info("call error: {}", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public void sendTransaction(String method, String... args) {
        try {
            System.out.println(
                    "Result: " + Arrays.toString(resource.sendTransaction(method, args)));
        } catch (WeCrossSDKException e) {
            logger.info("sendTransaction error: {}", e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}
