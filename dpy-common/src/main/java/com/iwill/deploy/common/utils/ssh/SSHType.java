package com.iwill.deploy.common.utils.ssh;

public enum SSHType {
    SFTP("sftp"), //ChannelSftp
    SHELL("shell"), //ChannelShell
    EXEC("exec"), //ChannelExec
    TCPIP("direct-tcpip"),//ChannelDirectTCPIP
    SUBSYSTEM("subsystem");//ChannelSubsystem

    private String type;

    SSHType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
