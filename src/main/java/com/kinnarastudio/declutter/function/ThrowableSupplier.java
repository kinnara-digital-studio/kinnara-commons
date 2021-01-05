package com.kinnarastudio.declutter.function;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Throwable version of {@link Supplier}
 *
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableSupplier<R, E extends Exception> extends Supplier<R> {
    @Nullable
    R getThrowable() throws E;

    @Nullable
    default R get() {
        try {
            return getThrowable();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).severe(e.getMessage());
            return null;
        }
    }

    default ThrowableSupplier<R, E> onException(Function<? super E, R> onException) {
        try {
            return this::getThrowable;
        } catch (Exception e) {
            Objects.requireNonNull(onException);
            return () -> onException.apply((E) e);
        }
    }
}
