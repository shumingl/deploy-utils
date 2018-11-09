package com.iwill.deploy.common.utils;

import com.iwill.deploy.common.utils.string.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {


    public static String parseField(Method method, String action) {
        return parseField(method.getName(), action);
    }

    public static String parseField(String method, String action) {
        if (StringUtil.isNOE(action))
            throw new RuntimeException("action is null, such as get/set/is/add/put etc.");
        int len = action.length();
        if (method.startsWith(action))
            return method.substring(len, len + 1).toLowerCase() + method.substring(len + 1);
        return null;
    }

    public static String[] parseFields(String[] methods, String action) {
        if (methods == null) return new String[0];
        final List<String> fields = new ArrayList<>();
        Arrays.asList(methods).forEach(method -> fields.add(parseField(method, action)));
        return (String[]) fields.toArray();
    }

    public static String[] parseFields(Method[] methods, String action) {
        if (methods == null) return new String[0];
        final List<String> fields = new ArrayList<>();
        Arrays.asList(methods).forEach(method -> fields.add(parseField(method, action)));
        return (String[]) fields.toArray();
    }

}
