package com.kinnarastudio.commons.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Throwable BiFunction
 *
 * @param <T>
 * @param <U>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface TryBiFunction<T, U, R, E extends Exception> extends BiFunction<T, U, R> {
    R tryApply(T t, U u) throws E;

    default R apply(T t, U u) {
        try {
            return tryApply(t, u);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
            return null;
        }
    }

    default BiFunction<T, U, R> onException(Function<E, R> f) {
        return (T t, U u) -> {
            try {
                return tryApply(t, u);
            } catch (Exception e) {
                return f.apply((E) e);
            }
        };
    }
}
