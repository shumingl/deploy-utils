package com.iwill.deploy.common.config;

import com.iwill.deploy.common.lang.DynamicHashMap;
import com.iwill.deploy.common.utils.spring.PlaceholderHelper;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ConfigureLoader {
    private static final DynamicHashMap<String, Object> config = new DynamicHashMap<>();
    private static final PlaceholderHelper placeholderHelper = new PlaceholderHelper("${", "}", ":", true);

    /**
     * 从多个属性文件中加载配置信息
     *
     * @param classpathFiles 类路径下的配置文件
     * @throws IOException 文件读取异常
     */
    public static void loadConfig(String... classpathFiles) throws IOException {
        for (String classpathFile : classpathFiles) {
            Properties properties = new Properties();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ClassPathResource(classpathFile).getInputStream()))) {
                properties.load(reader);
                for (String propertyName : properties.stringPropertyNames())
                    config.put(propertyName, properties.getProperty(propertyName));
            }
        }
        for (String key : config.keySet()) {
            String originalValue = config.getString(key);
            String resolvedValue = placeholderHelper.resolve(originalValue, config);
            config.put(key, resolvedValue);
        }
        for (String key : config.keySet()) {
            System.setProperty("application_" + key, config.getString(key));
        }
    }

    public static void put(String key, Object value) {
        config.put(key, value);
    }

    public static  DynamicHashMap<String, Object> getAll() {
        return config;
    }

    public static String getString(String key) {
        return config.getString(key);
    }

    public static Integer getInteger(String key) {
        return config.getInteger(key);
    }

    public static Long getLong(String key) {
        return config.getLong(key);
    }

    public static Float getFloat(String key) {
        return config.getFloat(key);
    }

    public static Double getDouble(String key) {
        return config.getDouble(key);
    }

    public static BigDecimal getDecimal(String key) {
        return config.getDecimal(key);
    }

    public static Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    /**
     * 根据前缀获取一组配置信息Map，Map中的key去掉了前缀
     *
     * @param prefix 前缀
     * @return
     */
    public static DynamicHashMap<String, Object> prefixConfigMap(String prefix) {
        DynamicHashMap<String, Object> result = new DynamicHashMap<>();
        for (String key : config.keySet()) {
            if (key.startsWith(prefix)) {
                String subKey = key.substring(prefix.length());
                result.put(subKey, config.get(key));
            }
        }
        return result;
    }

    /**
     * 格式化打印全部配置信息
     */
    public static void printAll() {
        List<String> keys = new ArrayList<>();
        int maxLen = 0;
        for (String key : config.keySet()) {
            maxLen = Math.max(key.length(), maxLen);
            keys.add(key);
        }
        Collections.sort(keys);
        for (String key : keys)
            System.out.printf("%-" + maxLen + "s : %s\n", key, config.get(key));
    }

}
