package com.kinnarastudio.commons.function;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aristo
 *
 * Try Runnable
 *
 * @param <E>
 */
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

    default Runnable onCatch(Consumer<? super E> onCatch) {
        return () -> {
            try {
                tryRun();
            } catch (Exception e) {
                onCatch.accept((E) e);
            }
        };
    }
}
