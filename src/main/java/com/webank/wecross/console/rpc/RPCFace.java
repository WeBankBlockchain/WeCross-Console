package com.webank.wecross.console.rpc;

import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.util.Map;
import java.util.Set;

public interface RPCFace {

    void setWeCrossRPC(WeCrossRPC weCrossRPC);

    void supportedStubs(String[] params) throws Exception;

    void listAccount(String[] params) throws Exception;

    void listResources(String[] params) throws Exception;

    void getResourceStatus(String[] params, Map<String, String> pathMaps) throws Exception;

    void getResourceInfo(String[] params, Map<String, String> pathMaps) throws Exception;

    void call(String[] params, Map<String, String> pathMaps) throws Exception;

    void sendTransaction(String[] params, Map<String, String> pathMaps) throws Exception;

    void invoke(String[] params, Map<String, String> pathMaps) throws Exception;

    void login(String[] params) throws Exception;

    void internalLogin() throws Exception;

    void registerAccount(String[] params) throws Exception;

    void addChainAccount(String[] params) throws Exception;

    void setDefaultAccount(String[] params) throws Exception;

    void logout(String[] params) throws Exception;

    Set<String> getPaths();
}
