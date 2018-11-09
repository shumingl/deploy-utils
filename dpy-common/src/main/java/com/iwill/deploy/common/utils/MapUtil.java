package com.iwill.deploy.common.utils;

import com.iwill.deploy.common.utils.string.StringUtil;

import java.util.*;

public class MapUtil {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> of(Object... valuePairs) {
        Map<K, V> map = new HashMap<>();
        if (valuePairs != null && valuePairs.length > 0) {
            for (int i = 0; i < valuePairs.length; i += 2) {
                Object keyObj = valuePairs[i];
                Object valObj = null;
                if (i + 1 < valuePairs.length)
                    valObj = valuePairs[i + 1];
                if (keyObj != null && valObj != null)
                    map.put((K) keyObj, (V) valObj);
            }
        }
        return map;
    }

    /**
     * 检查参数
     *
     * @param args 参数
     */
    public static void checkNullArgs(Map<String, ?> args, String... names) {
        if (args == null || args.size() == 0)
            throw new IllegalArgumentException("参数信息为空");
        for (String name : names) {
            if (!args.containsKey(name))
                throw new IllegalArgumentException("缺失指定参数：" + name);
        }
        List<String> values = new ArrayList<>();
        for (String name : names) {
            values.add(String.valueOf(args.get(name)));
        }
        if (StringUtil.isExistsNOE(values.toArray()))
            throw new IllegalArgumentException("部分参数空值：" + Arrays.asList(names));
    }

    /**
     * 以路径的方式获取数据
     *
     * @param data map
     * @param path 路径
     * @return
     */
    public static Object get(Map<?, ?> data, String path) {
        if (data == null) return null;
        if (StringUtil.isNOE(path)) return data;
        int idx = path.indexOf("/");
        // ===================获取前缀路径，判断为List的情况和Map的情况
        String tempPrefix = path.contains("/") ? path.substring(0, idx) : path;
        int prefixIdx = 0;
        String prefix = "";
        if (tempPrefix.contains("[") && tempPrefix.endsWith("]")) {// List
            int is = tempPrefix.indexOf('[');
            int ie = tempPrefix.indexOf(']');
            if (is > 0) prefix = tempPrefix.substring(0, is);
            // 获取索引号
            String sIdx = ie > is ? tempPrefix.substring(is + 1, ie) : "";
            if (!StringUtil.isNOE(sIdx))
                prefixIdx = Integer.parseInt(sIdx.trim());
        } else { // Map
            prefix = tempPrefix;
        }
        // 如果不含/
        if (!path.contains("/"))
            return data.get(prefix);

        // ===================获取后缀路径
        String suffix = path.substring(idx + 1);
        Object subdata = data.get(prefix);// 获取前缀下的Object
        if (subdata == null) return null;
        if (subdata instanceof Map) { // 如果是Map
            return get((Map<?, ?>) subdata, suffix);
        } else if (subdata instanceof List) { // 如果是List
            return get((Map<?, ?>) ((List<?>) subdata).get(prefixIdx), suffix);
        } else { // 其他
            throw new IllegalArgumentException(String.format("%s is not a java.util.Map or java.util.List", prefix));
        }
    }
}
