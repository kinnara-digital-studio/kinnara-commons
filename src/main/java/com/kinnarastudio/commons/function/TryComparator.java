package com.kinnarastudio.commons.function;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aristo
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface TryComparator<T, E extends Exception> extends Comparator<T> {
    int tryCompare(T o1, T o2) throws E;

    @Override
    default int compare(T o1, T o2) {
        try {
            return tryCompare(o1, o2);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return 0;
        }
    }

    default Comparator<T> onCatch(Function<E, Integer> function) {
        Objects.requireNonNull(function);

        return (T o1, T o2) -> {
            try {
                return tryCompare(o1, o2);
            } catch (Exception e) {
                return function.apply((E) e);
            }
        };
    }
}
