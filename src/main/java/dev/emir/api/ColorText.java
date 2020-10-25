package dev.emir.api;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorText {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> translate(String... messages) {
        List<String> toReturn = new ArrayList<>();
        for (String message : messages) {
            toReturn.add(translate(message));
        }
        return toReturn;
    }
}