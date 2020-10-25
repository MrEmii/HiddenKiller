package dev.emir.managers;

import dev.emir.Main;
import dev.emir.models.*;
import dev.emir.utils.Encrypter;
import dev.emir.utils.Multithreading;
import org.bukkit.GameMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class GameManager extends ManagerModel<GameModel> {

    public GameManager() {
        load();
    }

    public void save() {
        this.getList().forEach(GameModel::save);
    }

    public void load() {
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                File dir = new File(Main.getInstance().getDataFolder().getPath().concat(File.pathSeparator + "games"));
                File[] files = dir.listFiles(File::isFile);
                if (files != null && files.length > 0) {
                    Arrays.stream(files).forEach(file -> {
                        try {
                            BufferedReader bufferLectura;
                            java.io.FileReader flujoLectura;
                            String linea;
                            if (file.exists()) {
                                flujoLectura = new java.io.FileReader(file);
                                bufferLectura = new BufferedReader(flujoLectura);
                                StringBuilder ss = new StringBuilder();
                                linea = bufferLectura.readLine();
                                while (linea != null) {
                                    ss.append(linea);
                                    linea = bufferLectura.readLine();
                                }

                                GameModel model = Main.gson.fromJson(Encrypter.decode(ss.toString(), 26), GameModel.class);
                                model.setRollback(Main.getInstance().getWorldEdit().rollbacks.get(model.getGameName()));
                                model.loadButton();

                                add(model);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    public GameModel get(String identifier) {
        for (GameModel model : this.getList()) {
            if (model.getGameName().equals(identifier)) return model;
        }
        return null;
    }

    public GameModel getByPlayerName(String playerName) {
        for (GameModel model : this.getList()) {
            for (PlayerModel player : model.getPlayers()) {
                if (player.getPlayer().getDisplayName().equalsIgnoreCase(playerName)) return model;

            }
        }
        return null;
    }
}
