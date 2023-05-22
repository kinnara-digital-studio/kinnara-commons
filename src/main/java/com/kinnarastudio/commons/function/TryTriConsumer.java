package com.kinnarastudio.commons.function;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface TryTriConsumer<T, U, V, E extends Exception> extends TriConsumer<T, U, V> {
    void tryAccept(T t, U u, V v) throws E;

    @Override
    default void accept(T t, U u, V v) {
        try {
            tryAccept(t, u, v);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    default TriConsumer<T, U, V> onCatch(Consumer<? super E> consumer) {
        return (t, u, v) -> {
            try {
                tryAccept(t, u, v);
            } catch (Exception e) {
                consumer.accept((E) e);
            }
        };
    }
}
