package io.github.maliciousfiles.maliUtils.utils;

import io.github.maliciousfiles.maliUtils.MaliUtils;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigObject<T> {
    private T inner;
    private String path;

    public ConfigObject(String path, T def) {
        this.path = path;
        this.inner = (T) MaliUtils.instance.getConfig().get(path, def);
    }

    private void save() {
        MaliUtils.instance.getConfig().set(path, inner);
        MaliUtils.instance.saveConfig();
    }

    public T get() {
        return inner;
    }

    public void update(Consumer<T> consumer) {
        consumer.accept(inner);
        save();
    }

    public void update(Function<T,T> function) {
        inner = function.apply(inner);
        save();
    }
}
