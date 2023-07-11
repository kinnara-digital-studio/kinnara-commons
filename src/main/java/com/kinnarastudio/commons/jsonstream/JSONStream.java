package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JSONObjectAdapter;
import com.kinnarastudio.commons.jsonstream.model.JSONObjectEntry;

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
 * Streamer of json object and json array
 *
 */
public final class JSONStream {

    /**
     * Streamer for json object
     *
     * @param adapter
     * @param jsonObject
     * @return
     * @param <J> Json object class
     * @param <V> Json object value type
     */
    public static <J, V> Stream<JSONObjectEntry<V>> of(final ObjectAdapter<J, V> adapter, final J jsonObject) {
        Objects.requireNonNull(adapter);

        return Optional.ofNullable(jsonObject)
                .map(j -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(adapter.getKeyIterator(j), 0), false))
                .orElseGet(Stream::empty)
                .map(key -> {
                    final V value = adapter.getValue(jsonObject, key);
                    if (value != null) {
                        return new JSONObjectEntry<>(key, value);
                    }

                    return null;
                })
                .filter(Objects::nonNull);
    }

    /**
     * Streamer for {@link org.json.JSONObject}
     *
     * @param jsonObject
     * @param valueExtractor
     * @return
     * @param <V> Json object value type
     */
    public static <V> Stream<JSONObjectEntry<V>> of(final org.json.JSONObject jsonObject, final BiFunction<org.json.JSONObject, String, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);
        final JSONObjectAdapter<V> adapter = new JSONObjectAdapter<>(valueExtractor);
        return of(adapter, jsonObject);
    }

    /**
     * Streamer for json array
     *
     * @param jsonArray                                                             
     * @param adapter
     * @return
     * @param <J> Json array class
     * @param <V> Json array content type
     */
    public static <J, V> Stream<V> of(final ArrayAdapter<J, V> adapter, final J jsonArray) {
        Objects.requireNonNull(adapter);

        int length = Optional.ofNullable(jsonArray)
                .map(adapter::getLength)
                .orElse(0);

        return IntStream.iterate(0, i -> i + 1).limit(length)
                .boxed()
                .map(i -> adapter.getValue(jsonArray, i))
                .filter(Objects::nonNull);
    }

    /**
     * Streamer for {@link org.json.JSONArray}
     *
     * @param jsonArray      Source json array
     * @param valueExtractor Value extractor from json array
     * @return
     * @param <V> Json array content type
     */
    public static <V> Stream<V> of(final org.json.JSONArray jsonArray, final BiFunction<org.json.JSONArray, Integer, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);
        final JSONArrayAdapter<V> adapter = new JSONArrayAdapter<>(valueExtractor);
        return of(adapter, jsonArray);
    }

    /**
     * Flatten deep {@link org.json.JSONObject} structure into simplified {@link JSONObjectEntry} structure
     * with path as entry's key
     * Array's path will be represented as [index]
     *
     * @param jsonObject    source
     * @return
     */
    public static Stream<JSONObjectEntry<?>> flatten(final org.json.JSONObject jsonObject) {
        return flatten("", jsonObject);
    }

    /**
     * Flatten deep {@link org.json.JSONArray} structure into simplified {@link JSONObjectEntry} structure
     * with path as entry's key
     * Array's path will be represented as [index]
     *
     * @param jsonArray     source
     * @return
     */
    public static Stream<JSONObjectEntry<?>> flatten(final org.json.JSONArray jsonArray) {
        return flatten("", jsonArray);
    }

    /**
     * Recursively dig into {@link org.json.JSONObject} structure
     *
     * @param path          current path
     * @param jsonObject    source
     * @return
     */
    private static Stream<JSONObjectEntry<?>> flatten(final String path, final org.json.JSONObject jsonObject) {
        return Stream.concat(Stream.of(new JSONObjectEntry<>(path, jsonObject)), JSONStream.of(jsonObject, Try.onBiFunction(org.json.JSONObject::get)).flatMap(e -> {
            final String key = path.isEmpty() ? e.getKey() : String.join(".", path, e.getKey());
            final Object val = e.getValue();

            if (val instanceof org.json.JSONObject) {
                return flatten(key, (org.json.JSONObject) val);
            } else if (val instanceof org.json.JSONArray) {
                return flatten(key, (org.json.JSONArray) val);
            } else {
                return Stream.of(new JSONObjectEntry<>(key, val));
            }
        }));
    }

    /**
     * Recursively dig into {@link org.json.JSONArray} structure
     *
     * @param path          current path
     * @param jsonArray     source
     * @return
     */
    private static Stream<JSONObjectEntry<?>> flatten(final String path, final org.json.JSONArray jsonArray) {
        return Stream.concat(Stream.of(new JSONObjectEntry<>(path, jsonArray)), JSONStream.of(jsonArray, Try.onBiFunction((a, i) -> new JSONObjectEntry<>(String.valueOf(i), a.get(i))))
                .flatMap(e -> {
                    final String key = path + "[" + e.getKey() + "]";
                    final Object val = e.getValue();
                    if (val instanceof org.json.JSONObject) {
                        return flatten(key, (org.json.JSONObject) val);
                    } else if (val instanceof org.json.JSONArray) {
                        return flatten(key, (org.json.JSONArray) val);
                    } else {
                        return Stream.of(new JSONObjectEntry<>(key, val));
                    }
                }));
    }
}
