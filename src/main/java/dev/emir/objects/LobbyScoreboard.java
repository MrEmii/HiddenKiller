package dev.emir.objects;

import dev.emir.Main;
import dev.emir.models.PlayerModel;
import dev.emir.models.ScoreboardModel;
import dev.emir.utils.FileReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LobbyScoreboard extends ScoreboardModel {

    private String title = "&aGamerShip";
    private List<String> scoreboardText;

    final transient File path = new File(Main.getInstance().getDataFolder(), "boards");

    public LobbyScoreboard(Scoreboard score, String title, List<String> scoreboardText, PlayerModel target) {
        super(score, title.replaceAll("&", "§"));
        this.setTarget(target);
        this.title = title;
        this.scoreboardText = scoreboardText;
    }

    String blanks = ChatColor.RESET.toString();

    @Override
    public void sendBoard() {
        if (this.scoreboardText.size() > 0) {
            this.scoreboardText.forEach(text -> {
                blanks += ChatColor.RESET.toString();
                /*String finalText = text.replaceAll("&", "§").replace("{blank}", blanks).replace("{playerName}", getTarget().getPlayer().getDisplayName())
                        .replace("{kills}", String.valueOf(getTarget().getKills())).replace("{wins}", String.valueOf(getTarget().getWins()))
                        .replace("{played}", String.valueOf(Math.round(getTarget().getWins() - getTarget().getLosts()))).replace("{deaths}", String.valueOf(getTarget().getLosts()))
                        .replace("{coins}", String.valueOf(getTarget().getCoins())).replace("{level}", String.valueOf(getTarget().getLevel()));
                this.add(finalText);*/
            });
        }
        getTarget().getPlayer().setScoreboard(getScoreBoard());
        Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }, 0, 20);
    }

    public void save() {
        try {
            FileReader.save(this, this.path, "lobby_scoreboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LobbyScoreboard load() {
        try {
            return (LobbyScoreboard) FileReader.load(new File(this.path, "lobby_scoreboard.json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
