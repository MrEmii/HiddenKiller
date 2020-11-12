package dev.emir.commands;

import dev.emir.Main;
import dev.emir.utils.command.Command;
import dev.emir.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MainCommands {

    @Command(name = "gh", aliases = {"gameshiddencore", "core", "hub"}, permission = "gh.op", inGameOnly = true)
    public void setlobby(CommandArgs args) {
        if (args.getSender() instanceof Player) {
            Player p = args.getPlayer();
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            Main.getInstance().getConfig().set("spawn.world", p.getLocation().getWorld().getName());
            Main.getInstance().getConfig().set("spawn.x", p.getLocation().getX());
            Main.getInstance().getConfig().set("spawn.y", p.getLocation().getY());
            Main.getInstance().getConfig().set("spawn.z", p.getLocation().getZ());
            Main.getInstance().getConfig().set("spawn.yaw", p.getLocation().getYaw());
            Main.getInstance().getConfig().set("spawn.pitch", p.getLocation().getPitch());
            Main.getInstance().saveConfig();
            p.sendMessage(ChatColor.GREEN + "Se situo el lobby en tu ubicacion");
            p.sendMessage(ChatColor.GREEN + "Coordenadas:" + ChatColor.RESET + x + y + z);
        }
    }

}
