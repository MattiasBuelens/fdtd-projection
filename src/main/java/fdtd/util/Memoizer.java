package fdtd.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoizer<T, U> {

    private final Function<T, U> function;
    private final Function<T, U> memoized;
    private final Map<T, U> cache = new ConcurrentHashMap<>();

    private Memoizer(final Function<T, U> function) {
        this.function = function;
        this.memoized = (input) -> cache.computeIfAbsent(input, this.function::apply);
    }

    public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
        return new Memoizer<>(function).memoized;
    }

}