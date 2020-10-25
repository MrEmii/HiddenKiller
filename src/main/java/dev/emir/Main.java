package dev.emir;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.emir.db.Mongod;
import dev.emir.events.BungeeCordListener;
import dev.emir.events.PlayerListeners;
import dev.emir.managers.GameManager;
import dev.emir.managers.KitsManager;
import dev.emir.managers.PlayerManager;
import dev.emir.managers.SongsManager;
import dev.emir.noteblockapi.NBSDecoder;
import dev.emir.noteblockapi.Noteblock;
import dev.emir.scoreboad.ScoreboardObjectHandler;
import dev.emir.utils.WorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    private static Main instance;

    public static Logger logger = Logger.getLogger("HiddenKiller");

    public static Gson gson = new GsonBuilder().create();

    private PlayerManager playerManager;
    private GameManager gameManager;
    private KitsManager kitsManager;
    private Mongod mongod;
    private WorldEdit worldEdit;
    private ScoreboardObjectHandler scoreboardDataHandler;

    private BungeeCordListener bungeeCordListener;
    private SongsManager songsManager;

    private Noteblock noteblock;

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeCordListener = new BungeeCordListener());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "HiddenKiller", bungeeCordListener);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "HiddenKiller");
        this.mongod = new Mongod();
        this.worldEdit = new WorldEdit();
        this.gameManager = new GameManager();
        this.playerManager = new PlayerManager(this.mongod.getCollection("users"));

        this.getConfig().options().copyDefaults(true);
        saveResource("Lavanda.nbs", true);
        saveResource("test", true);
        this.saveDefaultConfig();
        this.songsManager = new SongsManager(NBSDecoder.parse(new File(getDataFolder(), "Lavanda.nbs")));
        this.noteblock = new Noteblock();

        this.scoreboardDataHandler = new ScoreboardObjectHandler();
        this.scoreboardDataHandler.enable();


        this.kitsManager = new KitsManager();
        loadEvents(new PlayerListeners(), this.scoreboardDataHandler, this.songsManager);
    }


    public void loadEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        this.mongod.disconnect();
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

    public BungeeCordListener getBungeeCordListener() {
        return bungeeCordListener;
    }

    public ScoreboardObjectHandler getScoreboardDataHandler() {
        return scoreboardDataHandler;
    }

    public Mongod getMongod() {
        return mongod;
    }

    public Noteblock getNoteblock() {
        return noteblock;
    }

    public WorldEdit getWorldEdit() {
        return worldEdit;
    }

    public SongsManager getSongsManager() {
        return songsManager;
    }
}
