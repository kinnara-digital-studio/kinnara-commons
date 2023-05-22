package com.kinnarastudio.commons.jsonstream;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JSONArrayStreamer<JSON> {
    private final ToIntFunction<JSON> lengthGetter;

    public JSONArrayStreamer(ToIntFunction<JSON> lengthGetter) {
        this.lengthGetter = lengthGetter;
    }

    public <V> Stream<V> of(final JSON jsonArray, final BiFunction<JSON, Integer, V> valueExtractor) {
        Objects.requireNonNull(valueExtractor);

        int length = Optional.ofNullable(jsonArray)
                .map(this.lengthGetter::applyAsInt)
                .orElse(0);

        return IntStream.iterate(0, i -> i + 1).limit(length)
                .boxed()
                .map(index -> valueExtractor.apply(jsonArray, index))
                .filter(Objects::nonNull);
    }
}
