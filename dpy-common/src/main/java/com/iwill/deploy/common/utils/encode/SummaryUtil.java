package com.iwill.deploy.common.utils.encode;

import java.security.MessageDigest;

/**
 *
 * @author shumingl
 */
public class SummaryUtil {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public enum ALGORITHM {
        MD2("MD2"), MD5("MD5"), SHA1("SHA1"), SHA256("SHA-256"), SHA384("SHA-384"), SHA512("SHA-512");
        private String algorithm;

        ALGORITHM(String algorithm) {
            this.algorithm = algorithm;
        }

        public String toString() {
            return this.algorithm;
        }
    }

    /**
     * encode string
     *
     * @param algorithm 算法
     * @param str       字符串
     * @return String
     */
    public static String encode(ALGORITHM algorithm, String str) {
        if (str == null) return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm.toString());
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * encode By SHA1
     *
     * @param str 字符串
     * @return String
     */
    public static String SHA1(String str) {
        return encode(ALGORITHM.SHA1, str);
    }

    /**
     * encode By MD5
     *
     * @param str 字符串
     * @return String
     */
    public static String MD5(String str) {
        return encode(ALGORITHM.MD5, str);
    }

    /**
     * Takes the raw bytes from the digest and formats them correct.
     *
     * @param bytes the raw bytes from the digest.
     * @return the formatted bytes.
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

}