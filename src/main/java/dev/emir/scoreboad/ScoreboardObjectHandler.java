package dev.emir.scoreboad;

import dev.emir.Main;
import dev.emir.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardObjectHandler extends Handler implements Listener {
    private Map<UUID, ScoreboardObject> sbData;

    public ScoreboardObjectHandler() {
        this.sbData = new HashMap<UUID, ScoreboardObject>();
    }

    @Override
    public void enable() {
        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            this.loadData(player);
        });
    }

    public void reload() {
        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            this.reloadData(player);
        });
    }

    public ScoreboardObject getScoreboardFor(final Player player) {
        return this.sbData.get(player.getUniqueId());
    }

    public void loadData(final Player player) {
        final Scoreboard scoreboard = Main.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        this.sbData.put(player.getUniqueId(), new ScoreboardObject(scoreboard, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("scoreboard.title"))));
    }

    public void reloadData(final Player player) {
        if (this.sbData.containsKey(player.getUniqueId())) {
            final Scoreboard scoreaboard = player.getScoreboard();
            player.setScoreboard(scoreaboard);
            this.sbData.put(player.getUniqueId(), new ScoreboardObject(scoreaboard, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("scoreboard.title"))));
        }
    }

    public void reloadData(final PlayerModel mPlayer) {
        Player player = mPlayer.getPlayer();
        if (this.sbData.containsKey(player.getUniqueId())) {
            final Scoreboard scoreaboard = player.getScoreboard();
            player.setScoreboard(scoreaboard);
            this.sbData.put(player.getUniqueId(), new ScoreboardObject(scoreaboard, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("scoreboard.title"))));
        }
    }


    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.loadData(player);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        for (final String entries : player.getScoreboard().getEntries()) {
            player.getScoreboard().resetScores(entries);
        }
        this.sbData.remove(player);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        for (final String entries : player.getScoreboard().getEntries()) {
            player.getScoreboard().resetScores(entries);
        }
        this.sbData.remove(player);
    }
}
