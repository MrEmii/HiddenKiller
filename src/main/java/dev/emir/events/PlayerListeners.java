package dev.emir.events;

import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.enums.PlayerState;
import dev.emir.models.GameModel;
import dev.emir.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;


public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinAsync(AsyncPlayerPreLoginEvent e) {
        try {
            PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getUniqueId().toString());
        } catch (Exception es) {
            es.printStackTrace();
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + "Ops... Who are you?");
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());

                for (int i = 0; i < 100; i++) {
                    player.sendMessage("");
                }

                if (!Main.getInstance().getConfig().getString("spawn.world").equalsIgnoreCase("undefined")) {
                    player.teleport(lobby());
                } else {
                    if (player.isOp())
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aUtiliza el comando &l/gh setlobby&a para setear el lobby!"));
                }
                try {
                    mPlayer.save();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                e.setJoinMessage(null);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent e) {
        PlayerModel mPlayer = Main.getInstance().getPlayerManager().get(e.getPlayer().getUniqueId().toString()).setPlayer(e.getPlayer());
        try {
            mPlayer.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.setLeaveMessage(null);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAchievement(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if (((event.getEntity() instanceof Player)) && (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) && !event.getEntity().isOp()) {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerTryKill(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerModel playerModel = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());
        if (player.isOp() || player.hasPermission("hk.admin")) return;
        event.setCancelled(true);
        if (playerModel.getState() != PlayerState.NA) {
            GameModel model = Main.getInstance().getGameManager().getByPlayerName(player.getName());
            if (model != null) {
                if (model.getGameState() != GameStates.WAITING || model.getGameState() != GameStates.STARTING && hasBlock(playerModel)) {
                    playerModel.addBlock();
                    event.setExpToDrop(0);
                    event.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public boolean hasBlock(PlayerModel model) {
        return model.getCurrentBlocks() < model.getCurrentKit().getBlocks();
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (!player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inter(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        PlayerModel playerModel = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());

        if (player.isOp() || player.hasPermission("hk.admin")) return;

        if (playerModel.getState() != PlayerState.NA) {
            GameModel model = Main.getInstance().getGameManager().getByPlayerName(player.getName());
            if (model != null) {
                if (model.getGameState() == GameStates.WAITING || model.getGameState() == GameStates.STARTING && playerModel.getState() != PlayerState.SPECTATOR) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (playerModel.getState() == PlayerState.NA) {
            Location from = e.getFrom();
            Location to = e.getTo();

            if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
                return;
            }
            if (e.getTo().getY() <= -44) {
                if (!Main.getInstance().getConfig().getString("spawn.world").equalsIgnoreCase("undefined")) {
                    Location lobby = lobby();

                    e.setTo(lobby);
                }

            }
        }

        e.setCancelled(false);
    }

    private static Location lobby() {
        Location lobby = new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("spawn.world")), Main.getInstance().getConfig().getDouble("spawn.x"), Main.getInstance().getConfig().getDouble("spawn.y"), Main.getInstance().getConfig().getDouble("spawn.z"));
        lobby.setYaw((float) Main.getInstance().getConfig().getDouble("spawn.yaw"));
        lobby.setPitch((float) Main.getInstance().getConfig().getDouble("spawn.pitch"));
        return lobby;
    }

}
