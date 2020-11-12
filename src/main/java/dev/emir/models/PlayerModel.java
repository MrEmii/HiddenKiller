package dev.emir.models;

import com.google.gson.GsonBuilder;
import dev.emir.Main;
import dev.emir.enums.PlayerState;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerModel {

    @BsonIgnore
    private transient Player player;
    private String uuid;
    private List<KitModel> kitsBought = new ArrayList<KitModel>();
    private int totalKills = 0;
    @BsonIgnore
    private int currentKills = 0;
    @BsonIgnore
    private int currentBlocks = 0;
    private int wins = 0;
    private int played = 0;
    private PlayerState state = PlayerState.NA;

    public void toSpectator() {
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SPECTATOR);

        TranslatableComponent lt = new TranslatableComponent("stat.leaveGame");

        ItemStack li = new ItemStack(Material.BEACON);
        ItemMeta lim = li.getItemMeta();
        lim.setDisplayName("§7" + lt.getTranslate());
        li.setItemMeta(lim);

        player.getInventory().clear();
        player.getInventory().setItem(8, li);
        player.updateInventory();
    }

    public void toPlayer() {
        getPlayer().setGameMode(GameMode.SURVIVAL);
        switch (getState()) {
            case VICTIM:
                player.getInventory().clear();
                this.getCurrentKit().getItems().forEach((integer, itemStack) -> {
                    player.getInventory().setItem(integer, itemStack);
                });
                break;
            case KILLER:
                TranslatableComponent dt = new TranslatableComponent("item.swordDiamond.name");
                ItemStack di = new ItemStack(Material.CHEST);
                ItemMeta dim = di.getItemMeta();
                dim.setDisplayName("§4" + dt.getTranslate());
                di.setItemMeta(dim);

                player.getInventory().clear();
                player.getInventory().setItem(0, di);
                break;
        }
        player.updateInventory();
        player.setGameMode(GameMode.SURVIVAL);
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public void toPreLobby() {
        ItemStack ki = new ItemStack(Material.CHEST);
        ItemMeta kim = ki.getItemMeta();
        kim.setDisplayName("§6Kits");
        ki.setItemMeta(kim);

        ItemStack hi = new ItemStack(Material.BEACON);
        ItemMeta him = hi.getItemMeta();
        him.setDisplayName("§7Salir");
        hi.setItemMeta(him);

        player.getInventory().clear();
        player.getInventory().setItem(0, ki);
        player.getInventory().setItem(8, hi);
        player.updateInventory();
        player.setGameMode(GameMode.ADVENTURE);
        getPlayer().setFoodLevel((int) getPlayer().getMaxHealth());
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public List<KitModel> getKitsBought() {
        return kitsBought;
    }


    public String getUuid() {
        return uuid;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getCurrentKills() {
        return currentKills;
    }

    public int getWins() {
        return wins;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPlayed() {
        return played;
    }

    public PlayerModel setPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        return this;
    }

    public PlayerModel setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public int getCurrentBlocks() {
        return currentBlocks;
    }

    public void addBlock() {
        currentBlocks++;
    }

    public PlayerModel setPlayer(String uuid) {
        this.uuid = uuid;
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null)
            this.player = player;
        return this;
    }

    public PlayerModel updatePlayer() {
        if (this.uuid != null) {
            setPlayer(this.uuid);
        }
        return this;
    }

    @Override
    public String toString() {
        return uuid;
    }


    public void save() {
        Main.getInstance().getMongod().replace("users", "uuid", this.uuid, Document.parse(Main.gson.toJson(this)));
    }


    public KitModel getCurrentKit() {
        if (this.getKitsBought().size() > 0) {
            for (KitModel model : this.getKitsBought()) {
                if (model.isCurrent()) return model;
            }
        }
        return null;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public String toJson() {
        return new GsonBuilder().create().toJson(this.getClass());
    }

    public void sendTitle(String title, String subtitle) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
        IChatBaseComponent subtitleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON);

        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);

        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }

    public void sendActionBar(String message) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
        net.minecraft.server.v1_8_R3.PacketPlayOutChat ppoc = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    public void changeSkin() {

    }

}
