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
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(finisher);

        return Collector.of(initializer, (array, t) -> {
            final V value = valueExtractor.apply(t);
            if (value != null) {
                array.put(value);
            }
        }, JSONMapper::concat, finisher);
    }

    /**
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Collector<T, org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> toAnotherJSONObject(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toAnotherJSONObject(org.codehaus.jettison.json.JSONObject::new, keyExtractor, valueExtractor, json -> json);
    }

    /**
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> Collector<T, org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> toAnotherJSONObject(Supplier<org.codehaus.jettison.json.JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toAnotherJSONObject(initializer, keyExtractor, valueExtractor, json -> json);
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
    public static <T, V> Collector<T, org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> toAnotherJSONObject(Supplier<org.codehaus.jettison.json.JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> finisher) {
        return toAnotherJSONObject(initializer, keyExtractor, valueExtractor, finisher, s -> null);
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
    public static <T, V> Collector<T, org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> toAnotherJSONObject(Supplier<org.codehaus.jettison.json.JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<org.codehaus.jettison.json.JSONObject, org.codehaus.jettison.json.JSONObject> finisher, UnaryOperator<String> duplicateKeyMerger) {
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
        }), JSONMapper::combine, finisher);
    }


    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> toAnotherJSONArray() {
        return toAnotherJSONArray(org.codehaus.jettison.json.JSONArray::new);
    }

    /**
     * @param initializer
     * @param <T>
     * @return
     */
    public static <T> Collector<T, org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> toAnotherJSONArray(Supplier<org.codehaus.jettison.json.JSONArray> initializer) {
        return toAnotherJSONArray(initializer, json -> json);
    }

    /**
     * @param initializer
     * @param finisher
     * @param <T>
     * @return
     */
    public static <T> Collector<T, org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> toAnotherJSONArray(Supplier<org.codehaus.jettison.json.JSONArray> initializer, Function<org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> finisher) {
        return toAnotherJSONArray(initializer, v -> v, finisher);
    }

    /**
     *
     * @param initializer
     * @param valueExtractor
     * @param finisher
     * @param <T>
     * @return
     */
    public static <T, V> Collector<T, org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> toAnotherJSONArray(Supplier<org.codehaus.jettison.json.JSONArray> initializer, Function<T, V> valueExtractor, Function<org.codehaus.jettison.json.JSONArray, org.codehaus.jettison.json.JSONArray> finisher) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(finisher);

        return Collector.of(initializer, (array, t) -> {
            final V value = valueExtractor.apply(t);
            if (value != null) {
                array.put(value);
            }
        }, JSONMapper::concat, finisher);
    }
}
