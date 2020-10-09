package dev.emir.interfaces;

public interface IConfigurationModel<T> {
    public void save();

    public T load();

    public default void reload() {
        load();
        save();
    }
}