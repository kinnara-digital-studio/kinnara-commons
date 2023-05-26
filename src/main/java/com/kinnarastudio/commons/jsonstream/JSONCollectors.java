package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONObjectAdapter;
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
     * Base method for json object collector
     *
     * @param adapter
     * @param keyExtractor
     * @param valueExtractor
     * @param combiner       Executed when duplicate keys are found
     * @param finisher
     * @return
     * @param <J> Json object class
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <J, T, V> Collector<T, J, J> toJson(ObjectAdapter<J, V> adapter, Function<T, String> keyExtractor, Function<T, V> valueExtractor, BinaryOperator<J> combiner, Function<J, J> finisher) {
        Objects.requireNonNull(adapter);
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);

        return Collector.of(adapter::initialize, Try.onBiConsumer((json, t) -> {
            final String key = keyExtractor.apply(t);

            final V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                adapter.putValue(json, key, value);
            }
        }), combiner, finisher);
    }

    /**
     * Collector to json object
     *
     * @param adapter
     * @param keyExtractor
     * @param valueExtractor
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <J> Json object class
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <J, T, V> Collector<T, J, J>  toJson(ObjectAdapter<J, V> adapter, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<J, J> finisher) {
        return toJson(adapter, keyExtractor, valueExtractor, (accepted, ignored) -> accepted, finisher);
    }

    /**
     * Collector to json object
     *
     * @param adapter
     * @param keyExtractor
     * @param valueExtractor
     * @param valueExtractor
     * @return
     * @param <J> Json object class
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <J, T, V> Collector<T, J, J>  toJson(ObjectAdapter<J, V> adapter, Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJson(adapter, keyExtractor, valueExtractor, j -> j);
    }

    /**
     * Base collector method for json array
     *
     * @param adapter
     * @param valueExtractor
     * @param combiner
     * @param finisher
     * @return
     * @param <J> Json array class
     * @param <T> Source data type
     * @param <V> Json array content type
     */
    public static <J, T, V> Collector<T, J, J> toJson(ArrayAdapter<J, V> adapter, Function<T, V> valueExtractor, BinaryOperator<J> combiner, Function<J, J> finisher) {
        Objects.requireNonNull(adapter);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);

        return Collector.of(adapter::instantiate, (json, t) -> {
            final V value = valueExtractor.apply(t);
            if (value != null) {
                adapter.putValue(json, value);
            }
        }, combiner, finisher, Collector.Characteristics.UNORDERED);
    }

    /**
     * Collector to json array
     *
     * @param adapter
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <J> Json array class
     * @param <T> Data source type
     * @param <V> Json array content type
     */
    public static <J, T, V> Collector<T, J, J> toJson(ArrayAdapter<J, V> adapter, Function<T, V> valueExtractor, Function<J, J> finisher) {
        return toJson(adapter, valueExtractor, (a1, a2) -> JSONMapper.concat(adapter, a1, a2), finisher);
    }

    /**
     * Collector to json array
     *
     * @param adapter Adapter for json array
     * @return
     * @param <J> Json array class
     * @param <V> Json array content type
     */
    public static <J, V> Collector<V, J, J> toJson(ArrayAdapter<J, V> adapter) {
        return toJson(adapter, v -> v, j -> j);
    }

    /**
     * Collector to json array
     *
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param combiner
     * @param finisher
     * @return
     * @param <T> Source data type
     * @param <V> Json object value data type
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, BinaryOperator<JSONObject> combiner, Function<JSONObject, JSONObject> finisher) {
        Objects.requireNonNull(initializer);
        final JSONObjectAdapter<V> adapter = new JSONObjectAdapter<>(initializer);
        return toJson(adapter, keyExtractor, valueExtractor, combiner, finisher);
    }

    /**
     * Collector to {@link JSONObject}
     *
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<JSONObject, JSONObject> finisher) {
        return toJSONObject(initializer, keyExtractor, valueExtractor, JSONMapper::combine, finisher);
    }

    /**
     * Collector to {@link JSONObject}
     *
     * @param initializer
     * @param keyExtractor
     * @param valueExtractor
     * @return
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Supplier<JSONObject> initializer, Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJSONObject(initializer, keyExtractor, valueExtractor, json -> json);
    }

    /**
     * Collector to {@link JSONObject}
     *
     * @param keyExtractor
     * @param valueExtractor
     * @return
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJSONObject(JSONObject::new, keyExtractor, valueExtractor);
    }


    /**
     * Collector to {@link JSONArray}
     *
     * @param initializer
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <T> Source data type
     * @param <V> Json object value type
     */
    public static <T, V> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer, Function<T, V> valueExtractor, Function<JSONArray, JSONArray> finisher) {
        final JSONArrayAdapter<V> adapter = new JSONArrayAdapter<>(initializer);
        return toJson(adapter, valueExtractor, (JSONArray array1, JSONArray array2) -> JSONMapper.concat(array1, array2), finisher);
    }


    /**
     * Collector to {@link JSONArray}
     *
     * @param initializer
     * @param finisher
     * @return
     * @param <T> Source data type == Json array content type
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer, Function<JSONArray, JSONArray> finisher) {
        return toJSONArray(initializer, v -> v, finisher);
    }

    /**
     * Collector to {@link JSONArray}
     *
     * @param initializer Initializer for {@link JSONArray}
     * @return
     * @param <T> Source data type == Json array content type
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray(Supplier<JSONArray> initializer) {
        return toJSONArray(initializer, json -> json);
    }


    /**
     * Collector to {@link JSONArray}
     *
     * @return
     * @param <T> Source data type == Json array content type
     */
    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray() {
        return toJSONArray(JSONArray::new);
    }
}