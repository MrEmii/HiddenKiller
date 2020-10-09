package dev.emir.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Serializer {

    public static String LocationToString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }
    public static Location StringToLocation(String string) {
        if (!string.isEmpty()) {
            String[] splitString = string.split(":");

            World world = Bukkit.getWorld(splitString[0]);

            double x = Double.parseDouble(splitString[1]);
            double y = Double.parseDouble(splitString[2]);
            double z = Double.parseDouble(splitString[3]);
            float yaw = Float.parseFloat(splitString[4]);
            float pitch = Float.parseFloat(splitString[5]);

            Location newLocation = new Location(world, x, y, z);

            newLocation.setYaw(yaw);
            newLocation.setPitch(pitch);

            return newLocation;
        } else {
            return null;
        }

    }

}
