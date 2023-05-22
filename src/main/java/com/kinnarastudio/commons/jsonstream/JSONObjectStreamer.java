package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.adapter.StreamerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JSONObjectStreamer<JSON> {
    private final Function<JSON, Iterator<String>> keysIteratorExtractor;

    public JSONObjectStreamer(Function<JSON, Iterator<String>> keysIteratorExtractor) {
        this.keysIteratorExtractor = keysIteratorExtractor;
    }

    public <V> Stream<JSONObjectEntry<V>> of(final JSON json, final BiFunction<JSON, String, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);

        return Optional.ofNullable(json)
                .map(j -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(keysIteratorExtractor.apply(j), 0), false))
                .orElseGet(Stream::empty)
                .map(key -> {
                    final V value = valueExtractor.apply(json, key);
                    if (value != null) {
                        return new JSONObjectEntry<>(key, value);
                    }

                    return null;
                })
                .filter(Objects::nonNull);
    }
}
