package dev.emir.models;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.emir.Main;
import dev.emir.enums.PlayerState;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.objects.DefaultKit;
import dev.emir.utils.FileReader;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerModel {

    @Expose(serialize = false, deserialize = false)
    private Player player;
    private String uuid;
    private List<KitModel> kitsBought = new ArrayList<KitModel>();
    private int totalKills = 0;
    private int currentKils = 0;
    private int wins = 0;
    private PlayerState state = PlayerState.NA;


    public void toSpectator() {
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void toPlayer() {
        getPlayer().setGameMode(GameMode.SURVIVAL);
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

    public int getCurrentKils() {
        return currentKils;
    }

    public int getWins() {
        return wins;
    }

    public PlayerModel setGame(GameModel model) {
        //TODO: Si el jugador entra, cambiar playerSTate
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerModel setPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        return this;
    }

    public PlayerModel setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public PlayerModel setPlayer(String uuid) {
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
        Main.getInstance().getMongod().getCollection("hiddenkiller").insertOne(Document.parse(FileReader.gson.toJson(this.getClass())));
    }

    public KitModel getCurrentKit() {
        if (this.getKitsBought().size() > 0) {
            for (KitModel model : this.getKitsBought()) {
                if (model.isCurrent()) return model;
            }
        }
        return new DefaultKit();
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
}
