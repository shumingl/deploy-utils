package com.iwill.deploy.common.utils.ssh.entity;

import com.iwill.deploy.common.utils.string.StringUtil;

public class SshConfig {
    private String host;
    private int port;
    private String user;
    private String pass;
    private String remote;
    private int timeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = StringUtil.convertFtpPath(remote);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = Integer.valueOf(timeout);
    }

    public String toString() {
        return String.format("%s@%s:%d:%s", user, host, port, remote);
    }

}
