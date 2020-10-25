package dev.emir.enums;

public enum PlayerState {
    NA("Viajando"), WAITING("Esperando"), STARTING("Empezando"), KILLER("&cAsesino"), VICTIM("&aVictima"), SPECTATOR("&7Espectador");

    String text;

    PlayerState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
