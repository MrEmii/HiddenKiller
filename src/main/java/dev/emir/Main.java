package dev.emir;
import dev.emir.db.Mongod;
import dev.emir.events.PlayerListeners;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.managers.*;
import dev.emir.utils.FileReader;
import dev.emir.utils.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class Main extends JavaPlugin {

    private static Main instance;

    private PlayerManager playerManager;
    private GameManager gameManager;
    private KitsManager kitsManager;
    private ScoreboardManager scoreboardManager;
    private PluginConfiguration configuration;
    private Mongod mongod;

    @Override
    public void onEnable() {
        instance = this;
        this.mongod = new Mongod();
        this.configuration = new PluginConfiguration().load();
        this.gameManager = new GameManager();
        this.playerManager = new PlayerManager(this.mongod.getCollection("hiddenkiller"));
        this.kitsManager = new KitsManager();
        this.scoreboardManager = new ScoreboardManager();
        loadEvents(new PlayerListeners());
    }


    public void loadEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public KitsManager getKitsManager() {
        return kitsManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Mongod getMongod() {
        return mongod;
    }

    public PluginConfiguration getConfiguration() {
        return configuration;
    }

    public static class PluginConfiguration implements IConfigurationModel<PluginConfiguration> {


        String lobbyWorld;

        public Location getLobby() {
            System.out.println(lobbyWorld);
            if (this.lobbyWorld == null) return null;
            return Serializer.StringToLocation(lobbyWorld);
        }

        public void setLobby(Location location) {
            this.lobbyWorld = Serializer.LocationToString(location);
        }

        @Override
        public void save() {
            System.out.println("GuaRDANDo");
            try {
                FileReader.save(this, Main.getInstance().getDataFolder(), "config.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PluginConfiguration load() {
            try {
                return FileReader.load(new File(Main.getInstance().getDataFolder(), "config.json"), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public void reload() {

        }
    }
}
