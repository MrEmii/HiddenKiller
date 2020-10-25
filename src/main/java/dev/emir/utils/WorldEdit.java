package dev.emir.utils;

import dev.emir.Main;
import dev.emir.models.WorldModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class WorldEdit {

    public HashMap<String, WorldModel> rollbacks = new HashMap<>();

    public WorldEdit() {
        this.loadRollbacks();
    }

    public void saveRollbacks() {
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                rollbacks.forEach((s, worldModel) -> {
                    OutputStreamWriter fileWriter;
                    BufferedWriter buffWritter;
                    File file = new File(Main.getInstance().getDataFolder().getPath().concat(File.pathSeparator + "backups"), s.concat(".json"));
                    if (!new File(Main.getInstance().getDataFolder().getPath().concat(File.pathSeparator + "backups")).mkdirs())
                        System.out.println(Main.getInstance().getDataFolder().toString() + " Are already created");
                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                System.out.println(s.concat(".json") + " Can't create");
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                        buffWritter = new BufferedWriter(fileWriter);
                        buffWritter.write(Encrypter.encode(Main.gson.toJson(worldModel), 26));

                        buffWritter.close();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            }
        });
    }

    public void loadRollbacks() {
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                File dir = new File(Main.getInstance().getDataFolder().getPath().concat(File.pathSeparator + "backups"));
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

                                WorldModel model = Main.gson.fromJson(Encrypter.decode(ss.toString(), 26), WorldModel.class);
                                rollbacks.put(model.getCacheName(), model);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    public void makebackup(Location loc1, Location loc2) {
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockY(), loc2.getBlockZ());

        World world = loc1.getWorld();
        WorldModel model = new WorldModel();

        double height = Math.round(Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY)));
        Main.logger.fine("Leyendo bloques de " + world.getName() + "\noutput: " + model.getCacheName() + ".json");
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y < height; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if ((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                                if (world.getBlockAt(x, y, z) != null) {
                                    Block b = world.getBlockAt(x, y, z);
                                    model.getBlockCache().add(new WorldModel.BlockData(b));
                                }

                            }
                        }
                    }
                }
            }
        });
        Main.logger.fine("Se leyeron correctamente los bloques.");
        model.save();
        rollbacks.put(world.getName(), model);
    }

    public void rollback(String game) {
        this.rollback(rollbacks.get(game));
    }

    public void rollback(WorldModel model) {
        synchronized (this) {
            World world = Bukkit.getWorld(model.getCacheName().split("||")[0]);
            Main.logger.fine("Empezando rollback de " + model.toString());
            Multithreading.runAsync(new Runnable() {
                @Override
                public void run() {
                    model.getBlockCache().forEach(block -> world.getBlockAt(block.getLocation()).setType(block.getType()));
                }
            });
            Main.logger.fine("Se terminó el rollback de " + model.toString());
        }
    }

}
