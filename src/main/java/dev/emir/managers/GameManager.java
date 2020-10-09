package dev.emir.managers;

import dev.emir.Main;
import dev.emir.models.GameModel;
import dev.emir.models.ManagerModel;
import dev.emir.models.PlayerModel;
import dev.emir.utils.FileReader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameManager extends ManagerModel<GameModel> {

    @Override
    public GameModel get(String identifier) {
        for (GameModel model : this.getList()) {
            if (model.getGameName().equals(identifier)) return model;
        }
        return null;
    }

    @Override
    public void update() {
        File path = new File(Main.getInstance().getDataFolder(), "games");

        if (path.isDirectory()) {
            File[] files = path.listFiles(File::isFile);
            if (files != null) {
                Arrays.stream(files).forEach(file -> {
                    if (file.getName().endsWith(".json")) {
                        try {
                            GameModel model = FileReader.load(file, new GameModel());
                            this.add(model);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public GameModel getByPlayerName(String playerName) {
        for (GameModel model : this.getList()) {
            for (List<PlayerModel> players : model.getPlayers().values()) {
                for (PlayerModel player : players) {
                    if (player.getPlayer().getDisplayName().equalsIgnoreCase(playerName)) return model;
                }
            }
        }
        return null;
    }
}
