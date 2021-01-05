package com.kinnarastudio.declutter;

import com.kinnarastudio.declutter.function.*;
import org.json.JSONArray;
import org.json.JSONException;
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
     * @param value
     * @return
     */
    default boolean isEmpty(@Nullable Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
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

    // JSON
    /**
     * Stream JSONArray
     *
     * @param jsonArray
     * @param <R>
     * @return
     */
    default <R> Stream<R> jsonStream(JSONArray jsonArray) {
        return jsonStream(jsonArray, (a, integer) -> (R)a.get(integer));
    }

    /**
     *
     * @param jsonArray
     * @param extractor
     * @param <R>
     * @return
     */
    default <R> Stream<R> jsonStream(JSONArray jsonArray, ThrowableBiFunction<JSONArray, Integer, R, JSONException> extractor) {
        int length = Optional.ofNullable(jsonArray).map(JSONArray::length).orElse(0);
        return IntStream.iterate(0, i -> i + 1).limit(length)
                .boxed()
                .map(integer -> extractor.onException(jsonException -> null).apply(jsonArray, integer))
                .filter(Objects::nonNull);
    }

    /**
     * Stream keys of JSONObject
     *
     * @param jsonObject
     * @return
     */
    default Stream<String> jsonStream(JSONObject jsonObject) {
        return Optional.ofNullable(jsonObject)
                .map(json -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        (Iterator<String>)json.keys(), 0), false))
                .orElseGet(Stream::empty);
    }

    /**
     * Collector for JSONArray
     *
     * @param <T>
     * @return
     */
    default <T> Collector<T, JSONArray, JSONArray> jsonCollector() {
        return Collector.of(JSONArray::new, (array, t) -> {
            if(t != null) {
                array.put(t);
            }
        }, (left, right) -> {
            jsonStream(right).forEach(throwableConsumer(left::put));
            return left;
        });
    }

    /**
     * Collector for JSONObject
     *
     * @param keyExtractor
     * @param valueExtractor
     * @param <T>
     * @param <V>
     * @return
     */
    default <T, V> Collector<T, JSONObject, JSONObject> jsonCollector(Function<T, String> keyExtractor, Function<T, V> valueExtractor) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);

        return Collector.of(JSONObject::new, throwableBiConsumer((jsonObject, t) -> {
            String key = keyExtractor.apply(t);
            V value = valueExtractor.apply(t);

            if (key != null && !key.isEmpty() && value != null) {
                jsonObject.put(key, value);
            }
        }), (left, right) -> {
            jsonStream(right).forEach(throwableConsumer(s -> left.put(s, right.get(s))));
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
     * Can be used in {@link Optional#map(Function)} to "peek" in Optional
     * Example : Optional.map(peekMap(o -> System.out.printl(o))
     *
     * @param consumer
     * @param <T>
     * @return
     */
    @Nonnull
    default <T> UnaryOperator<T> peekMap(@Nonnull final Consumer<T> consumer) {
        return t -> {
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

    // Throwable methods
    /**
     *
     * @param throwableSupplier
     * @param <R>
     * @param <E>
     * @return
     */
    default <R, E extends Exception> ThrowableSupplier<R, E> throwableSupplier(ThrowableSupplier<R, E> throwableSupplier) {
        return throwableSupplier;
    }

    default <R, E extends Exception> ThrowableSupplier<R, E> throwableSupplier(ThrowableSupplier<R, E> throwableSupplier, Function<? super E, R> onException) {
        return throwableSupplier.onException(onException);
    }

    /**
     * @param throwableConsumer
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> ThrowableConsumer<T, ? super E> throwableConsumer(ThrowableConsumer<T, ? super E> throwableConsumer) {
        return throwableConsumer;
    }

    /**
     *
     * @param throwableConsumer
     * @param failoverConsumer
     * @param <T>
     * @param <E>
     * @return
     */
    default <T, E extends Exception> Consumer<T> throwableConsumer(ThrowableConsumer<T, E> throwableConsumer, Consumer<? super E> failoverConsumer) {
        return throwableConsumer.onException(failoverConsumer);
    }

    /**
     * @param throwableBiConsumer
     * @param <T>
     * @param <U>
     * @param <E>
     * @return
     */
    default <T, U, E extends Exception> ThrowableBiConsumer<T, U, ? extends E> throwableBiConsumer(ThrowableBiConsumer<T, U, ? extends E> throwableBiConsumer) {
        return throwableBiConsumer;
    }

    /**
     * @param throwableFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> ThrowableFunction<T, R, ? extends E> throwableFunction(ThrowableFunction<T, R, ? extends E> throwableFunction) {
        return throwableFunction;
    }

    /**
     *
     * @param throwableFunction
     * @param failoverFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> Function<T, R> throwableFunction(ThrowableFunction<T, R, E> throwableFunction, Function<? super E, ? extends R> failoverFunction) {
        return throwableFunction.onException(failoverFunction);
    }

    /**
     *
     * @param throwableFunction
     * @param failoverFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, R, E extends Exception> Function<T, R> throwableFunction(ThrowableFunction<T, R, E> throwableFunction, BiFunction<T, E, R> failoverFunction) {
        return throwableFunction.onException(failoverFunction);
    }

    /**
     *
     * @param throwableBiFunction
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, U, R, E extends Exception> BiFunction<T, U, R> throwableBiFunction(ThrowableBiFunction<T, U, R, E> throwableBiFunction) {
        return throwableBiFunction;
    }

    /**
     *
     * @param throwableBiFunction
     * @param failover
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    default <T, U, R, E extends Exception> BiFunction<T, U, R> throwableBiFunction(ThrowableBiFunction<T, U, R, E> throwableBiFunction, Function<E, R> failover) {
        return throwableBiFunction.onException(failover);
    }

    // Extension for functional interfaces
}
