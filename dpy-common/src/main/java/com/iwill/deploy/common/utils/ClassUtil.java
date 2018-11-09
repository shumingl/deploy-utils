package com.iwill.deploy.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtil {

    public static List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) return null;
        List<Field> list = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        List<Field> superList = getAllFields(clazz.getSuperclass());
        if (superList != null) list.addAll(superList);
        return list;
    }

    public static List<Method> getAllMethods(Class<?> clazz) {
        if (clazz == null) return null;
        List<Method> list = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        List<Method> superList = getAllMethods(clazz.getSuperclass());
        if (superList != null) list.addAll(superList);
        return list;
    }

    /**
     * 判断一个类是否是其他类的子类或实现了其他接口
     *
     * @param clazz 要判断的类
     * @param SOIs  父类或接口 SuperClass Or Interface
     * @return
     */
    public static boolean isSubClass(Class<?> clazz, Class<?>... SOIs) {
        if (clazz == null || SOIs == null) return false;
        for (Class<?> soi : SOIs)
            if (soi.isAssignableFrom(clazz))
                return true;
        return false;
    }

}
