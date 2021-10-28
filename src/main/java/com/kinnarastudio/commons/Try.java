package com.kinnarastudio.commons;

import com.kinnarastudio.commons.function.*;

import java.util.Comparator;
import java.util.function.*;

public final class Try {
    /**
     * @param trySupplier
     * @param <R>
     * @param <E>
     * @return
     */
    public static <R, E extends Exception> R on(TrySupplier<R, E> trySupplier) {
        return trySupplier.get();
    }

    /**
     * @param trySupplier
     * @param failover
     * @param <R>
     * @param <E>
     * @return
     */
    public static <R, E extends Exception> R on(TrySupplier<R, E> trySupplier, Function<E, R> failover) {
        return trySupplier.onCatch(failover).get();
    }

    /**
     * @param trySupplier
     * @param <R>
     * @param <E>
     * @return
     */
    public static <R, E extends Exception> Supplier<R> onSupplier(TrySupplier<R, E> trySupplier) {
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
    public static <R, E extends Exception> Supplier<R> onSupplier(TrySupplier<R, E> supplier, Function<? super E, R> failover) {
        return supplier.onCatch(failover);
    }

    /**
     * @param consumer
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Consumer<T> onConsumer(TryConsumer<T, ? super E> consumer) {
        return consumer;
    }

    /**
     * @param consumer
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Consumer<T> onConsumer(TryConsumer<T, E> consumer, Consumer<? super E> failover) {
        return consumer.onCatch(failover);
    }

    /**
     * @param consumer
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Consumer<T> onConsumer(TryConsumer<T, E> consumer, BiConsumer<T, ? super E> failover) {
        return consumer.onCatch(failover);
    }

    /**
     * @param biConsumer
     * @param <T>
     * @param <U>
     * @param <E>
     * @return
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> onBiConsumer(TryBiConsumer<T, U, ? extends E> biConsumer) {
        return biConsumer;
    }

    /**
     * @param tryUnaryOperator
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> UnaryOperator<T> onUnaryOperator(TryUnaryOperator<T, ? extends E> tryUnaryOperator) {
        return tryUnaryOperator;
    }

    /**
     * @param tryUnaryOperator
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> UnaryOperator<T> onUnaryOperator(TryUnaryOperator<T, ? extends E> tryUnaryOperator, Function<? super E, ? extends T> failover) {
        return tryUnaryOperator.onCatch(failover);
    }

    /**
     * @param tryFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    public static <T, R, E extends Exception> Function<T, R> onFunction(TryFunction<T, R, ? extends E> tryFunction) {
        return tryFunction;
    }

    /**
     * @param tryFunction
     * @param failover
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    public static <T, R, E extends Exception> Function<T, R> onFunction(TryFunction<T, R, E> tryFunction, Function<? super E, ? extends R> failover) {
        return tryFunction.onCatch(failover);
    }

    /**
     * @param tryFunction
     * @param failover
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    public static <T, R, E extends Exception> Function<T, R> onFunction(TryFunction<T, R, E> tryFunction, BiFunction<T, E, R> failover) {
        return tryFunction.onCatch(failover);
    }

    /**
     * @param tryBiFunction
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    public static <T, U, R, E extends Exception> BiFunction<T, U, R> onBiFunction(TryBiFunction<T, U, R, E> tryBiFunction) {
        return tryBiFunction;
    }

    /**
     * @param tryBiFunction
     * @param failover
     * @param <T>
     * @param <U>
     * @param <R>
     * @param <E>
     * @return
     */
    public static <T, U, R, E extends Exception> BiFunction<T, U, R> onBiFunction(TryBiFunction<T, U, R, E> tryBiFunction, Function<E, R> failover) {
        return tryBiFunction.onCatch(failover);
    }

    /**
     * @param tryComparator
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Comparator<T> onComparator(TryComparator<T, E> tryComparator) {
        return tryComparator;
    }

    /**
     * @param tryComparator
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Comparator<T> onComparator(TryComparator<T, E> tryComparator, Function<E, Integer> failover) {
        return tryComparator.onCatch(failover);
    }

    /**
     * @param tryPredicate
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Predicate<T> onPredicate(TryPredicate<T, E> tryPredicate) {
        return tryPredicate;
    }

    /**
     * @param tryPredicate
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Predicate<T> onPredicate(TryPredicate<T, E> tryPredicate, Predicate<E> failover) {
        return tryPredicate.onCatch(failover);
    }

    /**
     * @param tryPredicate
     * @param failover
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E extends Exception> Predicate<T> onPredicate(TryPredicate<T, E> tryPredicate, BiPredicate<T, E> failover) {
        return tryPredicate.onCatch(failover);
    }

    /**
     * @param tryRunnable
     * @param <E>
     * @return
     */
    public static <E extends Exception> Runnable onRunnable(TryRunnable<E> tryRunnable) {
        return tryRunnable;
    }

    /**
     * @param tryRunnable
     * @param failover
     * @param <E>
     * @return
     */
    public static <E extends Exception> Runnable onRunnable(TryRunnable<E> tryRunnable, Consumer<E> failover) {
        return tryRunnable.onCatch(failover);
    }
}
