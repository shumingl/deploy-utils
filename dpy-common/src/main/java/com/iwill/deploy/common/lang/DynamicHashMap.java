package com.iwill.deploy.common.lang;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DynamicHashMap<K, V> extends HashMap<K, V> {

    public DynamicHashMap() {
        super();
    }

    public DynamicHashMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    public <T> T getObject(K key) {
        return (T) this.get(key);
    }

    public String getString(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        return String.valueOf(object);
    }

    public Integer getInteger(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).intValue();
        return Integer.parseInt(String.valueOf(object));
    }

    public Long getLong(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).longValue();
        return Long.parseLong(String.valueOf(object));
    }

    public Float getFloat(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).floatValue();
        return Float.parseFloat(String.valueOf(object));
    }

    public Double getDouble(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).doubleValue();
        return Double.parseDouble(String.valueOf(object));
    }

    public BigDecimal getDecimal(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        return new BigDecimal(String.valueOf(object));
    }

    public Boolean getBoolean(K key) {
        Object object = this.get(key);
        if (object == null) return null;
        if (object instanceof Boolean)
            return (Boolean) object;
        return Boolean.parseBoolean(String.valueOf(object));
    }

}
