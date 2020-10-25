package dev.emir.noteblockapi;

import dev.emir.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Noteblock {

    public HashMap<String, ArrayList<SongPlayer>> playingSongs = new HashMap<String, ArrayList<SongPlayer>>();
    public HashMap<String, Byte> playerVolume = new HashMap<String, Byte>();

    public static boolean isReceivingSong(Player p) {
        return ((Main.getInstance().getNoteblock().playingSongs.get(p.getName()) != null) && (!Main.getInstance().getNoteblock().playingSongs.get(p.getName()).isEmpty()));
    }

    public static void stopPlaying(Player p) {
        if (Main.getInstance().getNoteblock().playingSongs.get(p.getName()) == null) {
            return;
        }
        for (SongPlayer s : Main.getInstance().getNoteblock().playingSongs.get(p.getName())) {
            s.removePlayer(p);
        }
    }

    public static void setPlayerVolume(Player p, byte volume) {
        Main.getInstance().getNoteblock().playerVolume.put(p.getName(), volume);
    }

    public static byte getPlayerVolume(Player p) {
        Byte b = Main.getInstance().getNoteblock().playerVolume.get(p.getName());
        if (b == null) {
            b = 100;
            Main.getInstance().getNoteblock().playerVolume.put(p.getName(), b);
        }
        return b;
    }

}
