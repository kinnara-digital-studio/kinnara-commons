package com.kinnarastudio.commons.function;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Throwable version of {@link BiConsumer}
 *
 * @param <T>
 * @param <U>
 * @param <E>
 */
@FunctionalInterface
public interface TryBiConsumer<T, U, E extends Exception> extends BiConsumer<T, U> {
    void tryAccept(T t, U u) throws E;

    default void accept(T t, U u) {
        try {
            tryAccept(t, u);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    default BiConsumer<T, U> onCatch(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);

        return (t, u) -> {
            try {
                tryAccept(t, u);
            } catch (Exception e) {
                consumer.accept((E)e);
            }
        };
    }
}
