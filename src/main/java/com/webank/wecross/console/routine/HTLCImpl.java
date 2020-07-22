package com.webank.wecross.console.routine;

import com.webank.wecross.console.common.ConsoleUtils;
import com.webank.wecross.console.common.Hash;
import com.webank.wecross.console.common.HelpInfo;
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
    private Logger logger = LoggerFactory.getLogger(HTLCImpl.class);
    private static final String TRUE_FLAG = "true";
    private static final String NULL_FLAG = "null";
    private static final String SPLIT_REGEX = "##";
    private static final String SUCCESS_FLAG = "success";

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
            int doubleInterval = interval * 2;
            BigInteger t0 = now.add(BigInteger.valueOf(interval));
            BigInteger t1 = now.add(BigInteger.valueOf(doubleInterval));
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
        if (path == null) {
            return;
        }

        String account = params[2];
        String hash = params[3];

        Resource resource = ResourceFactory.build(weCrossRPC, path, account);
        String proposalInfo = resource.call("getProposalInfo", hash)[0].trim();
        if (NULL_FLAG.equals(proposalInfo)) {
            System.out.println("status: proposal not found!");
            return;
        }
        String[] proposalItems = proposalInfo.split(SPLIT_REGEX);
        BigInteger timelock = new BigInteger(proposalItems[2]);

        boolean selfUnlocked = TRUE_FLAG.equals(proposalItems[4]);
        boolean selfRolledback = TRUE_FLAG.equals(proposalItems[5]);
        boolean counterpartyUnlocked = TRUE_FLAG.equals(proposalItems[8]);

        if (selfUnlocked && counterpartyUnlocked) {
            System.out.println("status: succeeded!");
            return;
        }

        if (selfRolledback) {
            System.out.println("status: rolled back!");
            return;
        }

        BigInteger now = BigInteger.valueOf(System.currentTimeMillis() / 1000);
        if (timelock.compareTo(now) <= 0) {
            System.out.println("status: failed!");
            return;
        }

        System.out.println("status: ongoing!");
    }

    @Override
    public void newProposal(String[] params, Map<String, String> pathMaps) throws Exception {
        if (params.length == 1) {
            HelpInfo.promptHelp("newHTLCProposal");
            return;
        }
        if ("-h".equals(params[1]) || "--help".equals(params[1])) {
            HelpInfo.newProposaltHelp();
            return;
        }

        if (!checkProposal(params)) {
            return;
        }

        String path = ConsoleUtils.parsePath(params, pathMaps);
        if (path == null) {
            return;
        }
        String account = params[2];
        String[] args = new String[10];
        args[0] = ConsoleUtils.parseString(params[3]);
        for (int i = 1; i < 10; i++) {
            args[i] = ConsoleUtils.parseString(params[i + 4]);
        }

        TransactionResponse response =
                weCrossRPC.sendTransaction(path, account, "newProposal", args).send();
        Receipt receipt = response.getReceipt();
        if (response.getErrorCode() != StatusCode.SUCCESS) {
            ConsoleUtils.printJson(response.toString());
            return;
        } else if (response.getReceipt().getErrorCode() != StatusCode.SUCCESS) {
            logger.warn("TxError: " + response.getReceipt().toString());
            ConsoleUtils.printJson(receipt.toString());
            return;
        } else {
            System.out.println("Txhash: " + receipt.getHash());
            System.out.println("BlockNum: " + receipt.getBlockNumber());
            String result = receipt.getResult()[0].trim();
            if (SUCCESS_FLAG.equalsIgnoreCase(result)) {
                String txHash = response.getReceipt().getHash();
                long blockNum = response.getReceipt().getBlockNumber();
                setNewContractTxInfo(
                        path, account, ConsoleUtils.parseString(params[3]), txHash, blockNum);
                if (TRUE_FLAG.equalsIgnoreCase(params[5])) {
                    setSecret(
                            path,
                            account,
                            ConsoleUtils.parseString(params[3]),
                            ConsoleUtils.parseString(params[4]));
                }
                System.out.println("Result: create a htlc proposal successfully");
            } else {
                System.out.println("Result: " + result);
            }
        }
    }

    private boolean checkProposal(String[] params) throws NoSuchAlgorithmException {
        if (params.length != 14) {
            System.out.println("invalid number of parameters, 14 params needed");
            return false;
        }

        Hash hash = new Hash();
        if (TRUE_FLAG.equalsIgnoreCase(params[5])) {
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
            String path, String account, String hash, String txHash, long blockNum)
            throws Exception {
        TransactionResponse response =
                weCrossRPC
                        .sendTransaction(
                                path,
                                account,
                                "setNewProposalTxInfo",
                                hash,
                                txHash,
                                String.valueOf(blockNum))
                        .send();
        Receipt receipt = response.getReceipt();
        if (response.getErrorCode() != StatusCode.SUCCESS
                || receipt.getErrorCode() != StatusCode.SUCCESS) {
            if (receipt != null) {
                System.out.println("failed to setNewProposalTxInfo: " + receipt.getErrorMessage());
            } else {
                System.out.println("failed to setNewProposalTxInfo: " + response.getMessage());
            }
        } else {
            logger.info(
                    "newProposal succeeded, path: {}, txHash: {}, blockNum: {}",
                    path,
                    txHash,
                    blockNum);
        }
    }

    private void setSecret(String path, String account, String hash, String secret)
            throws Exception {
        TransactionResponse response =
                weCrossRPC.sendTransaction(path, account, "setSecret", hash, secret).send();
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

    @Override
    public void setWeCrossRPC(WeCrossRPC weCrossRPC) {
        this.weCrossRPC = weCrossRPC;
    }
}
