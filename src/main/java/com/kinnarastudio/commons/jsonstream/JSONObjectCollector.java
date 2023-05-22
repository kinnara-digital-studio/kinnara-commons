package com.kinnarastudio.commons.jsonstream;

import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.function.TriConsumer;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @param <J> Json Object Class
 */
public class JSONObjectCollector <J> {

    /**
     * Callback to initialize Json Object
     */
    private final Supplier<J> initializer;

    /**
     * Callback to put Key and Value into Json Object <Json, Key, Value>
     */
    private final TriConsumer<J, String, Object> putter;

    /**
     *
     * @param initializer Callback to initialize Json Object
     * @param putter Callback to put Key and Value into Json Object <Json, Key, Value>
     */
    public JSONObjectCollector(Supplier<J> initializer, TriConsumer<J, String, Object> putter) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(putter);

        this.initializer = initializer;
        this.putter = putter;
    }

    /**
     *
     * @param keyExtractor
     * @param valueExtractor
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        return toJSON(keyExtractor, valueExtractor, (json, ignored) -> json, json -> json);
    }

    /**
     *
     * @param keyExtractor
     * @param valueExtractor
     * @param combiner
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, String> keyExtractor, Function<T, V> valueExtractor, BinaryOperator<J> combiner) {
        return toJSON(keyExtractor, valueExtractor, combiner, json -> json);
    }

    /**
     *
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, String> keyExtractor, Function<T, V> valueExtractor, Function<J, J> finisher) {
        return toJSON(keyExtractor, valueExtractor, (json, ignored) -> json, finisher);
    }

    /**
     * @param keyExtractor
     * @param valueExtractor
     * @param finisher
     * @param <T>
     * @param <V>                Value type
     * @return
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, String> keyExtractor, Function<T, V> valueExtractor, BinaryOperator<J> combiner, Function<J, J> finisher) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);

        return Collector.of(initializer, Try.onBiConsumer((json, t) -> {
            final String key = keyExtractor.apply(t);

            final V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                putter.accept(json, key, value);
            }
        }), combiner, finisher);
    }
}
