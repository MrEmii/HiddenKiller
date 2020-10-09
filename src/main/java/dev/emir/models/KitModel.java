package dev.emir.models;

import dev.emir.enums.Categories;
import dev.emir.interfaces.IConfigurationModel;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class KitModel implements IConfigurationModel<KitModel> {

    private String name;
    private int price;
    private boolean isCurrent = false;
    private Categories category;
    private Map<Integer, ItemStack> items = new HashMap<>();

    public KitModel(String name, int price, Categories category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Categories getCategory() {
        return category;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    @Override
    public void save() {

    }

    @Override
    public KitModel load() {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public String toString() {
        return name;
    }
}
