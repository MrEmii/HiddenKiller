package dev.emir.models;

import dev.emir.Main;
import dev.emir.enums.Categories;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.utils.FileReader;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KitModel implements IConfigurationModel<KitModel> {

    private String name = "Default";
    private int price = 0;
    private int blocks = 4;
    private transient boolean isCurrent = false;
    private Categories category = Categories.NORMAL;
    private Map<Integer, ItemStack> items = new HashMap<>();

    private transient File path = new File(Main.getInstance().getDataFolder(), "kits");


    public void loadDefault(){
        if(this.name.equalsIgnoreCase("Default")){
            TranslatableComponent st = new TranslatableComponent("item.shovelDiamond.name");
            ItemStack si = new ItemStack(Material.DIAMOND_SPADE);
            ItemMeta sim = si.getItemMeta();
            sim.setDisplayName("§6" + st.getTranslate());
            si.setItemMeta(sim);

            TranslatableComponent lt = new TranslatableComponent("tile.torch.name");
            ItemStack li = new ItemStack(Material.BEACON);
            ItemMeta lim = li.getItemMeta();
            lim.setDisplayName("§eSuper " + lt.getTranslate());
            li.setItemMeta(lim);

            this.items.put(0, si);
            this.items.put(8, li);
        }
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

    public int getBlocks() {
        return blocks;
    }

    public File getPath() {
        return path;
    }

    @Override
    public void save() {
        try {
            FileReader.save(this, this.path, this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public KitModel load() {
        try {
            return FileReader.load(new File(this.path, this.name + ".json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (KitModel) this;
    }

    @Override
    public void reload() {

    }

    @Override
    public String toString() {
        return name;
    }
}
