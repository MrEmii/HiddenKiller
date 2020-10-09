package dev.emir.models;

import dev.emir.interfaces.IConfigurationModel;

import java.util.HashSet;
import java.util.Set;

public abstract class ManagerModel<T extends IConfigurationModel<?>> {

    protected Set<T> list = new HashSet<T>();

    public ManagerModel() {
        update();
    }

    public void update() {
    }

    public void save() {
        this.getList().forEach(IConfigurationModel::save);
    }

    public boolean add(T object) {
        return this.list.add(object);
    }

    public T get(String identifier) {
        return null;
    }

    public Set<T> getList() {
        return list;
    }
}
