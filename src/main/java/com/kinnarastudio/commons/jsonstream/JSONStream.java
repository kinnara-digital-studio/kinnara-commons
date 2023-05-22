package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

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

        final JSONObjectStreamer<JSONObject> streamer = new JSONObjectStreamer<>(JSONObject::keys);
        return streamer.of(jsonObject, valueExtractor);
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

        final JSONArrayStreamer<JSONArray> streamer = new JSONArrayStreamer<>(JSONArray::length);
        return streamer.of(jsonArray, valueExtractor);
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
