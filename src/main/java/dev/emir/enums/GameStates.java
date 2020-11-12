package dev.emir.enums;

public enum GameStates {
    WAITING("&aEsperando"), STARTING("&eEmpezando"),
    STARTED("&4Jugando"), FINISHED("&cTerminado"), IDLE("&7Ausente");

    String text;

    GameStates(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
