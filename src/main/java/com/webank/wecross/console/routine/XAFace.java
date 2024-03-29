package com.webank.wecross.console.routine;

import com.webank.wecrosssdk.rpc.WeCrossRPC;
import java.util.Map;

public interface XAFace {
    void setWeCrossRPC(WeCrossRPC weCrossRPC);

    void callTransaction(String[] params, Map<String, String> pathMaps) throws Exception;

    void execTransaction(String[] params, Map<String, String> pathMaps) throws Exception;

    void startTransaction(String[] params) throws Exception;

    void commitTransaction(String[] params) throws Exception;

    void rollbackTransaction(String[] params) throws Exception;

    void autoCommitXATransaction(String[] params, Map<String, String> pathMaps) throws Exception;

    void getXATransaction(String[] params) throws Exception;

    void getCurrentTransactionID(String[] params) throws Exception;

    void loadTransaction(String[] params) throws Exception;

    boolean isTransactionInfoExist(String txID, String[] paths) throws Exception;

    void listXATransactions(String[] params) throws Exception;
}
