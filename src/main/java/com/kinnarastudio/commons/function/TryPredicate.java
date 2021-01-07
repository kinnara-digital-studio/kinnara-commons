package com.kinnarastudio.commons.function;

import java.util.Objects;
import java.util.function.BiFunction;
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
        Objects.requireNonNull(predicate);

        return t -> {
            try {
                return tryTest(t);
            } catch (Exception e) {
                return predicate.test((E)e);
            }
        };
    }

    default Predicate<T> onCatch(BiFunction<T, ? super E, Boolean> biFunction) {
        Objects.requireNonNull(biFunction);

        return t -> {
            try {
                return tryTest(t);
            } catch (Exception e) {
                return biFunction.apply(t, (E)e);
            }
        };
    }
}