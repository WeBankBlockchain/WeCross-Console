package com.webank.wecross.console.common;

import java.util.List;

public class TransactionInfo {
    String transactionID;
    List<String> paths;

    public TransactionInfo(String transactionID, List<String> paths) {
        this.transactionID = transactionID;
        this.paths = paths;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(transactionID).append(" ");
        for (String path : paths) {
            sb.append(path).append(" ");
        }
        return sb.toString();
    }

    public String toPathString() {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(path).append(" ");
        }
        return sb.toString();
    }
}
