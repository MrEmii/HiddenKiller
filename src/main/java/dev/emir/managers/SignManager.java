package dev.emir.managers;

import dev.emir.Main;
import dev.emir.utils.Encrypter;
import org.bukkit.Location;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SignManager {

    protected HashMap<String, Location> signs;

    public SignManager() {
        this.signs = new HashMap<>();
        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Location> getSigns() {
        return signs;
    }

    public Location getSign(String game) {
        return signs.getOrDefault(game, null);
    }

    public String getGame(Location location) {
        for (String game : signs.keySet()) {
            if (getSign(game) == location)
                return game;
        }
        return "";
    }

    public boolean exist(Location location) {
        return signs.containsValue(location);
    }

    public boolean exist(String game) {
        return signs.containsKey(game);
    }

    public void addSign(String game, Location loc) {
        try {
            this.signs.putIfAbsent(game, loc);
            this.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        OutputStreamWriter fileWriter;
        BufferedWriter buffWritter;
        File file = new File(Main.getInstance().getDataFolder(), "signs.json");
        if (!Main.getInstance().getDataFolder().mkdirs())
            System.out.println(Main.getInstance().getDataFolder().toString() + " Are already created");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                System.out.println(signs + " Can't create");
                return;
            }
        }

        fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        buffWritter = new BufferedWriter(fileWriter);
        buffWritter.write(Encrypter.encode(Main.gson.toJson(signs), 26));

        buffWritter.close();
        fileWriter.close();
    }

    public void load() throws IOException {
        BufferedReader bufferLectura;
        java.io.FileReader flujoLectura;
        String linea;
        File sourceFile = new File(Main.getInstance().getDataFolder(), "signs.json");
        if (sourceFile.exists()) {
            flujoLectura = new java.io.FileReader(sourceFile);
            bufferLectura = new BufferedReader(flujoLectura);
            StringBuilder ss = new StringBuilder();
            linea = bufferLectura.readLine();
            while (linea != null) {
                ss.append(linea);
                linea = bufferLectura.readLine();
            }
            this.signs = Main.gson.fromJson(Encrypter.decode(ss.toString(), 26), getClass()).signs;
        } else {
            System.out.println("Sign's doesn't exist");
        }
    }

    public void reload() {
        try {
            load();
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
