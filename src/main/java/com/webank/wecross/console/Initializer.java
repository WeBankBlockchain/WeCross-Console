package com.webank.wecross.console;

import com.webank.wecross.console.custom.BCOSCommand;
import com.webank.wecross.console.custom.FabricCommand;
import com.webank.wecross.console.exception.ErrorCode;
import com.webank.wecross.console.exception.WeCrossConsoleException;
import com.webank.wecross.console.routine.HTLCFace;
import com.webank.wecross.console.routine.HTLCImpl;
import com.webank.wecross.console.routine.XAFace;
import com.webank.wecross.console.routine.XAImpl;
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
    private XAFace xaFace;
    private HTLCFace htlcFace;
    private BCOSCommand bcosCommand;
    private FabricCommand fabricCommand;

    public void init() throws WeCrossConsoleException {
        WeCrossRPCService weCrossRPCService = new WeCrossRPCService();
        try {
            weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);
        } catch (WeCrossSDKException e) {
            logger.error("init wecross service failed: {}", e.getMessage());
            throw new WeCrossConsoleException(ErrorCode.INIT_WECROSS_SERVICE_ERROR, e.getMessage());
        }
        rpcFace = new RPCImpl();
        rpcFace.setWeCrossRPC(weCrossRPC);
        htlcFace = new HTLCImpl();
        htlcFace.setWeCrossRPC(weCrossRPC);
        xaFace = new XAImpl();
        xaFace.setWeCrossRPC(weCrossRPC);
        bcosCommand = new BCOSCommand(weCrossRPC);
        fabricCommand = new FabricCommand(weCrossRPC);
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

    public XAFace getXaFace() {
        return xaFace;
    }

    public void setXaFace(XAFace xaFace) {
        this.xaFace = xaFace;
    }

    public BCOSCommand getBcosCommand() {
        return bcosCommand;
    }

    public void setBcosCommand(BCOSCommand bcosCommand) {
        this.bcosCommand = bcosCommand;
    }

    public FabricCommand getFabricCommand() {
        return fabricCommand;
    }

    public void setFabricCommand(FabricCommand fabricCommand) {
        this.fabricCommand = fabricCommand;
    }
}
