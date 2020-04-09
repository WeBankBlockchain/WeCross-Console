package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.Hash;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.rpc.RPCImpl;
import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.Receipt;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTLCImpl implements HTLCFace {
    private WeCrossRPC weCrossRPC;
    private Logger logger = LoggerFactory.getLogger(RPCImpl.class);

    @Override
    public void generateTimelock(String[] params) {
        if (params.length == 1) {
            HelpInfo.promptHelp("generateTimelock");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.generateTimelockHelp();
            return;
        }

        int interval = Integer.parseInt(params[1]);
        if (interval < 300) {
            System.out.println("condition: interval > 300");
        } else {
            BigInteger now = BigInteger.valueOf(System.currentTimeMillis() / 1000);
            BigInteger t0 = now.add(BigInteger.valueOf(interval * 2));
            BigInteger t1 = now.add(BigInteger.valueOf(interval));
            System.out.println("timelock0: " + t0);
            System.out.println("timelock1: " + t1);
        }
    }

    @Override
    public void generateSecretAndHash(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.generateSecretAndHashHelp();
            return;
        }

        Hash hash = new Hash();
        String secret = hash.getRandom(32);
        System.out.println("secret: " + secret);
        System.out.println("hash  : " + hash.sha256(secret));
    }

    @Override
    public void newContract(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("newContract");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.newContractHelp();
            return;
        }
        if (params.length != 14) {
            System.out.println("invalid number of parameters, 14 params needed");
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) return;
        String accountName = params[2];
        String[] args = new String[10];
        args[0] = ConsoleUtils.parseString(params[3]);
        for (int i = 1; i < 10; i++) {
            args[i] = ConsoleUtils.parseString(params[i + 4]);
        }
        TransactionResponse response =
                weCrossRPC.sendTransaction(path, accountName, "newContract", args).send();
        Receipt receipt = response.getReceipt();
        if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
            return;
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(receipt.toString());
            return;
        } else {
            System.out.println("Txhash: " + receipt.getHash());
            System.out.println("BlockNum: " + receipt.getBlockNumber());
            System.out.println("Result: " + Arrays.toString(receipt.getResult()));
        }

        if (receipt.getResult()[0].trim().equalsIgnoreCase("success")) {
            String txHash = response.getReceipt().getHash();
            long blockNum = response.getReceipt().getBlockNumber();
            setNewContractTxInfo(
                    path, accountName, ConsoleUtils.parseString(params[3]), txHash, blockNum);
            if (params[5].equalsIgnoreCase("true")) {
                setSecret(
                        path,
                        accountName,
                        ConsoleUtils.parseString(params[3]),
                        ConsoleUtils.parseString(params[4]));
            }
        }
    }

    private void setNewContractTxInfo(
            String path, String accountName, String hash, String txHash, long blockNum)
            throws Exception {
        TransactionResponse response =
                weCrossRPC
                        .sendTransaction(
                                path,
                                accountName,
                                "setNewContractTxInfo",
                                hash,
                                txHash,
                                String.valueOf(blockNum))
                        .send();
        if (response == null
                || response.getErrorCode() != StatusCode.SUCCESS
                || response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            System.out.println(
                    "failed to setNewContractTxInfo: " + response.getReceipt().getErrorMessage());
        } else {
            logger.info(
                    "newContract succeeded, path: {}, txHash: {}, blockNum: {}",
                    path,
                    txHash,
                    blockNum);
        }
    }

    private void setSecret(String path, String accountName, String hash, String secret)
            throws Exception {
        TransactionResponse response =
                weCrossRPC.sendTransaction(path, accountName, "setSecret", hash, secret).send();
        if (response == null
                || response.getErrorCode() != StatusCode.SUCCESS
                || response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            System.out.println("failed to setSecret: " + response.getReceipt().getErrorMessage());
        } else {
            logger.info("setSecret succeeded, path: {}, hashs: {}, secret: {}", path, hash, secret);
        }
    }

    public WeCrossRPC getWeCrossRPC() {
        return weCrossRPC;
    }

    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }
}
