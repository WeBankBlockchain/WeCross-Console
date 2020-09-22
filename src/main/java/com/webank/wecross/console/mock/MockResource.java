package com.webank.wecross.console.mock;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.PrintUtils;
import com.webank.wecrosssdk.exception.WeCrossSDKException;
import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.methods.Request;
import com.webank.wecrosssdk.rpc.methods.request.TransactionRequest;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockResource implements Serializable {

    private Logger logger = LoggerFactory.getLogger(MockResource.class);

    private Resource resource;

    public MockResource(WeCrossRPC weCrossRPC, String path, String account) {
        try {
            resource = ResourceFactory.build(weCrossRPC, path, account);
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
            Request<TransactionRequest> request = getRequest("call", method, args);
            logger.info("Call request: {}", request);
            TransactionResponse response = resource.call(request);
            PrintUtils.printTransactionResponse(response, true);
            logger.info("Call response: {}", response);
        } catch (WeCrossSDKException e) {
            logger.info("Call error: {}", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public void sendTransaction(String method, String... args) {
        try {
            Request<TransactionRequest> request = getRequest("sendTransaction", method, args);
            logger.info("SendTransaction request: {}", request);
            TransactionResponse response = resource.sendTransaction(request);
            PrintUtils.printTransactionResponse(response, false);
            logger.info("SendTransaction response: {}", response);
        } catch (WeCrossSDKException e) {
            logger.info("SendTransaction error: {}", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private Request<TransactionRequest> getRequest(String method, String func, String... args) {
        if (args.length == 0) {
            args = null;
        }
        Request<TransactionRequest> request = new Request<>();
        request.setMethod(method);
        request.setPath(resource.getPath());
        request.setAccount(resource.getAccount());
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setMethod(func);
        transactionRequest.setArgs(args);
        request.setData(transactionRequest);
        return request;
    }
}
