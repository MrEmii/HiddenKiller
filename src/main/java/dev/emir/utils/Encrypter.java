package dev.emir.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Encrypter {

    public static String decode(String enc, int offset) {
        return encode(enc, 26 - offset);
    }

    public static String encode(String enc, int offset) {
        offset = offset % 26 + 26;
        StringBuilder encoded = new StringBuilder();
        for (char i : enc.toCharArray()) {
            if (Character.isLetter(i)) {
                if (Character.isUpperCase(i)) {
                    encoded.append((char) ('A' + (i - 'A' + offset) % 26));
                } else {
                    encoded.append((char) ('a' + (i - 'a' + offset) % 26));
                }
            } else {
                encoded.append(i);
            }
        }
        return encoded.toString();
    }

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

    public static List<Location> StringToListLocation(List<String> strings) {
        List<Location> locations = new ArrayList<>();
        strings.forEach(s -> {
            locations.add(StringToLocation(s));
        });

        return locations;
    }
}
