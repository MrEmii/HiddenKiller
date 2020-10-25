package dev.emir.api;

import com.google.common.collect.Maps;
import dev.emir.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class GuiScreen{

    private Inventory inv;
    public Map<Integer, IButton> buttons;
    private int columns = 0;
    private String title = "";
    private int renderTimer = 0;
    private Main plugin = Main.getInstance();

    public GuiScreen(int columns){
        this.buttons = Maps.newHashMap();
        this.columns = columns;
        this.title = this.getClass().getSimpleName();
        this.inv = Bukkit.createInventory(null, this.limit(6*9, Math.multiplyExact(columns, 9)), this.title);
    }

    public GuiScreen(int columns, String title){
        this.buttons = Maps.newHashMap();
        this.columns = columns;
        this.title = ColorText.translate(title);
        this.inv = Bukkit.createInventory(null, this.limit(6*9, Math.multiplyExact(columns, 9)), ColorText.translate(title));
    }

    private int limit(int max, int num){
        return num > max ? max : num;
    }

    public void initGui(){}
    public void updateScreen(){}
    public void drawScreen(Player p){
        this.buttons.forEach((s, b) -> {
            this.inv.setItem(s, b.getItem());
        });
    }
    public void closeGui(){}

    public void handleClose(){
        this.buttons.clear();
        this.closeGui();
        Bukkit.getScheduler().cancelTask(this.renderTimer);
    }

    public void handleOpen(Player p){
        this.initGui();
        this.buttons.forEach((s, b) -> {
            this.inv.setItem(s, b.getItem());
        });
        this.renderTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, ()->{
            this.drawScreen(p);
            this.updateScreen();
        }, 0L, 1L);
    }

    public void handleClick(InventoryClickEvent e){
        if(ChatColor.stripColor(e.getClickedInventory().getTitle()).equalsIgnoreCase(ChatColor.stripColor(this.title))){
            if(this.buttons.get(e.getSlot()) != null && this.buttons.get(e.getSlot()).getEvent() != null && this.buttons.get(e.getSlot()).getItem().getType() != Material.AIR){
                this.buttons.get(e.getSlot()).getEvent().accept(new IButton.IButtonEvent((Player) e.getWhoClicked(), this, plugin));
            }
            e.setCancelled(true);
        }
    }

    public Inventory getInv() {
        return inv;
    }

    public Map<Integer, IButton> getButtons() {
        return buttons;
    }

    public int getColumns() {
        return columns;
    }

    public String getTitle() {
        return title;
    }

    public int getRenderTimer() {
        return renderTimer;
    }
}
