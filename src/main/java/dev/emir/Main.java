package dev.emir;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.emir.api.ColorText;
import dev.emir.commands.MainCommands;
import dev.emir.db.Mongod;
import dev.emir.enums.PlayerState;
import dev.emir.events.BungeeCoordEvents;
import dev.emir.events.PlayerListeners;
import dev.emir.managers.*;
import dev.emir.models.PlayerModel;
import dev.emir.noteblockapi.NBSDecoder;
import dev.emir.noteblockapi.Noteblock;
import dev.emir.scoreboad.ScoreboardObject;
import dev.emir.scoreboad.ScoreboardObjectHandler;
import dev.emir.utils.WorldEdit;
import dev.emir.utils.command.CommandFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    private static Main instance;

    public static Logger logger = Logger.getLogger("HiddenKiller");

    public static Gson gson = new GsonBuilder().create();

    private PlayerManager playerManager;
    private GameManager gameManager;
    private KitsManager kitsManager;
    private SignManager signManager;
    private Mongod mongod;
    private WorldEdit worldEdit;
    private ScoreboardObjectHandler scoreboardDataHandler;
    private BungeeCoordEvents bungeeCoord;

    private SongsManager songsManager;

    private Noteblock noteblock;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            instance = this;
            long startup = System.currentTimeMillis();
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeCoord = new BungeeCoordEvents());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

            this.mongod = new Mongod();
            this.worldEdit = new WorldEdit();

            this.getConfig().options().copyDefaults(true);
            saveResource("Lavanda.nbs", true);
            saveResource("kits/Default.json", true);
            this.saveDefaultConfig();

            this.gameManager = new GameManager();
            this.playerManager = new PlayerManager(this.mongod.getCollection("users"));
            this.signManager = new SignManager();
            this.kitsManager = new KitsManager();

            this.songsManager = new SongsManager(NBSDecoder.parse(new File(getDataFolder(), "Lavanda.nbs")));
            this.noteblock = new Noteblock();

            this.scoreboardDataHandler = new ScoreboardObjectHandler();
            this.scoreboardDataHandler.enable();

            this.setupCommands();
            this.setupScoreboard();
            loadEvents(new PlayerListeners(), this.scoreboardDataHandler, this.songsManager);

            resume(startup);
        } else {
            getLogger().warning("No se encontró librerías necesarias.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void setupCommands() {
        CommandFramework framework = new CommandFramework(this);
        framework.registerCommands(new MainCommands());
        framework.registerHelp();
    }

    public void loadEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            this.mongod.disconnect();
    }

    public void setupScoreboard() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(player -> {
                    PlayerModel model = getPlayerManager().get(player.getUniqueId().toString());

                    if (model.getState() == PlayerState.NA) {
                        ScoreboardObject scoreboard = scoreboardDataHandler.getScoreboardFor(player);
                        Main.getInstance().getBungeeCoord().playerCount("ALL");
                        scoreboard.clear();
                        getConfig().getStringList("scoreboard.lobby").forEach(line -> {
                            line = line.replace("{username}", player.getDisplayName())
                                    .replace("{kills}", String.valueOf(model.getTotalKills()))
                                    .replace("{wins}", String.valueOf(model.getWins()))
                                    .replace("{losed}", String.valueOf(model.getPlayed() - model.getTotalKills()))
                                    .replace("{played}", String.valueOf(model.getPlayed()))
                                    .replace("{lobby}", getServer().getServerName())
                                    .replace("{players}", String.valueOf(Main.getInstance().getBungeeCoord().getServers().get("ALL")));


                            line = PlaceholderAPI.setPlaceholders(player, line);
                            scoreboard.add(line);
                        });
                        scoreboard.update(player);
                    }
                });
            }
        };

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 2L, 5L);


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

    public SignManager getSignManager() {
        return signManager;
    }

    public BungeeCoordEvents getBungeeCoord() {
        return bungeeCoord;
    }

    public void resume(long startup) {
        long ready = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder("&a=== HiddenKiller ===");
        builder.append("\n");
        builder.append("&aKits: ").append(this.kitsManager.getList().size()).append("\n");
        builder.append("&aGames: ").append(this.gameManager.getList().size()).append("\n");
        builder.append("&6Loaded in: ").append(String.format("%d min, %d sec, %dms", TimeUnit.MILLISECONDS.toMinutes(startup - ready), TimeUnit.MILLISECONDS.toSeconds(startup - ready) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startup - ready)), TimeUnit.MILLISECONDS.toMillis(startup - ready))).append("\n");

        Bukkit.getConsoleSender().sendMessage(ColorText.translate(builder.toString()));

    }
}
