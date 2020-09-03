package com.webank.wecross.console.rpc;

import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.util.Map;
import java.util.Set;

public interface RPCFace {

    void setWeCrossRPC(WeCrossRPC weCrossRPC);

    void supportedStubs(String[] params) throws Exception;

    void listAccounts(String[] params) throws Exception;

    void listResources(String[] params) throws Exception;

    void getResourceStatus(String[] params, Map<String, String> pathMaps) throws Exception;

    void getResourceInfo(String[] params, Map<String, String> pathMaps) throws Exception;

    void call(String[] params, Map<String, String> pathMaps) throws Exception;

    void sendTransaction(String[] params, Map<String, String> pathMaps) throws Exception;

    void invoke(String[] params, Map<String, String> pathMaps) throws Exception;

    Set<String> getPaths();

    Set<String> getAccounts();
}
