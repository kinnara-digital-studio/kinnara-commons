package com.kinnarastudio.commons.function;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface TryRunnable<E extends Exception> extends Runnable {
    void tryRun() throws E;

    @Override
    default void run() {
        try {
            tryRun();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    default Runnable onCatch(Consumer<E> consumer) {
        return () -> {
            try {
                tryRun();
            } catch (Exception e) {
                consumer.accept((E) e);
            }
        };
    }
}
