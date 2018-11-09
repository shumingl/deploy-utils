package com.iwill.deploy.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {

    /**
     * 接收固定长度数据
     *
     * @param istream 输入流
     * @param buffer  数据缓存
     * @throws IOException
     */
    public static int readBytes(InputStream istream, byte[] buffer) throws IOException {
        if (buffer == null)
            return -1;
        int total = buffer.length;
        int finished = 0;
        int ret;
        while (finished < total) {
            ret = istream.read(buffer, finished, total - finished);
            if (ret != -1)
                finished += ret;
            else
                break;
        }
        return finished;
    }

    /**
     * 接收固定长度数据
     *
     * @param istream 输入流
     * @param buffer  数据缓存
     * @throws IOException
     */
    public static int readFixedBytes(InputStream istream, byte[] buffer) throws IOException {
        if (buffer == null)
            return -1;
        int total = buffer.length;
        int finished = 0;
        int ret;
        while (finished < total) {
            ret = istream.read(buffer, finished, total - finished);
            if (ret > -1)
                finished += ret;
        }
        return finished;
    }

    /**
     * 接收固定长度数据，超时则异常
     *
     * @param istream 输入流
     * @param buffer  数据缓存
     * @param timeout 超时时间
     * @throws IOException
     */
    public static int readFixedBytes(InputStream istream, byte[] buffer, long timeout) throws IOException {
        long start = System.currentTimeMillis();
        int total = buffer.length;
        int finished = 0;
        int ret;
        while (finished < total) {
            ret = istream.read(buffer, finished, total - finished);
            if (ret > -1)
                finished += ret;
            else {
                if (System.currentTimeMillis() - start > timeout)
                    throw new IOException(String.format("data read timeout (%d ms).", timeout));
            }
        }
        return finished;
    }

    /**
     * 发送数据
     *
     * @param ostream 输出流
     * @param buffer  数据内容
     * @throws IOException
     */
    public static void writeBytes(OutputStream ostream, byte[] buffer) throws IOException {
        ostream.write(buffer);
        ostream.flush();
    }

    public static List<String> readLines(BufferedReader reader, int lineLimit) throws IOException {
        List<String> result = new ArrayList<>(lineLimit);
        for (int i = 0; i < lineLimit; i++) {
            String line = reader.readLine();
            if (line != null)
                result.add(line);
            else
                break;
        }
        return result;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }
}
