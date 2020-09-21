package com.webank.wecross.console.common;

import java.util.List;

public class TransactionInfo {
    String transactionID;
    String account;
    List<String> paths;

    public TransactionInfo(String transactionID, String account, List<String> paths) {
        this.transactionID = transactionID;
        this.paths = paths;
        this.account = account;
    }

    public TransactionInfo(String account, List<String> paths) {
        this.paths = paths;
        this.account = account;
    }

    public TransactionInfo() {}

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getAccount() {
        return account;
    }

    public void setAccounts(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(transactionID).append(" ");
        sb.append(account).append(" ");

        for (String path : paths) {
            sb.append(path).append(" ");
        }
        return sb.toString();
    }
}
