package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author aristo
 *
 * Stream into {@link JSONObject} or {@link JSONArray}
 *
 */
public final class JSONStream {
    /**
     * Stream direct children of {@link JSONObject}
     *
     * @param jsonObject        source
     * @param valueExtractor    extractor
     * @param <V>
     * @return
     */
    public static <V> Stream<JSONObjectEntry<V>> of(final JSONObject jsonObject, final BiFunction<JSONObject, String, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);

        return Optional.ofNullable(jsonObject)
                .map(json -> StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<String>) json.keys(), 0), false))
                .orElseGet(Stream::empty)
                .map(key -> {
                    V value = valueExtractor.apply(jsonObject, key);
                    if (value != null) {
                        return new JSONObjectEntry<>(key, valueExtractor.apply(jsonObject, key));
                    }
                    return null;
                })
                .filter(Objects::nonNull);
    }

    /**
     * Stream direct children of {@link JSONArray}
     *
     * @param jsonArray         source
     * @param valueExtractor    extractor
     * @param <V>
     * @return
     */
    public static <V> Stream<V> of(final JSONArray jsonArray, final BiFunction<JSONArray, Integer, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);

        int length = Optional.ofNullable(jsonArray)
                .map(JSONArray::length)
                .orElse(0);

        return IntStream.iterate(0, i -> i + 1).limit(length)
                .boxed()
                .map(integer -> valueExtractor.apply(jsonArray, integer))
                .filter(Objects::nonNull);
    }

    /**
     * Flatten deep {@link JSONObject} structure into simplified {@link JSONObjectEntry} structure
     * with path as entry's key
     * Array's path will be represented as [index]
     *
     * @param jsonObject    source
     * @return
     */
    public static Stream<JSONObjectEntry<?>> flatten(final JSONObject jsonObject) {
        return flatten("", jsonObject);
    }

    /**
     * Flatten deep {@link JSONArray} structure into simplified {@link JSONObjectEntry} structure
     * with path as entry's key
     * Array's path will be represented as [index]
     *
     * @param jsonArray     source
     * @return
     */
    public static Stream<JSONObjectEntry<?>> flatten(final JSONArray jsonArray) {
        return flatten("", jsonArray);
    }

    /**
     * Recursively dig into {@link JSONObject} structure
     *
     * @param path          current path
     * @param jsonObject    source
     * @return
     */
    private static Stream<JSONObjectEntry<?>> flatten(final String path, final JSONObject jsonObject) {
        return Stream.concat(Stream.of(new JSONObjectEntry<>(path, jsonObject)), JSONStream.of(jsonObject, Try.onBiFunction(JSONObject::get)).flatMap(e -> {
            final String key = path.isEmpty() ? e.getKey() : String.join(".", path, e.getKey());
            final Object val = e.getValue();

            if (val instanceof JSONObject) {
                return flatten(key, (JSONObject) val);
            } else if (val instanceof JSONArray) {
                return flatten(key, (JSONArray) val);
            } else {
                return Stream.of(new JSONObjectEntry<>(key, val));
            }
        }));
    }

    /**
     * Recursively dig into {@link JSONArray} structure
     *
     * @param path          current path
     * @param jsonArray     source
     * @return
     */
    private static Stream<JSONObjectEntry<?>> flatten(final String path, final JSONArray jsonArray) {
        return Stream.concat(Stream.of(new JSONObjectEntry<>(path, jsonArray)), JSONStream.of(jsonArray, Try.onBiFunction((a, i) -> new JSONObjectEntry<>(String.valueOf(i), a.get(i))))
                .flatMap(e -> {
                    final String key = path + "[" + e.getKey() + "]";
                    final Object val = e.getValue();
                    if (val instanceof JSONObject) {
                        return flatten(key, (JSONObject) val);
                    } else if (val instanceof JSONArray) {
                        return flatten(key, (JSONArray) val);
                    } else {
                        return Stream.of(new JSONObjectEntry<>(key, val));
                    }
                }));
    }
}
