package dev.emir.threads;

import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.enums.PlayerState;
import dev.emir.models.GameModel;
import dev.emir.models.PlayerModel;
import dev.emir.models.WorldModel;
import dev.emir.scoreboad.ScoreboardObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameTimer extends BukkitRunnable {

    private GameModel model;
    private boolean isPaused;
    private Date date;
    SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
    List<Location> possiblesTelepors = new ArrayList<>();

    public GameTimer(GameModel model) {
        this.model = model;
        this.date = new Date();
        this.isPaused = false;

        for (WorldModel.BlockData blockData : model.getRollback().getBlockCache()) {
            Block mainBlock = blockData.getLocation().getBlock();
            if (mainBlock.getY() > model.getCenterLocation().getBlockY() + 3 && mainBlock.getRelative(BlockFace.UP).getType() == Material.AIR && mainBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() == Material.AIR) {
                possiblesTelepors.add(blockData.getLocation());
            }
        }

        this.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public void run() {
        model.getPlayers().forEach(playerModel -> {

            ScoreboardObject scoreboard = Main.getInstance().getScoreboardDataHandler().getScoreboardFor(playerModel.getPlayer());

            scoreboard.clear();

            switch (playerModel.getState()) {
                case SPECTATOR:
                case KILLER:
                    Main.getInstance().getConfig().getStringList("scoreboard.killer").forEach(line -> {
                        line = line.replace("{playerState}", playerModel.getState().getText())
                                .replace("{playerKills}", String.valueOf(playerModel.getCurrentKills()))
                                .replace("{date}", DateFor.format(date))
                                .replace("{mapTime}", formatTime())
                                .replace("{counter}", String.valueOf(model.getPlayers().size()))
                                .replace("{mapName}", model.getGameName());

                        scoreboard.add(line);
                    });
                    break;
                case VICTIM:
                    Main.getInstance().getConfig().getStringList("scoreboard.survival").forEach(line -> {
                        line = line.replace("{playerState}", playerModel.getState().getText())
                                .replace("{date}", DateFor.format(date))
                                .replace("{mapTime}", formatTime())
                                .replace("{counter}", String.valueOf(model.getPlayers().size()))
                                .replace("{mapName}", model.getGameName());

                        scoreboard.add(line);
                    });
                    break;
                default:
                    Main.getInstance().getConfig().getStringList("scoreboard.preLobby").forEach(line -> {
                        line = line.replace("{username}", playerModel.getPlayer().getDisplayName())
                                .replace("{date}", DateFor.format(date))
                                .replace("{counter}", model.getPlayers().size() + "/" + model.getMaxPlayers())
                                .replace("{mapName}", model.getGameName());

                        scoreboard.add(line);
                    });
                    break;
            }

            scoreboard.update(playerModel.getPlayer());

            if (model.getLobbyTimeLeft() <= 4) {
                playerModel.sendActionBar("&6El juego comienza en:" + model.getLobbyTimeLeft() + " segundos");
            }
        });
        if (!isPaused) {
            switch (model.getGameState()) {
                case WAITING:
                    if (model.getLobbyTimeLeft() != 0) {
                        model.subtractLobbyTimeLeft();
                    } else {
                        Random random = new Random();
                        model.getPlayers().forEach(playerModel -> {
                            playerModel.getPlayer().teleport(possiblesTelepors.get(random.nextInt(possiblesTelepors.size())));
                            playerModel.getPlayer().getInventory().clear();
                            playerModel.setState(PlayerState.STARTING);

                            playerModel.sendTitle("&6Suerte.", "");
                        });

                        model.setGameState(GameStates.STARTING);
                    }
                    break;
                case STARTING:
                    if (model.getStartingTimeLeft() != 0) {
                        model.subtractStartingTimeLeft();
                        model.getPlayers().get(new Random().nextInt(model.getPlayers().size())).setState(PlayerState.KILLER);
                        model.getPlayers().forEach(playerModel -> {
                            if (playerModel.getState() == PlayerState.STARTING) {
                                playerModel.setState(PlayerState.VICTIM);
                            }
                            final ScoreboardObject scoreboard = Main.getInstance().getScoreboardDataHandler().getScoreboardFor(playerModel.getPlayer());


                            playerModel.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 4));
                            scoreboard.update(playerModel.getPlayer());
                        });
                    } else {
                        model.getPlayers().forEach(PlayerModel::toPlayer);
                        model.setGameState(GameStates.STARTED);
                    }
                    break;
                case STARTED:
                    if (model.getGameTimeLeft() != 0) {
                        model.subtractGameTimeLeft();
                    } else {
                        model.setGameState(GameStates.FINISHED);
                    }
                    break;
                case FINISHED:
                    model.getPlayers().forEach(PlayerModel::save);
                    Main.getInstance().getWorldEdit().rollback(model.getRollback());

                    /*
                        TODO: Se reinicia el mapa, se guardan las stats y devuelve tru/false
                        Si es true: Se vuelve a waiting
                        Si es false: Se pone el juego en idle para solucionar el problema
                     */
                    break;
                case IDLE:
                    //TODO: Acá no sé jajajajajajajaj
                    break;
            }
        }
    }


    public int getSeconds() {
        switch (model.getGameState()) {
            case WAITING:
                return model.getLobbyTimeLeft() % 60;
            case STARTING:
                return model.getStartingTimeLeft() % 60;
            case STARTED:
                return model.getGameTimeLeft() % 60;
            case FINISHED:
                return model.getEndTimeLeft() % 60;
            default:
                return 0;

        }
    }

    public int getMinutes() {
        switch (model.getGameState()) {
            case WAITING:
                return model.getLobbyTimeLeft() / 60;
            case STARTING:
                return model.getStartingTimeLeft() / 60;
            case STARTED:
                return model.getGameTimeLeft() / 60;
            case FINISHED:
                return model.getEndTimeLeft() / 60;
            default:
                return 0;

        }
    }

    public String formatTime() {
        int iminutes = getMinutes(), iseconds = getSeconds();
        String minutes = String.valueOf(getMinutes()), seconds = String.valueOf(getSeconds());
        if (iminutes < 10) {
            minutes = "0".concat(minutes);
        }

        if (iseconds < 10) {
            seconds = "0".concat(seconds);
        }

        return minutes + ":" + seconds;
    }


}
