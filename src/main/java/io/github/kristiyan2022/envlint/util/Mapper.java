package io.github.kristiyan2022.envlint.util;

public interface Mapper<S, D> {
    D map(S source);
}
