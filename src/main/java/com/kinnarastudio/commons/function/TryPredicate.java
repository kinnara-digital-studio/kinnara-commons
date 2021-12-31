package com.kinnarastudio.commons.function;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aristo
 *
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
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

    default Predicate<T> onCatch(BiPredicate<T, ? super E> biPredicate) {
        Objects.requireNonNull(biPredicate);

        return t -> {
            try {
                return tryTest(t);
            } catch (Exception e) {
                return biPredicate.test(t, (E)e);
            }
        };
    }
}