package dev.emir.models;

import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.enums.PlayerState;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.threads.GameTimer;
import dev.emir.utils.FileReader;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameModel implements IConfigurationModel<GameModel> {

    private int maxPlayers, minPlayers, lobbyTimeLeft, startingTimeLeft, gameTimeLeft, endTimeLeft;
    private List<Location> chestEnableds = new ArrayList<>();
    private GameStates gameState = GameStates.WAITING;
    private List<ItemStack> legendaryItems;
    private List<ItemStack> normalItems;
    private HashMap<UUID, List<PlayerModel>> players = new HashMap<>();
    private List<ItemStack> rareItems;
    private Location lobby, specLobby;
    private List<Location> spawns;
    private List<Location> bounds;
    private Set<Chest> opened;
    private String gameName;
    private GameTimer gameTimer;

    final transient File path = new File(Main.getInstance().getDataFolder(), "games");

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getLobbyTimeLeft() {
        return lobbyTimeLeft;
    }

    public int getGameTimeLeft() {
        return gameTimeLeft;
    }

    public int getEndTimeLeft() {
        return endTimeLeft;
    }

    public List<Location> getChestEnableds() {
        return chestEnableds;
    }

    public GameStates getGameState() {
        return gameState;
    }

    public int getStartingTimeLeft() {
        return startingTimeLeft;
    }

    public List<ItemStack> getLegendaryItems() {
        return legendaryItems;
    }

    public List<ItemStack> getNormalItems() {
        return normalItems;
    }

    public Map<UUID, List<PlayerModel>> getPlayers() {
        return players;
    }

    public List<ItemStack> getRareItems() {
        return rareItems;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpecLobby() {
        return specLobby;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public List<Location> getBounds() {
        return bounds;
    }

    public Set<Chest> getOpened() {
        return opened;
    }

    public String getGameName() {
        return gameName;
    }

    public GameTimer getTimer() {
        return gameTimer;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void subtractLobbyTimeLeft() {
        this.lobbyTimeLeft--;
    }

    public void subtractStartingTimeLeft() {
        this.startingTimeLeft--;
    }

    public void subtractGameTimeLeft() {
        this.gameTimeLeft--;
    }

    public void subtractEndTimeLeft() {
        this.endTimeLeft--;
    }

    public void setChestEnableds(List<Location> chestEnableds) {
        this.chestEnableds = chestEnableds;
    }

    public void setGameState(GameStates gameState) {
        this.gameState = gameState;
    }

    public void setLegendaryItems(List<ItemStack> legendaryItems) {
        this.legendaryItems = legendaryItems;
    }

    public void setNormalItems(List<ItemStack> normalItems) {
        this.normalItems = normalItems;
    }

    public void setRareItems(List<ItemStack> rareItems) {
        this.rareItems = rareItems;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public void setSpecLobby(Location specLobby) {
        this.specLobby = specLobby;
    }

    public void setSpawns(List<Location> spawns) {
        this.spawns = spawns;
    }

    public void setBounds(List<Location> bounds) {
        this.bounds = bounds;
    }

    public void setOpened(Set<Chest> opened) {
        this.opened = opened;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameTimer(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }

    public void broadcast(String message) {
        this.getPlayers().values().forEach(gamePlayers -> {
            gamePlayers.forEach(player -> {
                if (player.isInGame()) {
                    player.getPlayer().sendMessage(message);
                }
            });
        });
    }

    public List<PlayerModel> getTeam(UUID owner) {
        return getPlayers().get(owner);
    }

    public PlayerModel getPlayer(String playerName) {
        for (List<PlayerModel> gamePlayers : this.getPlayers().values()) {
            for (PlayerModel player : gamePlayers) {
                if (player.getPlayer().getDisplayName().equalsIgnoreCase(playerName)) return player;
            }
        }
        return null;
    }

    public void sendTeamMessage(UUID owner, String message) {
        this.getPlayers().get(owner).forEach(to -> {
            to.getPlayer().sendMessage(message);
        });
    }

    @Override
    public String toString() {
        return gameName;
    }

    public void save() {
        try {
            FileReader.save(this, this.path, this.gameName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameModel load() {
        try {
            FileReader.load(new File(this.path, this.gameName + ".json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (GameModel) this;
    }

    public void reload() {
    }
}
