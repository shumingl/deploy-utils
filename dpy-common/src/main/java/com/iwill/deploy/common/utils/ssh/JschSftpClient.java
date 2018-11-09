package com.iwill.deploy.common.utils.ssh;

import com.iwill.deploy.common.exception.BusinessException;
import com.iwill.deploy.common.utils.FileUtil;
import com.iwill.deploy.common.utils.osop.OSinfo;
import com.iwill.deploy.common.utils.ssh.entity.SftpConfig;
import com.iwill.deploy.common.utils.ssh.entity.SftpFileStat;
import com.iwill.deploy.common.utils.string.StringUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JschSftpClient implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(JschSftpClient.class);
    private JschChannel jschChannel;
    private SftpConfig config;

    public JschSftpClient() {
    }

    public JschSftpClient(SftpConfig config) {
        this.config = config;
        this.jschChannel = new JschChannel(config, SSHType.SFTP);
    }

    /**
     * 进入到路径
     *
     * @param path 路径
     * @return
     */
    public boolean cd(String path) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            sftp.cd(path);
            return true;
        } catch (SftpException e) {
            throw new BusinessException(String.format("cd error: %s", path), e);
        }
    }

    /**
     * 创建路径
     *
     * @param path 路径
     * @return
     */
    public boolean mkdirs(String path) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            String sc = "/";
            if (!path.contains(sc) || path.startsWith(sc)) {//无分隔符或绝对路径
                if (!exists(path, "dir")) sftp.mkdir(path);
                return true;
            }
            // 相对路径，且带有分隔符
            int start = 0;
            int current = path.indexOf(sc, start);
            while (current > 0) {
                String subPath = path.substring(start, current);
                logger.info("path.substring({}, {})={}", start, current, subPath);
                if (!exists(subPath, "dir"))
                    sftp.mkdir(subPath); // 逐级创建
                start = current + 1;
                current = path.indexOf(sc, start);
            }
            return true;

        } catch (SftpException e) {
            throw new BusinessException(String.format("mkdirs error: %s->%s", config.getRemote(), path), e);
        }

    }

    /**
     * 判断文件是否存在
     *
     * @param serverFile 文件名
     * @return
     */
    public boolean exists(String serverFile) {
        return exists(serverFile, "file");
    }

    /**
     * 判断文件或路径是否存在
     *
     * @param pathfile 路径名
     * @param type     类型 file/dir
     * @return
     */
    public boolean exists(String pathfile, String type) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            String serverPath = config.getRemote();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!pathfile.startsWith("/"))
                pathfile = serverPath + pathfile;
            SftpATTRS attrs = sftp.lstat(pathfile);
            if ("file".equalsIgnoreCase(type))
                return !attrs.isDir() && !attrs.isLink();
            else if ("dir".equalsIgnoreCase(type))
                return attrs.isDir();
            else
                throw new BusinessException(String.format("error type[%s], need file/dir.", type));
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id)
                return false;
            else
                throw new BusinessException(String.format("检查文件%s信息异常", pathfile), e);
        }
    }

    public SftpATTRS attr(String serverFile) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            String serverPath = config.getRemote();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!serverFile.startsWith("/"))
                serverFile = serverPath + serverFile;
            return sftp.lstat(serverFile);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id)
                return null;
            throw new BusinessException(String.format("exists error: %s", serverFile), e);
        }
    }

    /**
     * 批量获取文件属性
     *
     * @param serverFiles 文件名
     * @return
     */
    public List<SftpFileStat> attrs(String... serverFiles) {
        if (serverFiles == null || serverFiles.length == 0) return null;
        ChannelSftp sftp = jschChannel.getChannel();
        String serverFile = "";
        try {
            List<SftpFileStat> attrs = new ArrayList<>();
            String serverPath = config.getRemote();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            for (String file : serverFiles) {
                serverFile = file;
                if (serverFile != null) {
                    if (!serverFile.startsWith("/"))
                        serverFile = serverPath + serverFile;
                    try {
                        SftpATTRS attrs1 = sftp.lstat(serverFile);
                        attrs.add(new SftpFileStat(file, attrs1));
                    } catch (SftpException e) {
                        attrs.add(new SftpFileStat(file, null));
                    }
                } else {
                    attrs.add(null);
                }
            }
            return attrs;
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id)
                return null;
            throw new BusinessException(String.format("批量获取文件信息异常: %s", serverFile), e);
        }
    }

    /**
     * 下载文件
     *
     * @param serverFile 服务器文件
     * @param localFile  本地文件名
     */
    public void download(String serverFile, String localFile) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            String serverPath = config.getRemote();
            String localPath = config.getLocal();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!StringUtil.isNOE(localPath)) sftp.lcd(localPath);
            if (!serverFile.startsWith("/"))
                serverFile = serverPath + serverFile;
            sftp.get(serverFile, localFile);
        } catch (SftpException e) {
            throw new BusinessException("文件下载异常", e);
        }
    }

    /**
     * 下载文件
     *
     * @param fileMaps 一对一文件下载数组（2的倍数） ，如：源文件1 目标文件1 源文件2 目标文件2
     */
    public void downloads(String... fileMaps) {
        if (fileMaps.length == 0) return;
        if (fileMaps.length % 2 != 0)
            throw new BusinessException("参数个数错误，需要为2的倍数，files.length=" + fileMaps.length);
        // 组织文件数据
        List<String> srcFiles = new ArrayList<>();
        List<String> dstFiles = new ArrayList<>();
        for (int i = 0; i < fileMaps.length; i += 2) {
            String srcFile = fileMaps[i];
            String dstFile = fileMaps[i + 1];
            if (!StringUtil.isNOE(srcFile) && !StringUtil.isNOE(dstFile)) {
                srcFiles.add(srcFile);
                dstFiles.add(dstFile);
            }
        }
        downloads(srcFiles, dstFiles);
    }

    /**
     * 下载文件
     *
     * @param srcFiles 服务端文件
     * @param dstFiles 客户端/目标文件
     */
    public void downloads(List<String> srcFiles, List<String> dstFiles) {
        downloads(null, srcFiles, null, dstFiles);
    }

    /**
     * 多文件下载
     *
     * @param serverPath  服务端路径，找不到则使用config.getRemotedir()
     * @param serverFiles 服务端文件名，文件名可以用全路径文件名或相对serverPath文件名
     * @param localPath   本地路径，找不到则使用config.getLocaldir()
     * @param localFiles  本地文件名，文件名
     */
    public void downloads(String serverPath, List<String> serverFiles, String localPath, List<String> localFiles) {
        if (serverFiles == null || localFiles == null || serverFiles.size() == 0) return;
        if (serverFiles.size() != localFiles.size())
            throw new BusinessException(String.format("参数个数错误，文件数量不相等，serverFiles: %d, localFiles: %d",
                    serverFiles.size(), localFiles.size()));
        ChannelSftp sftp = jschChannel.getChannel();
        String curfile = "";
        try {
            // =================================================检查参数
            if (StringUtil.isNOE(serverPath)) serverPath = config.getRemote();
            if (StringUtil.isNOE(localPath)) localPath = config.getLocal();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!StringUtil.isNOE(localPath)) sftp.lcd(localPath);
            serverPath = StringUtil.convertFtpPath(serverPath);

            List<String> srcFiles = new ArrayList<>();
            List<String> dstFiles = new ArrayList<>();

            // =================================================处理文件名
            for (int i = 0; i < serverFiles.size(); i++) {
                String srcfile = serverFiles.get(i);
                curfile = srcfile;
                if (!StringUtil.isNOE(srcfile)) {
                    // 源文件-全路径
                    if (!srcfile.startsWith("/")) srcfile = serverPath + srcfile;
                    // 目标文件-全路径
                    String dstfile = localFiles.get(i);
                    if (StringUtil.isNOE(dstfile)) {
                        String dstname = FileUtil.getFileName(srcfile);
                        dstfile = StringUtil.generatePath(localPath, dstname);
                    } else {
                        if (OSinfo.isWindows()) { // 如果是Windows
                            if (!FileUtil.isWindowsAbsolutelyPath(dstfile)) // 相对路径
                                dstfile = StringUtil.generatePath(localPath, dstfile);
                            // 绝对路径
                        } else // 非Windows路径，/开始视为绝对路径
                            dstfile = dstfile.startsWith("/") ? dstfile : StringUtil.generatePath(localPath, dstfile);
                    }
                    // 临时文件
                    srcFiles.add(srcfile);
                    dstFiles.add(dstfile);
                    sftp.get(srcfile, dstfile); // =============下载文件
                }
            }
            // =================================================文件检查
            for (int i = 0; i < dstFiles.size(); i++) {
                String srcfile = srcFiles.get(i);
                curfile = srcfile;
                File dst = new File(dstFiles.get(i));
                if (!dst.exists())
                    throw new BusinessException(
                            String.format("文件%s下载失败。目标文件%s不存在", srcfile, dstFiles.get(i)));
                SftpATTRS attrs = sftp.lstat(srcfile);
                long srclength = attrs.getSize();
                long dstlength = dst.length();
                if (dstlength != srclength)
                    throw new BusinessException(
                            String.format("文件%s下载失败。文件大小检查失败：server.size=%d, local.size=%d.",
                                    srcfile, dstlength, srclength));
            }
        } catch (SftpException e) {
            throw new BusinessException("文件批量下载异常：" + curfile, e);
        }
    }

    /**
     * 文件上传
     *
     * @param localFile  本地文件
     * @param serverFile 服务器文件名
     */
    public void upload(String localFile, String serverFile) {
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            String serverPath = config.getRemote();
            String localPath = config.getLocal();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!StringUtil.isNOE(localPath)) sftp.lcd(localPath);
            if (!serverFile.startsWith("/"))
                serverFile = serverPath + serverFile;
            sftp.put(localFile, serverFile);
        } catch (SftpException e) {
            throw new BusinessException("文件上传异常：" + localFile, e);
        }
    }

    /**
     * 上传文件
     *
     * @param fileMaps 一对一文件数组（2的倍数） ，如：源文件1 目标文件1 源文件2 目标文件2
     */
    public void uploads(String... fileMaps) {
        if (fileMaps.length == 0) return;
        if (fileMaps.length % 2 != 0)
            throw new BusinessException("参数个数错误，需要为2的倍数，files.length=" + fileMaps.length);

        // 组织文件数据
        List<String> srcFiles = new ArrayList<>();
        List<String> dstFiles = new ArrayList<>();
        for (int i = 0; i < fileMaps.length; i += 2) {
            String srcFile = fileMaps[i];
            String dstFile = fileMaps[i + 1];
            if (!StringUtil.isNOE(srcFile) && !StringUtil.isNOE(dstFile)) {
                srcFiles.add(srcFile);
                dstFiles.add(dstFile);
            }
        }
        uploads(srcFiles, dstFiles);
    }

    /**
     * 文件批量上传
     *
     * @param srcFiles 本地文件
     * @param dstFiles 服务器文件名
     */
    public void uploads(List<String> srcFiles, List<String> dstFiles) {
        uploads(null, srcFiles, null, dstFiles);
    }

    /**
     * 多文件下载
     *
     * @param localPath   本地路径，找不到则使用config.getLocaldir()
     * @param localFiles  本地文件名，文件名
     * @param serverPath  服务端路径，找不到则使用config.getRemotedir()
     * @param serverFiles 服务端文件名，文件名可以用全路径文件名或相对serverPath文件名
     */
    public void uploads(String localPath, List<String> localFiles, String serverPath, List<String> serverFiles) {
        if (serverFiles == null || localFiles == null || serverFiles.size() == 0) return;
        if (serverFiles.size() != localFiles.size())
            throw new BusinessException(
                    String.format("参数个数错误，文件数量不相等。localFiles: %d, serverFiles: %d.",
                            localFiles.size(), serverFiles.size()));
        ChannelSftp sftp = jschChannel.getChannel();
        try {
            // =================================================检查参数
            if (StringUtil.isNOE(serverPath)) serverPath = config.getRemote();
            if (StringUtil.isNOE(localPath)) localPath = config.getLocal();
            if (!StringUtil.isNOE(serverPath)) sftp.cd(serverPath);
            if (!StringUtil.isNOE(localPath)) sftp.lcd(localPath);
            serverPath = StringUtil.convertFtpPath(serverPath);

            List<String> srcFiles = new ArrayList<>();
            List<String> dstFiles = new ArrayList<>();

            // =================================================处理文件名
            for (int i = 0; i < localFiles.size(); i++) {
                String srcfile = localFiles.get(i);
                if (!StringUtil.isNOE(srcfile)) {
                    // 源文件-全路径
                    if (OSinfo.isWindows()) { // 如果是Windows相对路径
                        if (!FileUtil.isWindowsAbsolutelyPath(srcfile))
                            srcfile = StringUtil.generatePath(localPath, srcfile);
                    } else // 非Windows路径，/开始视为绝对路径
                        srcfile = srcfile.startsWith("/") ? srcfile : StringUtil.generatePath(localPath, srcfile);

                    String dstfile = serverFiles.get(i);
                    if (StringUtil.isNOE(dstfile)) { // 目标文件名为空时，使用源文件名
                        String dstname = FileUtil.getFileName(srcfile);
                        dstfile = serverPath + dstname;
                    } else {
                        // 目标文件名不空时，生成全路径文件名
                        if (!dstfile.startsWith("/")) dstfile = serverPath + dstfile;
                    }
                    // 临时文件
                    srcFiles.add(srcfile);
                    dstFiles.add(dstfile);
                    sftp.put(srcfile, dstfile); // =============上传文件
                }
            }
            // =================================================文件检查
            for (int i = 0; i < srcFiles.size(); i++) {
                String srcfile = srcFiles.get(i);
                File src = new File(srcfile);
                SftpATTRS attrs = sftp.lstat(dstFiles.get(i));
                long dstlength = attrs.getSize();
                long srclength = src.length();
                if (dstlength != srclength)
                    throw new BusinessException(
                            String.format("文件%s上传失败。文件大小检查失败：local.size=%d, server.size=%d.",
                                    srcfile, srclength, dstlength));
            }
        } catch (SftpException e) {
            throw new BusinessException("文件批量上传异常", e);
        }
    }

    public void close() {
        jschChannel.close();
    }

    public SftpConfig getConfig() {
        return config;
    }

    public void setConfig(SftpConfig config) {
        this.config = config;
        this.jschChannel = new JschChannel(config, SSHType.SFTP);
    }

    public JschChannel getJschChannel() {
        return jschChannel;
    }

    public void setJschChannel(JschChannel jschChannel) {
        this.jschChannel = jschChannel;
    }
}
