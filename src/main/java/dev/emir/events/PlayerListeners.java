package dev.emir.events;

import dev.emir.Main;
import dev.emir.models.PlayerModel;
import dev.emir.objects.LobbyScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e) {
        PlayerModel model = Main.getInstance().getPlayerManager().get(e.getUniqueId().toString());
        if (model == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Lo sentimos, pero no puedes ingresar al servidor.\nVuelve a intentar");
        } else {
            model.save();
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        World playerWorld = player.getWorld();
        Location lobby = Main.getInstance().getConfiguration().getLobby();
        if (lobby != null && playerWorld.getName().equalsIgnoreCase(lobby.getWorld().getName())) {
            PlayerModel m_player = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());
            if (m_player != null) {
                m_player.setPlayer(player);
                player.teleport(Main.getInstance().getConfiguration().getLobby());
                LobbyScoreboard scoreboard = new LobbyScoreboard(Bukkit.getScoreboardManager().getNewScoreboard(), "&aGamerShip", Arrays.asList(
                        "&a",
                        "&rYour level: {level}",
                        "{blank}",
                        "&rKills: {kills}",
                        "&rWins: {wins}",
                        "&rPlayed: {played}",
                        "{blank}",
                        "&7Coins: &6{coins}",
                        "&ewww.server.net"
                ), m_player);
                Main.getInstance().getScoreboardManager().get(m_player, scoreboard).sendBoard();
                System.out.println("HOLA");
            } else {
                player.kickPlayer("Lo sentimos, pero no puedes ingresar al servidor.\nVuelve a intentar");
            }
        }
    }


}
