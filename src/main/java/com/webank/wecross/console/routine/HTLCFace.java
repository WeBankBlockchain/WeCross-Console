package com.webank.wecross.console.routine;

import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.util.Map;

public interface HTLCFace {
    void setWeCrossRPC(WeCrossRPC weCrossRPC);

    void genTimelock(String[] params);

    void genSecretAndHash(String[] params) throws Exception;

    void newContract(String[] params, Map<String, String> pathMaps) throws Exception;
}
