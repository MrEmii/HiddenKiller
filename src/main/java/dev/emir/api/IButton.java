package dev.emir.api;

import dev.emir.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class IButton {
    private ItemStack item;
    private Consumer<IButtonEvent> event;

    public IButton(ItemStack item, Consumer<IButtonEvent> event) {
        this.item = item;
        this.event = event;
    }

    public IButton(ItemStack item) {
        this(item, null);
    }

    public ItemStack getItem() {
        return item;
    }

    public Consumer<IButtonEvent> getEvent() {
        return event;
    }

    public static class IButtonEvent {
        Player player;
        GuiScreen gui;
        Main plugin;

        public IButtonEvent(Player player, GuiScreen gui, Main plugin) {
            this.player = player;
            this.gui = gui;
            this.plugin = plugin;
        }

        public Main getPlugin() {
            return plugin;
        }

        public Player getPlayer() {
            return player;
        }

        public GuiScreen getGui() {
            return gui;
        }
    }
}
