package com.kinnarastudio.commons;

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
 * Mixin for simple processing
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
                .map(Try.onFunction(String::valueOf))
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
     *
     * @param value
     * @param then
     * @param <T>
     * @param <U>
     * @return
     */
    default <T, U extends T> T ifNullThen(@Nullable T value, @Nonnull Supplier<U> then) {
        return value == null ? then.get() : value;
    }

    /**
     * If value empty or null then return failover
     *
     * @param value
     * @param then
     * @param <T>
     * @return
     */
    @Nonnull
    default <T, U extends T> T ifEmptyThen(@Nullable T value, @Nonnull U then) {
        return value == null || isEmpty(value) ? then : value;
    }

    /**
     *
     * @param value
     * @param then
     * @param <T>
     * @param <U>
     * @return
     */
    default <T, U extends T> T ifEmptyThen(@Nullable T value, @Nonnull Supplier<U> then) {
        return isEmpty(value) ? then.get() : value;
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
}
