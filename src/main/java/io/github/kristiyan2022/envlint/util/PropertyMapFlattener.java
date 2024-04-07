package io.github.kristiyan2022.envlint.util;

import java.util.Map;
import java.util.stream.Collectors;

public class PropertyMapFlattener implements Mapper<Map<String, ?>, Map<String, String>> {
    private final String prefix;

    public PropertyMapFlattener() {
        this(null);
    }

    public PropertyMapFlattener(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Flatten a hierarchical {@link Map} into a flat {@link Map}
     * @param source The input map (must not be {@literal null})
     * @return the resulting {@link Map}.
     */
    @Override
    public Map<String, String> map(Map<String, ?> source) {
        return source.entrySet().stream()
                .map(new FlatEntryMapper(prefix)::map)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
