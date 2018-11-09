package com.iwill.deploy.common.utils;

import com.iwill.deploy.common.exception.BusinessException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FileUtil {
    private static final Pattern WindowsPathPattern = Pattern.compile("^[A-Za-z]:.+");

    /**
     * 判断是否是Windows全路径文件名/目录名
     *
     * @param path 路径
     * @return
     */
    public static boolean isWindowsAbsolutelyPath(String path) {
        return WindowsPathPattern.matcher(path).matches();
    }

    public static String getFileName(String filename) {
        final String ws = "\\";
        final String ls = "/";
        if (filename.endsWith(ws) || filename.endsWith(ls))
            throw new BusinessException("非法文件名：" + filename);
        int idx = Math.max(filename.lastIndexOf(ws), filename.lastIndexOf(ls));
        return (idx == -1 ? filename : filename.substring(idx + 1));
    }

    public static String readText(String classpathFile, String encode) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathFile);
        File file = resource.getFile();//ResourceUtils.getFile(classpathFile);
        byte[] bytes = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
        return new String(bytes, encode);
    }

    public static byte[] readBytes(String classpathFile) throws IOException {
        File file = ResourceUtils.getFile(classpathFile);
        return Files.readAllBytes(Paths.get(file.getCanonicalPath()));
    }

    public static void main(String[] args) {
        String filename = "G:\\Documents\\大数据处理浅析.pptx";
        System.out.println(FileUtil.getFileName(filename));
        filename = "/data/app/app/ksgad/logs/dmb.log";
        System.out.println(FileUtil.isWindowsAbsolutelyPath(filename));
        filename = "G:\\Documents";
        System.out.println(FileUtil.getFileName(filename));
        filename = "G:\\Documents\\";
        System.out.println(FileUtil.getFileName(filename));
    }
}
