package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.Function;

public final class JSONMapper {
    public static <T, V> Function<T, JSONObject> toJSONObject(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return Try.onFunction((t) -> {
            Objects.requireNonNull(keyExtractor);
            Objects.requireNonNull(valueExtractor);
            
            String key = keyExtractor.apply(t);
            V value = valueExtractor.apply(t);

            JSONObject jsonObject =  new JSONObject();
            jsonObject.put(key, value);
            return jsonObject;
        });
    }
}
