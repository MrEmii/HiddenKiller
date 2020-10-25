package dev.emir.events;

import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.enums.PlayerState;
import dev.emir.models.GameModel;
import dev.emir.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;


public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinAsync(AsyncPlayerPreLoginEvent e) {
        try {
            PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getUniqueId().toString());
            mPlayer.save();
        } catch (Exception es) {
            es.printStackTrace();
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + "Ops... Who are you?");
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        GameModel model = Main.getInstance().getGameManager().get(mPlayer.getUuid());
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
        Main.getInstance().getSongsManager().addPlayer(player);
        Main.getInstance().getSongsManager().play();
        StringBuilder color = new StringBuilder("§r");
        for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
            color.append("§r");
        }

        player.setDisplayName(color.toString());

        if (model == null) {
            if (!player.isOp() || !player.hasPermission("hk.op")) {
                Main.getInstance().getBungeeCordListener().connect("lobby", player);
            }
        } else {
            model.addPlayer(mPlayer);
        }

        e.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setLeaveMessage(null);
    }


    @EventHandler
    public void onPlayerAchievement(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player) && !event.getEntity().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageByEntityEvent event) {
        if (((event.getEntity() instanceof Player)) || ((event.getDamager() instanceof Player)) && !event.getEntity().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            player.setFoodLevel(20);
            player.setSaturation(10.0F);
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void playerDropItemEvent(PlayerDropItemEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("gh.drop"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSuffocation(EntityDamageEvent event) {
        if (((event.getEntity() instanceof Player)) && (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFallIntoVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerModel playerModel = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());
        if (playerModel.getState() != PlayerState.NA) {
            GameModel model = Main.getInstance().getGameManager().getByPlayerName(player.getName());
            if (model != null) {
                if (model.getGameState() == GameStates.WAITING || model.getGameState() == GameStates.STARTING) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (player.isOp() || player.hasPermission("hk.admin")) return;

        Main.getInstance().getBungeeCordListener().connect("hub", player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("gh.place"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            event.setExpToDrop(0);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        PlayerModel playerModel = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());
        if (playerModel.getState() != PlayerState.NA) {
            e.setCancelled(true);
            return;
        }

        if (player.isOp() || player.hasPermission("hk.admin")) return;

        Main.getInstance().getBungeeCordListener().connect("hub", player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        PlayerModel playerModel = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());
        if (playerModel.getState() != PlayerState.NA) {
            GameModel model = Main.getInstance().getGameManager().getByPlayerName(player.getName());
            if (model != null) {
                if (model.getGameState() == GameStates.WAITING || model.getGameState() == GameStates.STARTING && playerModel.getState() == PlayerState.STARTING) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (player.isOp() || player.hasPermission("hk.admin")) return;

        Main.getInstance().getBungeeCordListener().connect("hub", player);

    }

}
