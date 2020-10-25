package dev.emir.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.emir.interfaces.IConfigurationModel;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileReader {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> void encryptSave(IConfigurationModel<T> configurationModel, File sourceFile, String filename) throws IOException {
        OutputStreamWriter fileWriter;
        BufferedWriter buffWritter;
        File file = new File(sourceFile, filename.endsWith(".json") ? filename : filename.concat(".json"));
        if (!sourceFile.mkdirs()) System.out.println(sourceFile.toString() + " Are already created");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                System.out.println(filename + " Can't create");
                return;
            }
        }

        fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        buffWritter = new BufferedWriter(fileWriter);
        System.out.println(filename);
        buffWritter.write(Encrypter.encode(gson.toJson(configurationModel), 26));

        buffWritter.close();
        fileWriter.close();
    }

    public static <T> void save(IConfigurationModel<T> configurationModel, File sourceFile, String filename) throws IOException {
        OutputStreamWriter fileWriter;
        BufferedWriter buffWritter;
        File file = new File(sourceFile, filename.endsWith(".json") ? filename : filename.concat(".json"));
        if (!sourceFile.mkdirs()) System.out.println(sourceFile.toString() + " Are already created");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                System.out.println(filename + " Can't create");
                return;
            }
        }

        fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        buffWritter = new BufferedWriter(fileWriter);
        System.out.println(filename);
        buffWritter.write(gson.toJson(configurationModel));

        buffWritter.close();
        fileWriter.close();

    }

    public static <T> T load(File sourceFile, IConfigurationModel<T> classSource) throws IOException {
        BufferedReader bufferLectura;
        java.io.FileReader flujoLectura;
        String linea;
        if (sourceFile.exists()) {
            flujoLectura = new java.io.FileReader(sourceFile);
            bufferLectura = new BufferedReader(flujoLectura);
            StringBuilder ss = new StringBuilder();
            linea = bufferLectura.readLine();
            while (linea != null) {
                ss.append(linea);
                linea = bufferLectura.readLine();
            }

            return (T) gson.fromJson(ss.toString(), classSource.getClass());
        } else {
            classSource.save();
            return load(sourceFile, classSource);
        }
    }

    public static <T> T decryptLoad(File sourceFile, IConfigurationModel<T> classSource) throws IOException {
        BufferedReader bufferLectura;
        java.io.FileReader flujoLectura;
        String linea;
        if (sourceFile.exists()) {
            flujoLectura = new java.io.FileReader(sourceFile);
            bufferLectura = new BufferedReader(flujoLectura);
            StringBuilder ss = new StringBuilder();
            linea = bufferLectura.readLine();
            while (linea != null) {
                ss.append(linea);
                linea = bufferLectura.readLine();
            }

            return (T) gson.fromJson(Encrypter.decode(ss.toString(), 26), classSource.getClass());
        } else {
            classSource.save();
            return load(sourceFile, classSource);
        }
    }
}
