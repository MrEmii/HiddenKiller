package dev.emir.models;

import dev.emir.Main;
import dev.emir.api.ColorText;
import dev.emir.enums.GameStates;
import dev.emir.enums.PlayerState;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.threads.GameTimer;
import dev.emir.utils.Encrypter;
import dev.emir.utils.FileReader;
import dev.emir.utils.WorldEdit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameModel implements IConfigurationModel<GameModel> {

    private int maxPlayers, minPlayers, lobbyTimeLeft, startingTimeLeft, gameTimeLeft, endTimeLeft;
    private GameStates gameState = GameStates.WAITING;
    private transient ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
    private transient GameTimer gameTimer;
    private String lobby;
    private String center;
    private String gameName;
    private transient WorldModel rollback;

    final transient File path = new File(Main.getInstance().getDataFolder(), "games");

    public GameModel(int maxPlayers, int minPlayers, int lobbyTimeLeft, int startingTimeLeft, int gameTimeLeft, int endTimeLeft, GameStates gameState, String gameName, String center) {
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyTimeLeft = lobbyTimeLeft;
        this.startingTimeLeft = startingTimeLeft;
        this.gameTimeLeft = gameTimeLeft;
        this.endTimeLeft = endTimeLeft;
        this.gameState = gameState;
        this.gameName = gameName;
        this.center = center;
        this.gameTimer = new GameTimer(this);
        this.rollback = Main.getInstance().getWorldEdit().rollbacks.get(this.gameName);
    }

    public void loadButton() {
        if (this.rollback != null) {
            this.rollback.getBlockCache().forEach(blockData -> {
                if (blockData.getLocation().getBlock().getType() == Material.BEDROCK) {
                    setupButton(blockData.getLocation());
                }
            });
        }
    }

    public void setupButton(Location base) {

        Block block = base.getBlock();

        Block northBlock = base.getBlock().getRelative(BlockFace.NORTH);
        Block southBlock = base.getBlock().getRelative(BlockFace.SOUTH);
        Block westBlock = base.getBlock().getRelative(BlockFace.WEST);
        Block eastBlock = base.getBlock().getRelative(BlockFace.EAST);

        if (northBlock.getType() != Material.AIR && !northBlock.isLiquid()) block.setType(northBlock.getType());
        else if (southBlock.getType() != Material.AIR && !northBlock.isLiquid()) block.setType(southBlock.getType());
        else if (westBlock.getType() != Material.AIR && !northBlock.isLiquid()) block.setType(westBlock.getType());
        else if (eastBlock.getType() != Material.AIR && !northBlock.isLiquid()) block.setType(eastBlock.getType());

        if (northBlock.getType() == Material.AIR && !northBlock.isLiquid())
            northBlock.setType(northBlock.getType());
        else if (southBlock.getType() == Material.AIR && !northBlock.isLiquid())
            southBlock.setType(southBlock.getType());
        else if (westBlock.getType() == Material.AIR && !northBlock.isLiquid())
            westBlock.setType(westBlock.getType());
        else if (eastBlock.getType() == Material.AIR && !northBlock.isLiquid())
            eastBlock.setType(eastBlock.getType());

    }

    public void setRollback(WorldModel rollback) {
        this.rollback = rollback;
    }

    public WorldModel getRollback() {
        return rollback;
    }

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

    public GameStates getGameState() {
        return gameState;
    }

    public int getStartingTimeLeft() {
        return startingTimeLeft;
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

    public void setGameState(GameStates gameState) {
        this.gameState = gameState;
        Main.getInstance().getBungeeCordListener().updateGame(this);
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameTimer(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }

    public ArrayList<PlayerModel> getPlayers() {
        return players;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public Location getLobby() {
        return Encrypter.StringToLocation(this.lobby);
    }

    public String getCenter() {
        return center;
    }

    public Location getCenterLocation() {
        return Encrypter.StringToLocation(center);
    }

    public File getPath() {
        return path;
    }

    public void broadcast(String message) {
        this.players.forEach(playerModel -> {
            if (playerModel.getState() != PlayerState.NA) {
                playerModel.getPlayer().sendMessage(ColorText.translate(message));
            }
        });
    }

    public PlayerModel getPlayer(String playerName) {
        return players.stream().filter(playerModel -> playerModel.getPlayer().getDisplayName().equalsIgnoreCase(playerName)).findAny().orElse(null);
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
            return FileReader.load(new File(this.path, this.gameName + ".json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (GameModel) this;
    }

    public void reload() {
        this.load();
        this.save();
    }

    public GameInformation toGameInformation() {
        return new GameInformation(gameName, getPlayers().size(), getMaxPlayers(), getGameState().name());
    }

    public void addPlayer(PlayerModel mPlayer) {
        Main.getInstance().getBungeeCordListener().updateGame(this);
        mPlayer.getPlayer().teleport(getLobby());

        if (this.getPlayers().size() <= this.maxPlayers && !this.players.contains(mPlayer) && gameState == GameStates.WAITING) {
            if (this.getPlayers().size() == this.maxPlayers) {
                this.getTimer().setPaused(false);
            }
            this.players.add(mPlayer);
            mPlayer.toPreLobby();
            mPlayer.setState(PlayerState.WAITING);
            mPlayer.getPlayer().setDisplayName(ColorText.translate("&a&l[" + mPlayer.getPlayer().getDisplayName() + "&c&l]&r"));
            broadcast(mPlayer.getPlayer().getDisplayName() + "&cha entrado a la partida.");

            Main.getInstance().getBungeeCordListener().updateGame(this);
            getPlayers().forEach(Main.getInstance().getScoreboardDataHandler()::reloadData);
            return;
        }

        if (this.players.contains(mPlayer)) this.players.remove(maxPlayers);

        Main.getInstance().getBungeeCordListener().connect("lobby", mPlayer.getPlayer());
    }
}
