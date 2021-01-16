package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

public interface JSONCollectors {
    /**
     *
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    static <T, V> Collector<T, JSONObject, JSONObject> toJSONObject(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);

        return Collector.of(JSONObject::new, Try.onBiConsumer((jsonObject, t) -> {
            String key = keyExtractor.apply(t);
            V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                jsonObject.put(key, value);
            }
        }), (left, right) -> {
            JSONStream.of(right, JSONObject::opt)
                    .forEach(Try.onConsumer(e -> left.put(e.getKey(), right.opt(e.getKey()))));
            return left;
        });
    }

    /**
     *
     * @param <T>
     * @return
     */
    static <T> Collector<T, JSONArray, JSONArray> toJSONArray() {
        return Collector.of(JSONArray::new, (array, t) -> {
            if(t != null) {
                array.put(t);
            }
        }, (left, right) -> {
            JSONStream.of(right, JSONArray::opt).forEach(Try.onConsumer(left::put));
            return left;
        });
    }
}
