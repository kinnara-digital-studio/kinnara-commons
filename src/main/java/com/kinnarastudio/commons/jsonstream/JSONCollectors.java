package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * @author aristo
 *
 * Json Collectors
 * Generate {@link Collector} for {@link JSONObject} and {@link JSONArray}
 */
public final class JSONCollectors {
    /**
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJSONObject(JSONObject::new, keyExtractor, valueExtractor, json -> json);
    }

    /**
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJSONObject(initializer, keyExtractor, valueExtractor, json -> json);
    }

    /**
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<JSONObject, JSONObject> finisher) {
        return toJSONObject(initializer, keyExtractor, valueExtractor, JSONMapper::combine, finisher);
    }

    /**
     *
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param combiner
     * @param finisher
     * @return
     * @param <T>
     * @param <V>
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, BinaryOperator<JSONObject> combiner, Function<JSONObject, JSONObject> finisher) {
        final JSONObjectCollector<JSONObject> collector = new JSONObjectCollector<>(initializer, Try.onTriConsumer(JSONObject::put));
        return collector.toJSON(keyExtractor, valueExtractor, combiner, finisher);
    }

    /**
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @param duplicateKeyMerger operation when duplicate key found
     * @param <T>UnaryOperator<String> duplicateKeyMerger
     * @param <V>                Value type
     * @return
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<JSONObject, JSONObject> finisher, UnaryOperator<String> duplicateKeyMerger) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(duplicateKeyMerger);

        return Collector.of(initializer, Try.onBiConsumer((json, t) -> {
            String key = keyExtractor.apply(t);

            while (key != null && json.has(key)) {
                String newKey = duplicateKeyMerger.apply(key);

                // stop generating key if newKey is null or newKey equals key
                if (newKey == null || newKey.equals(key)) {
                    break;
                }

                key = newKey;
            }

            V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                json.put(key, value);
            }
        }), JSONMapper::combine, finisher);
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray() {
        return toJSONArray(JSONArray::new);
    }

    /**
     * @param initializer
     * @param <T>
     * @return
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer) {
        return toJSONArray(initializer, json -> json);
    }

    /**
     * @param initializer
     * @param finisher
     * @param <T>
     * @return
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer, Function<JSONArray, JSONArray> finisher) {
        return toJSONArray(initializer, v -> v, finisher);
    }

    /**
     * @param <T>
     * @param initializer
     * @param valueExtractor
     * @param finisher
     * @return
     */
    public static <T, V> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer, Function<T, V> valueExtractor, Function<JSONArray, JSONArray> finisher) {
        final JSONArrayCollector<JSONArray> collector = new JSONArrayCollector<>(initializer, JSONArray::put);
        return collector.toJSON(valueExtractor, JSONMapper::concat, finisher);
    }
}
