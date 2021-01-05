package com.kinnarastudio.declutter.function;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Throwable version of {@link BiConsumer}
 *
 * @param <T>
 * @param <U>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Exception> extends BiConsumer<T, U> {
    void acceptThrowable(T t, U u) throws E;

    default void accept(T t, U u) {
        try {
            acceptThrowable(t, u);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
        }
    }

    default BiConsumer<T, U> onException(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);

        return (T t, U u) -> {
            try {
                acceptThrowable(t, u);
            } catch (Exception e) {
                consumer.accept((E)e);
            }
        };

    }
}
