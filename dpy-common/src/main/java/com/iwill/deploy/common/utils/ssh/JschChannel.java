package com.iwill.deploy.common.utils.ssh;

import com.iwill.deploy.common.exception.BusinessException;
import com.iwill.deploy.common.utils.ssh.entity.SftpConfig;
import com.iwill.deploy.common.utils.string.StringUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

@SuppressWarnings("unchecked")
public class JschChannel {

    private final JSch jsch = new JSch();
    private Session session;
    private Channel channel;
    private SftpConfig config;
    private SSHType type;

    public JschChannel() {
    }

    public JschChannel(SftpConfig config, SSHType type) {
        this.config = config;
        this.type = type;
        try {
            createSessionChannel();
        } catch (Exception e) {
            throw new BusinessException(String.format("Create JschChannel Error [%s]", config.toString()), e);
        }
    }


    private void createSessionChannel() throws JSchException {
        String host = config.getHost();
        Integer port = config.getPort();
        String username = config.getUser();
        String password = config.getPass();
        int timeout = config.getTimeout();
        // 建立会话
        session = jsch.getSession(username, host, port);
        if (!StringUtil.isNOE(password))
            session.setPassword(password);
        // 配置会话
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        if (timeout > 0)
            session.setTimeout(timeout);
        session.connect(); // 建立会话连接
        channel = session.openChannel(type.toString());
        channel.connect(); // 建立通道连接
    }

    public void close() {
        if (channel != null)
            channel.disconnect();
        channel = null;
        if (session != null)
            session.disconnect();
        session = null;
    }


    public Session getSession() {
        return session;
    }

    public JSch getJsch() {
        return jsch;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public <T extends Channel> T getChannel() {
        return (T) channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public SftpConfig getConfig() {
        return config;
    }

    public JschChannel setConfig(SftpConfig config) {
        this.config = config;
        return this;
    }

    public SSHType getType() {
        return type;
    }

    public JschChannel setType(SSHType type) {
        this.type = type;
        return this;
    }
}
