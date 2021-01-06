package com.kinnarastudio.commons.function;

import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Throwable version of {@link Predicate}
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface TryPredicate<T, E extends Exception> extends Predicate<T> {

    boolean tryTest(T t) throws E;

    @Override
    default boolean test(T t) {
        try {
            return tryTest(t);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
            return false;
        }
    }

    default Predicate<T> onCatch(Predicate<? super E> predicate) {
        return (T t) -> {
            try {
                return tryTest(t);
            } catch (Exception e) {
                return predicate.test((E)e);
            }
        };
    }
}