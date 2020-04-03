package com.webank.wecross.console;

import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.routine.HTLCFace;
import com.webank.wecross.console.routine.HTLCImpl;
import com.webank.wecross.console.rpc.RPCFace;
import com.webank.wecross.console.rpc.RPCImpl;
import com.webank.wecrosssdk.exception.WeCrossSDKException;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer {

    private Logger logger = LoggerFactory.getLogger(Initializer.class);

    private WeCrossRPC weCrossRPC;
    private RPCFace rpcFace;
    private HTLCFace htlcFace;

    public void init() throws WeCrossConsoleException {
        WeCrossRPCService weCrossRPCService = new WeCrossRPCService();
        try {
            weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);
        } catch (WeCrossSDKException e) {
            logger.error("init wecross service failed: {}", e);
            throw new WeCrossConsoleException(ErrorCode.INIT_WECROSS_SERVICE_ERROR, e.getMessage());
        }
        rpcFace = new RPCImpl();
        rpcFace.setWeCrossRPC(weCrossRPC);
        htlcFace = new HTLCImpl();
        htlcFace.setWeCrossRPC(weCrossRPC);
    }

    public WeCrossRPC getWeCrossRPC() {
        return weCrossRPC;
    }

    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }

    public RPCFace getRpcFace() {
        return rpcFace;
    }

    public void setRpcFace(RPCFace rpcFace) {
        this.rpcFace = rpcFace;
    }

    public HTLCFace getHtlcFace() {
        return htlcFace;
    }

    public void setHtlcFace(HTLCFace htlcFace) {
        this.htlcFace = htlcFace;
    }
}
