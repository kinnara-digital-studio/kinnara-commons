package com.kinnarastudio.commons;

import com.kinnarastudio.commons.function.*;
import com.kinnarastudio.commons.jsonstream.JSONCollectors;
import com.kinnarastudio.commons.jsonstream.JSONStream;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Mixin for stream processing
 */
public interface Declutter {
    /**
     * Nullsafe. If string is null or empty
     *
     * @param value desc
     * @return asdas
     */
    default boolean isEmpty(@Nullable Object value) {
        return Optional.ofNullable(value)
                .map(tryFunction(String::valueOf))
                .map(String::isEmpty)
                .orElse(true);
    }

    default boolean isEmpty(@Nullable Object[] array) {
        return Optional.ofNullable(array)
                .map(a -> a.length)
                .map(i -> i == 0)
                .orElse(true);
    }

    /**
     * Nullsafe. If object is not null and not empty
     *
     * @param value
     * @return
     */
    default boolean isNotEmpty(@Nullable Object value) {
        return !isEmpty(value);
    }

    /**
     * Nullsafe. If collection is null or empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    default <T> boolean isEmpty(@Nullable Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);
    }

    /**
     * Nullsafe. If collection is not null and not empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    default <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return !isEmpty(collection);
    }


    /**
     * If value null then return failover
     *
     * @param value
     * @param then
     * @param <T>
     * @return
     */
    @Nonnull
    default <T, U extends T> T ifNullThen(@Nullable T value, @Nonnull U then) {
        return value == null ? then : value;
    }

    /**
     * If value empty or null then return failover
     *
     * @param value
     * @param then
     * @param <T>
     * @return
     */
    default <T, U extends T> T ifEmptyThen(@Nullable T value, @Nonnull U then) {
        return isEmpty(value) ? then : value;
    }

    /**
     * Return null if string empty
     *
     * @param s
     * @return
     */
    default String nullIfEmpty(String s) {
        return s.isEmpty() ? null : s;
    }

    /**
     * Deprecated
     * Use {@link JSONStream#of(JSONArray, BiFunction)}
     * 
     * Stream JSONArray
     *
     * @param jsonArray
     * @param <R>
     * @return
     */
    @Deprecated
    default <R> Stream<R> jsonStream(JSONArray jsonArray) {
        return jsonStream(jsonArray, (array, index) -> (R)array.opt(index));
    }

    /**
     * Deprecated
     * Use {@link JSONStream#of(JSONArray, BiFunction)}
     *      
     * @param jsonArray
     * @param extractor
     * @param <R>
     * @return
     */
    @Deprecated
    default <R> Stream<R> jsonStream(JSONArray jsonArray, BiFunction<JSONArray, Integer, R> extractor) {
        Objects.requireNonNull(extractor);

        int length = Optional.ofNullable(jsonArray)
                .map(JSONArray::length)
                .orElse(0);

        return IntStream.iterate(0, i -> i + 1).limit(length)
                .boxed()
                .map(integer -> extractor.apply(jsonArray, integer))
                .filter(Objects::nonNull);
    }

    /**
     * Deprecated
     * Use {@link JSONStream#of(JSONObject, BiFunction)}
     * 
     * Stream keys of JSONObject
     *
     * @param jsonObject
     * @return
     */
    @Deprecated
    default Stream<String> jsonStream(JSONObject jsonObject) {
        return Optional.ofNullable(jsonObject)
                .map(json -> StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<String>) json.keys(), 0), false))
                .orElseGet(Stream::empty);
    }

    /**
     * Deprecated
     * Use {@link JSONStream#of(JSONObject, BiFunction)}
     * 
     * @param jsonObject
     * @param <R>
     * @return
     */
    @Deprecated
    default <R> Stream<Map.Entry<String, R>> jsonStreamEntry(JSONObject jsonObject) {
        return jsonStreamEntry(jsonObject, (json, key) -> (R) json.opt(key));
    }

    /**
     * Deprecated
     * Use {@link JSONStream#of(JSONObject, BiFunction)}
     * 
     * @param jsonObject
     * @param extractor
     * @param <V>
     * @return
     */
    @Deprecated
    default <V> Stream<Map.Entry<String, V>> jsonStreamEntry(JSONObject jsonObject, BiFunction<JSONObject, String, V> extractor) {
        Objects.requireNonNull(extractor);

        return Optional.ofNullable(jsonObject)
                .map(json -> StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<String>) json.keys(), 0), false))
                .orElseGet(Stream::empty)
                .map(key -> new AbstractMap.SimpleImmutableEntry<>(key, extractor.apply(jsonObject, String.valueOf(key))));
    }

    /**
     * Deprecated
     * Use {@link JSONCollectors#toJSONArray()}
     * 
     * Collector for JSONArray
     *
     * @param <T>
     * @return
     */
    @Deprecated
    default <T> Collector<T, JSONArray, JSONArray> jsonCollector() {
        return Collector.of(JSONArray::new, (array, t) -> {
            if(t != null) {
                array.put(t);
            }
        }, (left, right) -> {
            jsonStream(right).forEach(tryConsumer(left::put));
            return left;
        });
    }

    /**
     * Deprecated
     * Use {@link JSONCollectors#toJSONObject(Function, Function)}
     * 
     * Collector for JSONObject
     *
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    @Deprecated
    default <T, V> Collector<T, JSONObject, JSONObject> jsonCollector(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);

        return Collector.of(JSONObject::new, tryBiConsumer((jsonObject, t) -> {
            String key = keyExtractor.apply(t);
            V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                jsonObject.put(key, value);
            }
        }), (left, right) -> {
            jsonStream(right).forEach(tryConsumer(s -> left.put(s, right.get(s))));
            return left;
        });
    }

    /**
     * Predicate not
     *
     * @param p
     * @param <T>
     * @return
     */
    default <T> Predicate<T> not(Predicate<T> p) {
        assert p != null;
        return t -> !p.test(t);
    }

    /**
     * Map to it's own value. Can be used in {@link Optional#map(Function)} to "peek" in Optional
     * Example : Optional.map(peekMap(o -> System.out.printl(o))
     *
     * @param consumer
     * @param <T>
     * @return
     */
    @Nonnull
    default <T> UnaryOperator<T> peekMap(@Nonnull final Consumer<T> consumer) {
        return (final T t) -> {
            consumer.accept(t);
            return t;
        };
    }

    /**
     * Can be used in {@link Stream#filter(Predicate)} to remove duplicate values
     * Example : Stream.filter(distinctFilter(m -> m.get("value"))
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    default <T> Predicate<T> distinctFilter(@Nonnull Function<? super T, ?> keyExtractor) {
        Objects.requireNonNull(keyExtractor);

        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> {
            Object key = keyExtractor.apply(t);
            return key == null || seen.add(key);
        };
    }

    // Try methods
    /**
     *
     * @param trySupplier
     * @param <R>
     * @param <E>
     * @return
     */
    default <R, E extends Exception> Supplier<R> trySupplier(TrySupplier<R, E> trySupplier) {
        return trySupplier;
    }

    /**
     * Try Supplier with exception handling
     *
     * @param supplier
     * @param failover
     * @param <R>
     * @param <E>
     * @return
     */
    default <R, E extends Exception> Supplier<R> trySupplier(TrySupplier<R, E> supplier, Function<? super E, R> failover) {
        return supplier.onCatch(failover);
    }

    /**
     * @param consumer
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Consumer<T> tryConsumer(TryConsumer<T, ? super E> consumer) {
        return consumer;
    }

    /**
     *
     * @param consumer
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Consumer<T> tryConsumer(TryConsumer<T, E> consumer, Consumer<? super E> failover) {
        return consumer.onCatch(failover);
    }

    /**
     *
     * @param consumer
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Consumer<T> tryConsumer(TryConsumer<T, E> consumer, BiConsumer<T, ? super E> failover) {
        return consumer.onCatch(failover);
    }

    /**
     * @param biConsumer
     * @param <T>
     * @param <U>
     * @param <E>
     * @return
     */
    default <T, U, E extends Exception> BiConsumer<T, U> tryBiConsumer(TryBiConsumer<T, U, ? extends E> biConsumer) {
        return biConsumer;
    }

    /**
     * @param tryFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> Function<T, R> tryFunction(TryFunction<T, R, ? extends E> tryFunction) {
        return tryFunction;
    }

    /**
     *
     * @param tryFunction
     * @param failover
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> Function<T, R> tryFunction(TryFunction<T, R, E> tryFunction, Function<? super E, ? extends R> failover) {
        return tryFunction.onCatch(failover);
    }

    /**
     *
     * @param tryFunction
     * @param failover
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> Function<T, R> tryFunction(TryFunction<T, R, E> tryFunction, BiFunction<T, E, R> failover) {
        return tryFunction.onCatch(failover);
    }

    /**
     *
     * @param tryBiFunction
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, U, R, E extends Exception> BiFunction<T, U, R> tryBiFunction(TryBiFunction<T, U, R, E> tryBiFunction) {
        return tryBiFunction;
    }

    /**
     *
     * @param tryBiFunction
     * @param failover
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, U, R, E extends Exception> BiFunction<T, U, R> tryBiFunction(TryBiFunction<T, U, R, E> tryBiFunction, Function<E, R> failover) {
        return tryBiFunction.onCatch(failover);
    }

    /**
     *
     * @param tryComparator
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Comparator<T> tryComparator(TryComparator<T, E> tryComparator) {
        return tryComparator;
    }

    /**
     *
     * @param tryComparator
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Comparator<T> tryComparator(TryComparator<T, E> tryComparator, Function<E, Integer> failover) {
        return tryComparator.onCatch(failover);
    }

    /**
     *
     * @param tryPredicate
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Predicate<T> tryPredicate(TryPredicate<T, E> tryPredicate) {
        return tryPredicate;
    }

    /**
     *
     * @param tryPredicate
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Predicate<T> tryPredicate(TryPredicate<T, E> tryPredicate, Predicate<E> failover) {
        return tryPredicate.onCatch(failover);
    }

    /**
     *
     * @param tryPredicate
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Predicate<T> tryPredicate(TryPredicate<T, E> tryPredicate, BiPredicate<T, E> failover) {
        return tryPredicate.onCatch(failover);
    }

    /**
     *
     * @param tryRunnable
     * @param <E>
     * @return
     */
    default <E extends Exception> Runnable tryRunnable(TryRunnable<E> tryRunnable) {
        return tryRunnable;
    }

    /**
     *
     * @param tryRunnable
     * @param failover
     * @param <E>
     * @return
     */
    default <E extends Exception> Runnable tryRunnable(TryRunnable<E> tryRunnable, Consumer<E> failover) {
        return tryRunnable.onCatch(failover);
    }

    /**
     *
     * @param tryUnaryOperator
     * @param <T>
     * @param <E>
     * @return
     */
    default  <T, E extends Exception> UnaryOperator<T> tryUnaryOperator(TryUnaryOperator<T, ? extends E> tryUnaryOperator) {
        return tryUnaryOperator;
    }

    /**
     *
     * @param tryUnaryOperator
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    default  <T, E extends Exception> UnaryOperator<T> tryUnaryOperator(TryUnaryOperator<T, ? extends E> tryUnaryOperator, Function<? super E, ? extends T> failover) {
        return tryUnaryOperator.onCatch(failover);
    }
}
