package com.webank.wecross.console.common;

import java.util.List;

public class TransactionInfo {
    String transactionID;
    List<String> accounts;
    List<String> paths;

    public TransactionInfo(String transactionID, List<String> accounts, List<String> paths) {
        this.transactionID = transactionID;
        this.paths = paths;
        this.accounts = accounts;
    }

    public TransactionInfo(List<String> accounts, List<String> paths) {
        this.paths = paths;
        this.accounts = accounts;
    }

    public TransactionInfo() {
    }

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

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        StringBuilder sb =new StringBuilder();
        sb.append(transactionID).append(" ");
        for (String account : accounts) {
            sb.append(account).append(" ");
        }
        for (String path : paths) {
            sb.append(path).append(" ");
        }
        return sb.toString();
    }
}
