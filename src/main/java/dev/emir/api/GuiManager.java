package dev.emir.api;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GuiManager implements Listener {

    private static GuiManager instance;
    private static Map<UUID, GuiScreen> screens = Maps.newHashMap();

    public void displayGuiScreen(GuiScreen gui, Player p){
        if(!screens.containsKey(p.getUniqueId()))
            screens.put(p.getUniqueId(), gui);
        screens.get(p.getUniqueId()).handleOpen(p);
        p.openInventory(screens.get(p.getUniqueId()).getInv());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(Objects.nonNull(screens.get(e.getPlayer().getUniqueId()))){
            screens.get(e.getPlayer().getUniqueId()).handleClose();
            screens.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(Objects.nonNull(screens.get(e.getWhoClicked().getUniqueId()))){
            screens.get(e.getWhoClicked().getUniqueId()).handleClick(e);
        }
    }

    public static GuiManager getInstance(){
        return instance == null ? instance = new GuiManager() : instance;
    }

}
