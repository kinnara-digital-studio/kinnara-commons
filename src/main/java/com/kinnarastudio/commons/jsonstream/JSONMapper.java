package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public static JSONArray concat(JSONArray array1, JSONArray array2) {
        return Stream
                .concat(JSONStream.of(array1, Try.onBiFunction(JSONArray::get)), JSONStream.of(array2, Try.onBiFunction(JSONArray::get)))
                .collect(JSONCollectors.toJSONArray());
    }

    public static JSONObject combine(JSONObject object1, JSONObject object2) {
        return Stream.concat(JSONStream.of(object1, Try.onBiFunction(JSONObject::get)), JSONStream.of(object2, Try.onBiFunction(JSONObject::get)))
                .collect(JSONCollectors.toJSONObject(JSONObjectEntry::getKey, JSONObjectEntry::getValue));
    }
}
