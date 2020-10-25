package dev.emir.models;

import dev.emir.Main;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.utils.FileReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldModel implements IConfigurationModel<WorldModel> {

    private ArrayList<BlockData> blockCache = new ArrayList<BlockData>();
    private String cacheName = "";

    final transient File path = new File(Main.getInstance().getDataFolder(), "worlds");

    public ArrayList<BlockData> getBlockCache() {
        return blockCache;
    }

    public String getCacheName() {
        return cacheName;
    }

    @Override
    public String toString() {
        return this.cacheName;
    }

    public void save() {
        Main.logger.fine("Guardando archivo.");
        try {
            Main.logger.fine("Archivo guardado.");
            FileReader.save(this, this.path, this.cacheName);
        } catch (IOException e) {
            Main.logger.warning("No se pudó guardar el archivo.");
            Main.logger.info(e.getMessage());
        }
    }

    public WorldModel load() {
        Main.logger.fine("Cargando archivo.");
        try {
            Main.logger.fine("Archivo cargado.");
            return FileReader.load(new File(this.path, this.cacheName + ".json"), this);
        } catch (IOException e) {
            Main.logger.warning("No se pudó cargar el archivo.");
            Main.logger.info(e.getMessage());
        }
        return (WorldModel) this;
    }

    public static class BlockData {
        private Material type;
        private double x, y, z;
        private String world;
        private byte data;

        public BlockData(Block block) {
            this.type = block.getType();
            this.x = block.getLocation().getBlockX();
            this.y = block.getLocation().getBlockY();
            this.z = block.getLocation().getBlockZ();
            this.world = block.getLocation().getWorld().getName();
            this.data = block.getData();
        }

        public BlockData(Material type, Location location, byte data) {
            this.type = type;
            this.x = location.getBlockX();
            this.y = location.getBlockY();
            this.z = location.getBlockZ();
            this.world = location.getWorld().getName();
            this.data = data;
        }

        public BlockData(Material type, double x, double y, double z, byte data, String world) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.data = data;
            this.world = world;
        }

        public Material getType() {
            return type;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public String getWorld() {
            return world;
        }

        public byte getData() {
            return data;
        }

        public Location getLocation() {
            return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
        }
    }
}
