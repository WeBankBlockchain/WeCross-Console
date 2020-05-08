package com.webank.wecross.console.mock;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.io.Serializable;

public class MockWeCross implements Serializable {
    private static WeCrossRPC weCrossRPC;

    public MockWeCross(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    public static MockResource getResource(String path, String accountName) {
        if (!ConsoleUtils.isValidPath(path)) {
            System.out.println("Please provide a valid path");
            return null;
        }
        return new MockResource(weCrossRPC, path, accountName);
    }

    public WeCrossRPC getWeCrossRPC() {
        return weCrossRPC;
    }

    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }
}
