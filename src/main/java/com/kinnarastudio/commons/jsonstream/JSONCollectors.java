package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

/**
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
        return toJSONObject(initializer, keyExtractor, valueExtractor, finisher, s -> null);
    }

    /**
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @param duplicateKeyMerger operation when duplicate key found
     * @param <T>
     * @param <V>                Value type
     * @return
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<JSONObject, JSONObject> finisher, UnaryOperator<String> duplicateKeyMerger) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(duplicateKeyMerger);

        return Collector.of(initializer, Try.onBiConsumer((jsonObject, t) -> {
            String key = keyExtractor.apply(t);

            while (key != null && jsonObject.has(key)) {
                String newKey = duplicateKeyMerger.apply(key);

                // stop generating key if newKey is null or newKey equals key
                if (newKey == null || newKey.equals(key)) {
                    break;
                }

                key = newKey;
            }

            V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                jsonObject.put(key, value);
            }
        }), (left, right) -> {
            JSONStream.of(right, JSONObject::opt)
                    .forEach(Try.onConsumer(e -> left.put(e.getKey(), right.opt(e.getKey()))));
            return left;
        }, finisher);
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray() {
        return toJSONArray(JSONArray::new, json -> json);
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
     * @param initializer
     * @param valueExtractor
     * @param finisher
     * @param <T>
     * @return
     */
    public static <T, V> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer, Function<T, V> valueExtractor, Function<JSONArray, JSONArray> finisher) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(finisher);

        return Collector.of(initializer, (array, t) -> {
            V value = valueExtractor.apply(t);
            if (value != null) {
                array.put(value);
            }
        }, (left, right) -> {
            JSONStream.of(right, JSONArray::opt).forEach(Try.onConsumer(left::put));
            return left;
        }, finisher);
    }
}
