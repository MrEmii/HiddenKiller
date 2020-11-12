package dev.emir.managers;

import dev.emir.Main;
import dev.emir.models.KitModel;
import dev.emir.models.ManagerModel;
import dev.emir.utils.FileReader;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class KitsManager extends ManagerModel<KitModel> {

    public KitsManager() {
        this.load();
    }

    public void save() {
        this.list.forEach(KitModel::save);
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                File dir = new File(Main.getInstance().getDataFolder(), "kits");
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

                                KitModel model = FileReader.gson.fromJson(ss.toString(), KitModel.class);
                                if (model.getName().equalsIgnoreCase("Default") && model.getItems().size() == 0) {
                                    model.loadDefault();
                                    model.save();
                                }

                                Main.getInstance().getKitsManager().add(model);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @Override
    public KitModel get(String identifier) {
        for (KitModel model : this.getList()) {
            if (model.getName().equals(identifier)) return model;
        }
        return null;
    }
}
