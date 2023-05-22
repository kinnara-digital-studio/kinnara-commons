package com.kinnarastudio.commons.jsonstream;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @param <J> Json Array Class
 */
public class JSONArrayCollector<J> {

    /**
     * Callback to initialize Json Array
     */
    private final Supplier<J> initializer;

    /**
     * Callback to put Value into Json Array <Json, Value>
     */
    private final BiConsumer<J, Object> putter;

    /**
     *
     * @param initializer Callback to initialize Json Array
     * @param putter Callback to put Value into Json Array <Json, Value>
     */
    public JSONArrayCollector(Supplier<J> initializer, BiConsumer<J, Object> putter) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(putter);

        this.initializer = initializer;
        this.putter = putter;
    }

    /**
     *
     * @param valueExtractor
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, V> valueExtractor) {
        return toJSON(valueExtractor, (json, ignored) -> json, json -> json);
    }

    /**
     *
     * @param valueExtractor
     * @param combiner
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, V> valueExtractor, BinaryOperator<J> combiner) {
        return toJSON(valueExtractor, combiner, json -> json);
    }

    /**
     *
     * @param valueExtractor
     * @param finisher
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, V> valueExtractor, Function<J, J> finisher) {
        return toJSON(valueExtractor, (json, ignored) -> json, finisher);
    }

    /**
     *
     * @param valueExtractor
     * @param combiner
     * @param finisher
     * @return
     * @param <T>
     * @param <V>
     */
    public <T, V> Collector<T, J, J> toJSON(Function<T, V> valueExtractor, BinaryOperator<J> combiner, Function<J, J> finisher) {
        Objects.requireNonNull(valueExtractor);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);

        return Collector.of(initializer, (json, t) -> {
            final V value = valueExtractor.apply(t);
            if (value != null) {
                putter.accept(json, value);
            }
        }, combiner, finisher, Collector.Characteristics.UNORDERED);
    }
}
