package com.kinnarastudio.commons.function;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @param f
     * @return
     */
    default UnaryOperator<T> onCatch(Function<? super E, ? extends T> f) {
        return t -> {
            try {
                return (T) tryApply(t);
            } catch (Exception e) {
                return f.apply((E) e);
            }
        };
    }
}
