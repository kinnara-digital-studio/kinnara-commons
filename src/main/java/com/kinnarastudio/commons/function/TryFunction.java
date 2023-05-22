package com.kinnarastudio.commons.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aristo
 *
 * Throwable version of {@link Function}.
 * Returns null then exception is raised
 *
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface TryFunction<T, R, E extends Exception> extends Function<T, R> {
    R tryApply(T t) throws E;

    @Override
    default R apply(T t) {
        try {
            return tryApply(t);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param onCatch
     * @return
     */
    default Function<T, R> onCatch(Function<? super E, ? extends R> onCatch) {
        return t -> {
            try {
                return (R) tryApply(t);
            } catch (Exception e) {
                return onCatch.apply((E) e);
            }
        };
    }

    /**
     * @param onCatch
     * @return
     */
    default Function<T, R> onCatch(BiFunction<? super T, ? super E, ? extends R> onCatch) {
        return t -> {
            try {
                return (R) tryApply(t);
            } catch (Exception e) {
                return onCatch.apply(t, (E) e);
            }
        };
    }
}