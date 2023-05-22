package com.kinnarastudio.commons.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aristo
 *
 * Try Unary Operator
 *
 * @param <T>
 * @param <E>
 */
public interface TryUnaryOperator<T, E extends Exception> extends UnaryOperator<T> {

    T tryApply(T t) throws E;

    @Override
    default T apply(T t) {
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
    default UnaryOperator<T> onCatch(Function<? super E, ? extends T> onCatch) {
        return t -> {
            try {
                return (T) tryApply(t);
            } catch (Exception e) {
                return onCatch.apply((E) e);
            }
        };
    }

    /**
     *
     * @param onCatch
     * @return
     */
    default UnaryOperator<T> onCatch(BiFunction<T, ? super E, ? extends T> onCatch) {
        return t -> {
            try {
                return (T) tryApply(t);
            } catch (Exception e) {
                return onCatch.apply(t, (E) e);
            }
        };
    }
}
