package dev.emir.noteblockapi;

import org.bukkit.Sound;

public class Instrument {

    public static Sound getInstrument(byte instrument) {
        switch (instrument) {
            case 1:
                return Sound.NOTE_BASS;
            case 2:
                return Sound.NOTE_BASS_GUITAR;
            case 3:
                return Sound.NOTE_BASS_DRUM;
            case 4:
                return Sound.NOTE_SNARE_DRUM;
            case 5:
                return Sound.NOTE_STICKS;
            case 6:
                return Sound.NOTE_PLING;
            default:
                return Sound.NOTE_PIANO;

        }
    }

    public static org.bukkit.Instrument getBukkitInstrument(byte instrument) {
        switch (instrument) {
            case 1:
                return org.bukkit.Instrument.BASS_GUITAR;
            case 2:
                return org.bukkit.Instrument.BASS_DRUM;
            case 3:
                return org.bukkit.Instrument.SNARE_DRUM;
            case 4:
                return org.bukkit.Instrument.STICKS;
            default:
                return org.bukkit.Instrument.PIANO;
        }
    }
}
