package dev.emir.threads;

import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.models.GameModel;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {

    private GameModel<?> model;
    private boolean isPaused;

    public GameTimer(GameModel<?> model) {
        this.model = model;
        this.isPaused = false;

        this.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public void run() {
        if (!isPaused) {
            switch (model.getGameState()) {
                case WAITING:
                    if (model.getLobbyTimeLeft() != 0) {
                        model.subtractLobbyTimeLeft();
                        if (model.getLobbyTimeLeft() <= 4) {
                            model.broadcast("§aGame starts in §b" + model.getLobbyTimeLeft());
                        }
                    } else {
                        //TODO: ENVIAR AL MAPA
                        switch (model.getGameType()) {
                            case SOLO:
                                model.setGameState(GameStates.STARTING);
                                break;
                            case DUOS:
                                TeamGame duos = (TeamGame) model;
                                break;
                        }
                    }
                    break;
                case STARTING:
                    if (model.getStartingTimeLeft() != 0) {
                        model.subtractStartingTimeLeft();
                        //TODO: Enviar titulos de "Ya comienza"
                    } else {
                        //TODO: INICIAR JUEGO
                        model.setGameState(GameStates.STARTED);
                    }
                    break;
                case STARTED:
                    if (model.getGameTimeLeft() != 0) {
                        model.subtractGameTimeLeft();
                    } else {
                        model.setGameState(GameStates.FINISHED);
                    }
                    break;
                case FINISHED:
                    /*
                        TODO: Se reinicia el mapa, se guardan las stats y devuelve tru/false
                        Si es true: Se vuelve a waiting
                        Si es false: Se pone el juego en idle para solucionar el problema
                     */
                    break;
                case IDLE:
                    //TODO: Acá no sé jajajajajajajaj
                    break;
            }
        }
    }


}
