package com.kinnarastudio.commons.function;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Throwable version of {@link Consumer}
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface TryConsumer<T, E extends Exception> extends Consumer<T> {

    void tryAccept(T t) throws E;

    @Override
    default void accept(T t) {
        try {
            tryAccept(t);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    default Consumer<T> onCatch(final Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);

        return t -> {
            try {
                tryAccept(t);
            } catch (Exception e) {
                consumer.accept((E) e);
            }
        };
    }

    default Consumer<T> onCatch(BiConsumer<T, ? super E> biConsumer) {
        Objects.requireNonNull(biConsumer);

        return t -> {
            try {
                tryAccept(t);
            } catch (Exception e) {
                biConsumer.accept(t, (E)e);
            }
        };
    }
}
