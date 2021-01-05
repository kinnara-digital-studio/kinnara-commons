package com.kinnarastudio.declutter.function;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Throwable version of {@link Consumer}
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception> extends Consumer<T> {

    void acceptThrowable(T t) throws E;

    @Override
    default void accept(T t) {
        try {
            acceptThrowable(t);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
        }
    }

    default Consumer<T> onException(final Consumer<? super E> onException) {
        Objects.requireNonNull(onException);

        return (T t) -> {
            try {
                acceptThrowable(t);
            } catch (Exception e) {
                onException.accept((E) e);
            }
        };
    }
}
