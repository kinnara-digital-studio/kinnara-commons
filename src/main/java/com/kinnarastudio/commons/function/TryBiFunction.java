package com.kinnarastudio.commons.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    default BiFunction<T, U, R> onCatch(Function<E, R> function) {
        return (t, u) -> {
            try {
                return tryApply(t, u);
            } catch (Exception e) {
                return function.apply((E) e);
            }
        };
    }
}
