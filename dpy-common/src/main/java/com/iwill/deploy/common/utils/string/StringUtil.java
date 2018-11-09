package com.iwill.deploy.common.utils.string;

import org.joda.time.DateTime;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class StringUtil {


    private static final AtomicInteger seqidx = new AtomicInteger(0);

    /**
     * 生成FTP相对路径
     *
     * @param rootPath 当前路径
     * @param fullName 完整路径
     * @return
     */
    public static String getFtpRelativePath(String rootPath, String fullName) {
        if (!isNOE(rootPath) && !isNOE(fullName)) {
            rootPath = rootPath.trim();
            fullName = fullName.trim().replace('\\', '/');// 所有\替换成/
            fullName = trimChar(fullName.substring(rootPath.length()), '/');
            return fullName;
        }
        return null;
    }

    /**
     * 生成本地相对路径
     *
     * @param rootPath 当前路径
     * @param fullName 完整路径
     * @return
     */
    public static String getLocalRelativePath(String rootPath, String fullName) {
        if (!isNOE(rootPath) && !isNOE(fullName)) {
            rootPath = rootPath.trim();
            if (File.separatorChar == '/') {
                fullName = fullName.trim().replace('\\', '/');// 所有\替换成/
            } else if (File.separatorChar == '\\') {
                fullName = fullName.trim().replace('/', '\\');// 所有/替换成\
            }
            fullName = trimChar(fullName.substring(rootPath.length()), File.separatorChar);
            return fullName;
        }
        return null;
    }

    /**
     * 字符串去除类型
     */
    public static enum TrimType {
        Left, Right, Both
    }

    /**
     * 去除字符串首尾的字符
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimChar(String string, char tchar) {
        return trimChar(string, tchar, TrimType.Both);
    }

    /**
     * 去除字符串左边的字符
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimLeftChar(String string, char tchar) {
        return trimChar(string, tchar, TrimType.Left);
    }

    /**
     * 去除字符串右边的字符
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimRightChar(String string, char tchar) {
        return trimChar(string, tchar, TrimType.Right);
    }

    /**
     * 去除字符串首尾的子字符串
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimString(String string, String tchar) {
        return trimString(string, tchar, TrimType.Both);
    }

    /**
     * 去除字符串左边的子字符串
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimLeftString(String string, String tchar) {
        return trimString(string, tchar, TrimType.Left);
    }

    /**
     * 去除字符串右边的子字符串
     *
     * @param string
     * @param tchar
     * @return
     */
    public static String trimRighString(String string, String tchar) {
        return trimString(string, tchar, TrimType.Right);
    }

    /**
     * 去除字符串的首尾字符
     *
     * @param string
     * @param tchar
     * @param type
     * @return 处理结果
     */
    private static String trimString(String string, String tchar, TrimType type) {

        if (string == null || "".endsWith(string))
            return string;

        StringBuilder sb = new StringBuilder(string);
        int length = string.length();// 源字符串长度
        int start = 0;// 开始位置
        int end = length;// 结束位置
        int current = start;// 当前位置
        int charlen = tchar.length();// 被去除的字符串的长度
        boolean startflag = false;
        boolean endflag = false;

        // 计算位置
        while (current <= length - charlen) {

            // 如果在头部找到字符，开始位置自增
            if (!startflag && sb.substring(current, current + charlen).equals(tchar)) start += charlen;
            else startflag = true;

            if (startflag && endflag || start >= end) break;

            // 如果在尾部找到字符，结束位置自减
            int pos = length - current - charlen;
            if (!endflag && sb.substring(pos, pos + charlen).equals(tchar)) end -= charlen;
            else endflag = true;

            if (startflag && endflag || start >= end) break;

            current += charlen;
        }

        if (type == TrimType.Both) {// 首尾字符全部去除掉
            return sb.substring(start, end);
        } else if (type == TrimType.Left) {// 去除字符串左边的字符
            return sb.substring(start, length);
        } else if (type == TrimType.Right) {// 去除字符串右边的字符
            return sb.substring(0, end);
        } else {// 参数错误直接返回
            return string;
        }
    }

    /**
     * 去除字符串的首尾字符
     *
     * @param string
     * @param tchar
     * @param type
     * @return 处理结果
     */
    private static String trimChar(String string, char tchar, TrimType type) {

        if (string == null || "".endsWith(string))
            return string;

        StringBuilder sb = new StringBuilder(string);
        int length = string.length();// 源字符串长度
        int start = 0;// 开始位置
        int end = length;// 结束位置
        int current = start;// 当前位置
        boolean startflag = false;
        boolean endflag = false;

        // 计算位置
        while (current < length) {

            // 如果在头部找到字符，开始位置自增
            if (!startflag && sb.charAt(current) == tchar) start++;
            else startflag = true;

            if (startflag && endflag || start >= end) break;

            // 如果在尾部找到字符，结束位置自减
            if (!endflag && sb.charAt(length - current - 1) == tchar) end--;
            else endflag = true;

            if (startflag && endflag || start >= end) break;

            current++;
        }

        if (type == TrimType.Both) {// 首尾字符全部去除掉
            return sb.substring(start, end);
        } else if (type == TrimType.Left) {// 去除字符串左边的字符
            return sb.substring(start, length);
        } else if (type == TrimType.Right) {// 去除字符串右边的字符
            return sb.substring(0, end);
        } else {// 参数错误直接返回
            return string;
        }
    }

    /**
     * 获取异常堆栈信息
     *
     * @param ex
     * @return
     */
    public static String getExceptionStackTrace(Exception ex) {
        StackTraceElement[] sts = ex.getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getName()).append(":").append(ex.getMessage());
        for (StackTraceElement st : sts)
            sb.append("\n\t").append(st.toString());
        return sb.toString();
    }

    /**
     * 按照顺序拼接所有的参数，生成文件路径path
     * <p>会将每一个参数，去掉右侧的分隔符，保证左侧有一个分隔符，只有分隔符的不进行拼接
     *
     * @param paths
     * @return
     */
    public static String generatePath(String... paths) {
        if (paths == null || paths.length == 0)
            return "";
        StringBuilder result = new StringBuilder();
        boolean isfirst = true;
        for (String path : paths) {
            if (isNOE(path))
                continue;

            if (File.separatorChar == '/')
                path = path.trim().replace('\\', '/');// 所有\替换成/

            if (File.separatorChar == '\\')
                path = path.trim().replace('/', '\\');// 所有/替换成\

            if (isfirst) {// 第一个参数仅去除尾部分隔符，因为共享文件路径是\\开头
                path = trimRightChar(path, File.separatorChar);
                if (!"".equals(path))
                    result.append(path);
                isfirst = false;
            } else {// 其余参数去除两侧分隔符后，在左侧拼接一个分隔符
                path = trimChar(path, File.separatorChar);
                if (!"".equals(path))
                    result.append(File.separator).append(path);
            }
        }
        return result.toString();
    }

    /**
     * 判断字符串是否为数字
     *
     * @param string
     * @return
     */
    public static boolean isNumeric(String string) {
        try {
            new BigDecimal(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为Boolean
     *
     * @param string
     * @return
     */
    public static boolean isBoolean(String string) {
        try {
            Boolean.parseBoolean(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 空值或空白字符串(Null Or Empty)，忽略空格
     *
     * @param string string
     * @return
     */
    public static boolean isNOE(Object string) {
        return string == null || String.valueOf(string).equals("");
    }

    public static boolean isAllNOE(Object... strings) {
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++)
                if (!isNOE(strings[i])) return false;
            return true;
        } else {
            return true;
        }
    }

    public static boolean isExistsNOE(Object... strings) {
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++)
                if (isNOE(strings[i])) return true;
            return false;
        } else {
            return false;
        }

    }

    /**
     * 字符串根据字节数进行左对齐补全，空格填充
     *
     * @param string 字符串
     * @param length 宽度
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String StringPadB(String encode, String string, int length) throws UnsupportedEncodingException {
        return StringPadB(encode, string, length, -1, ' ');
    }

    /**
     * 字符串根据字节数进行补全，空格填充
     *
     * @param string 字符串
     * @param length 宽度
     * @param type   对齐方式：左对齐：-1；右对齐：1
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String StringPadB(String encode, String string, int length, int type) throws UnsupportedEncodingException {
        return StringPadB(encode, string, length, type, ' ');
    }

    /**
     * 字符串根据字节数进行补全，超出部分不会截取
     *
     * @param string   字符串
     * @param length   宽度
     * @param type     对齐方式：左对齐：-1；右对齐：1
     * @param fillchar 填充字符
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String StringPadB(String encode, String string, int length, int type, char fillchar)
            throws UnsupportedEncodingException {
        if (string == null)
            string = ""; // 为空处理成空白
        if (fillchar > 255) // 不支持非ASCII字符的填充
            fillchar = ' ';
        String input = new String(string.getBytes(encode), encode);
        int width = input.getBytes(encode).length;
        // 如果字节数超出设定的值则直接返回
        if (width >= length)
            return input;
        // 生成字符字符串
        StringBuilder b = new StringBuilder();
        if (type == -1) {
            b.append(input);
            for (int i = 0; i < length - width; i++)
                b.append(fillchar);
        } else {
            for (int i = 0; i < length - width; i++)
                b.append(fillchar);
            b.append(input);
        }

        return b.toString();
    }


    public static String leftb(String encode, String inputString, Integer length) {
        return substringb(encode, inputString, 0, length);
    }

    public static String leftb(String inputString, Integer length) {
        return substringb("UTF-8", inputString, 0, length);
    }

    public static String substringb(String inputString, Integer offset, Integer length) {
        return substringb("UTF-8", inputString, offset, length);
    }

    public static String substringb(String encode, String inputString, Integer offset) {
        try {
            // 将字符串转换成目标字符集
            String string = new String(inputString.getBytes(encode), encode);
            byte[] bytes = string.getBytes(encode);
            Integer length = bytes.length - offset;
            return substringb(encode, inputString, offset, length);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("[substringb]不支持的字符集编码。", ex);
        }
    }

    public static String substringb(String encode, String inputString, Integer offset, Integer length) {

        try {

            if (inputString == null || "".equals(inputString)) return "";
            // 将字符串转换成目标字符集
            String string = new String(inputString.getBytes(encode), encode);

            byte[] bytes = string.getBytes(encode);

            // 如果参数超过本身长度，则使用本身长度
            if (length + offset > bytes.length)
                length = bytes.length - offset;
            else {
                // 计算最终长度，防止截断半个字符
                int len = 0;
                for (int i = 0; i < string.length(); i++) {
                    int l = (string.charAt(i) + "").getBytes(encode).length;
                    if (len + l <= length)
                        len += l;
                }
                length = len;
            }
            return new String(bytes, offset, length, encode);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("[substringb]不支持的字符集编码。", ex);
        }
    }

    public static Integer getIntValue(Object object) {
        if (isNOE(object))
            return null;
        if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            return Integer.valueOf(String.valueOf(object));
        } else {
            return null;
        }
    }

    public static String convertFtpPath(String path) {
        String temp = path;
        if (temp != null) {
            temp = temp.replace('\\', '/');
            temp += temp.endsWith("/") ? "" : "/";
        }
        return temp;
    }

    public static String getMethod(String action, String string) {
        if (isNOE(string)) return action;
        return action + string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * 获取流水号
     *
     * @return 24位流水号：17位日期[yyyyMMddHHmmssSSS]+7位自增序号[0000000-9999999]
     */
    public static String getSequenceNo() {
        return String.format("%s%07d", DateTime.now().toString("yyyyMMddHHmmssSSS"), seqidx.getAndIncrement() % 10000000);
    }

    /**
     * 获取前缀流水号
     *
     * @param prefix 前缀
     * @return 前缀+24位流水号：17位日期[yyyyMMddHHmmssSSS]+7位自增序号[0000000-9999999]
     */
    public static String getSequenceNo(String prefix) {
        return prefix + getSequenceNo();
    }

    /**
     * Test whether the given string matches the given substring at the given
     * index.
     *
     * @param str       the original string (or StringBuilder)
     * @param index     the index in the original string to start matching against
     * @param substring the substring to match at the given index
     */
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

}
