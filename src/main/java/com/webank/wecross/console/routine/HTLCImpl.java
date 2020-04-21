package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.Default;
import com.webank.wecross.console.common.Hash;
import com.webank.wecross.console.common.HelpInfo;
import com.webank.wecross.console.rpc.RPCImpl;
import com.webank.wecrosssdk.common.StatusCode;
import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.common.Receipt;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTLCImpl implements HTLCFace {
    private WeCrossRPC weCrossRPC;
    private Logger logger = LoggerFactory.getLogger(RPCImpl.class);

    @Override
    public void genTimelock(String[] params) {
        if (params.length == 1) {
            HelpInfo.promptHelp("genTimelock");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.genTimelockHelp();
            return;
        }

        int interval = Integer.parseInt(params[1]);
        if (interval < 300) {
            System.out.println("condition: interval > 300");
        } else {
            BigInteger now = BigInteger.valueOf(System.currentTimeMillis() / 1000);
            BigInteger t0 = now.add(BigInteger.valueOf(interval + interval));
            BigInteger t1 = now.add(BigInteger.valueOf(interval));
            System.out.println("timelock0: " + t0);
            System.out.println("timelock1: " + t1);
        }
    }

    @Override
    public void genSecretAndHash(String[] params) throws Exception {
        if (params.length != 1) {
            HelpInfo.genSecretAndHashHelp();
            return;
        }

        Hash hash = new Hash();
        String secret = hash.getRandom(32);
        System.out.println("secret: " + secret);
        System.out.println("hash  : " + hash.sha256(secret));
    }

    @Override
    public void checkTransferStatus(String[] params, Map<String, String> pathMaps)
            throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("checkTransferStatus");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.checkTransferStatusHelp();
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) return;

        String accountName = params[2];
        String hash = params[3];

        Resource resource = ResourceFactory.build(weCrossRPC, path, accountName);
        BigInteger timelock;
        try {
            String timelockStr = resource.call("getSelfTimelock", hash)[0].trim();
            if (timelockStr.equalsIgnoreCase("")) {
                System.out.println("status: hash not found!");
                return;
            }
            timelock = new BigInteger(timelockStr);
        } catch (Exception e) {
            System.out.println("status: hash not found!");
            return;
        }

        if (resource.call("getSelfUnlockStatus", hash)[0].trim().equalsIgnoreCase(Default.TRUE_FLAG)
                && resource.call("getCounterpartyUnlockStatus", hash)[0]
                        .trim()
                        .equalsIgnoreCase(Default.TRUE_FLAG)) {
            System.out.println("status: succeeded!");
            return;
        }

        if (resource.call("getCounterpartyRollbackStatus", hash)[0]
                .trim()
                .equalsIgnoreCase(Default.TRUE_FLAG)) {
            System.out.println("status: rolled back!");
            return;
        }

        if (resource.call("getSelfLockStatus", hash)[0]
                .trim()
                .equalsIgnoreCase(Default.FALSE_FLAG)) {

            BigInteger now = BigInteger.valueOf(System.currentTimeMillis() / 1000);
            if (timelock.compareTo(now) <= 0) {
                System.out.println("status: failed!");
                return;
            }
        }

        System.out.println("status: ongoing!");
    }

    @Override
    public void newContract(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("newHTLCTransferProposal");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.newContractHelp();
            return;
        }

        if (!checkContract(params)) {
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
            String result = receipt.getResult()[0].trim();
            if (result.equalsIgnoreCase("success")) {
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
                System.out.println("Result: create a htlc transfer proposal successfully");
            } else {
                System.out.println("Result: " + result);
            }
        }
    }

    private boolean checkContract(String[] params) throws NoSuchAlgorithmException {
        if (params.length != 14) {
            System.out.println("invalid number of parameters, 14 params needed");
            return false;
        }

        Hash hash = new Hash();
        if (params[5].equalsIgnoreCase("true")) {
            if (!params[3].equals(hash.sha256(params[4]))) {
                System.out.println("hash not matched");
                return false;
            }
        }

        BigInteger amount0 = new BigInteger(params[8]);
        BigInteger amount1 = new BigInteger(params[12]);

        if (amount0.compareTo(BigInteger.valueOf(0)) > 0
                && amount1.compareTo(BigInteger.valueOf(0)) > 0) {
            return true;
        } else {
            System.out.println("transfer amount must be greater than 0");
            return false;
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
        Receipt receipt = response.getReceipt();
        if (response.getErrorCode() != StatusCode.SUCCESS
                || receipt.getErrorCode() != StatusCode.SUCCESS) {
            if (receipt != null) {
                System.out.println("failed to setNewContractTxInfo: " + receipt.getErrorMessage());
            } else {
                System.out.println("failed to setNewContractTxInfo: " + response.getMessage());
            }
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
        Receipt receipt = response.getReceipt();
        if (response.getErrorCode() != StatusCode.SUCCESS
                || receipt.getErrorCode() != StatusCode.SUCCESS) {
            if (receipt != null) {
                System.out.println("failed to setSecret: " + receipt.getErrorMessage());
            } else {
                System.out.println("failed to setSecret: " + response.getMessage());
            }
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
