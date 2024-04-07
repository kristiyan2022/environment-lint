package io.github.kristiyan2022.envlint.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class FlatEntryMapper implements Mapper<Map.Entry<String, ?>, Map<String, String>> {
    private final String prefix;

    public FlatEntryMapper() {
        this(null);
    }

    public FlatEntryMapper(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Map<String, String> map(Map.Entry<String, ?> source) {
        return flattenElement(this.prefix, source);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> flattenElement(String prefix, Map.Entry<String, ?> entry) {
        String key = entry.getKey();
        prefix = StringUtils.hasText(prefix)
                ? prefix + "." + key
                : key;

        Object value = entry.getValue();
        if (value instanceof Map) {
            return new PropertyMapFlattener(prefix).map((Map<String, ?>) value);
        } else if (value instanceof Iterable) {
            return flattenIterable(prefix, (Iterable<?>) value);
        } else {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put(prefix, value.toString());
            return stringStringHashMap;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> flattenElement(String prefix, Object source) {
        if (source instanceof Iterable) {
            return flattenIterable(prefix, (Iterable<?>) source);
        } else if (source instanceof Map) {
            return new PropertyMapFlattener().map((Map<String, ?>) source);
        } else {
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(prefix, source.toString());
            return stringStringMap;
        }
    }

    private static Map<String, String> flattenIterable(String prefix, Iterable<?> iterable) {
        Map<String, String> result = new HashMap<>();

        int index = 0;
        for (Object element : iterable) {
            result.putAll(flattenElement(prefix + "[" + index + "]", element));
            index++;
        }

        return result;
    }
}
