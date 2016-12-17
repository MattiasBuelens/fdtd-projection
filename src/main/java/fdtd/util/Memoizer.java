package fdtd.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoizer<T, U> implements Function<T, U> {

    private final Function<T, U> function;
    private final Map<T, U> cache = new ConcurrentHashMap<>();

    private Memoizer(final Function<T, U> function) {
        this.function = function;
    }

    @Override
    public U apply(T t) {
        return cache.computeIfAbsent(t, this.function);
    }

    public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
        return new Memoizer<>(function);
    }

}