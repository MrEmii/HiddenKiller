package dev.emir.managers;

import dev.emir.Main;
import dev.emir.noteblockapi.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SongsManager implements Listener {

    private ArrayList<Song> songs = new ArrayList<>();
    private int current = 0;
    private SongPlayer songPlayer;

    public SongsManager(Song... songs) {
        this.songs = new ArrayList<>(Arrays.asList(songs));
        songPlayer = new RadioSongPlayer(get(current));
    }

    public void addPlayer(Player player) {
        songPlayer.addPlayer(player);
    }

    public void setSong(Song song) {
        this.songPlayer = new RadioSongPlayer(song);
    }

    public void play() {
        songPlayer.setPlaying(true);
    }

    public Song current() {
        if (!(this.current >= this.songs.size())) {
            return this.songs.get(this.current);
        }
        return this.songs.get(0);
    }

    public void next() {
        this.current++;
        if (current() != null) {
            setSong(current());
            play();
        }
    }

    @EventHandler
    public void songStop(SongEndEvent e) {
        e.getSongPlayer().destroy();
        Main.getInstance().getSongsManager().next();
    }

    public void add(Song... songs) {
        Arrays.stream(songs).forEach(Main.getInstance().getSongsManager()::add);
    }

    public void add(Song song) {
        songs.add(song);
    }

    public void remove(Song song) {
        songs.remove(song);
    }

    public void remove(int index) {
        if (!(index >= songs.size())) {
            songs.remove(index);
        }
    }

    public Song get(int index) {
        return songs.get(index);
    }

    public void shuffle() {
        Collections.shuffle(songs);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}
