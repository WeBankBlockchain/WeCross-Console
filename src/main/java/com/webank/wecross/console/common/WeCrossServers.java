package com.webank.wecross.console.common;

import com.webank.wecross.console.exception.ConsoleException;
import com.webank.wecross.console.exception.Status;
import java.util.HashMap;
import java.util.Map;

public class WeCrossServers {
    private Map<String, String> servers = new HashMap<>();

    private String defaultServer;

    public Map<String, String> getServers() {
        return servers;
    }

    public void checkKey() throws ConsoleException {
        String regex = "^[a-z0-9A-Z]+$";
        for (String key : servers.keySet()) {
            if (!key.matches(regex)) {
                throw new ConsoleException(
                        Status.ILLEGAL_KEY,
                        "Illegal key in servers config: "
                                + key
                                + "\nOnly numbers and letters are allowed");
            }
        }
    }

    public void setServers(Map<String, String> servers) {
        this.servers = servers;
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public void setDefaultServer(String defaultServer) {
        this.defaultServer = defaultServer;
    }
}
