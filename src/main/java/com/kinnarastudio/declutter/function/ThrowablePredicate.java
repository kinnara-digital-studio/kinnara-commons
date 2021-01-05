package com.kinnarastudio.declutter.function;

import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Throwable version of {@link Predicate}
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowablePredicate<T, E extends Exception> extends Predicate<T> {

    boolean testThrowable(T t) throws E;

    @Override
    default boolean test(T t) {
        try {
            return testThrowable(t);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
            return false;
        }
    }

    default Predicate<T> onException(Predicate<? super E> predicate) {
        return (T t) -> {
            try {
                return testThrowable(t);
            } catch (Exception e) {
                return predicate.test((E)e);
            }
        };
    }
}