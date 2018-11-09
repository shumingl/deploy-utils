package com.iwill.deploy.common.utils.ssh.entity;

import com.jcraft.jsch.SftpATTRS;

public class SftpFileStat {
    private String filename;
    private SftpATTRS attr;

    public SftpFileStat(String filename, SftpATTRS attr) {
        this.filename = filename;
        this.attr = attr;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SftpATTRS getAttr() {
        return attr;
    }

    public void setAttr(SftpATTRS attr) {
        this.attr = attr;
    }
}
