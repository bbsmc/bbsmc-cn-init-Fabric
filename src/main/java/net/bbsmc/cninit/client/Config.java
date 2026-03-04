package net.bbsmc.cninit.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("bbsmc-cn-init.json");

    private static ConfigData data = new ConfigData();

    public static class ConfigData {
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = new String(Files.readAllBytes(CONFIG_PATH));
                data = GSON.fromJson(json, ConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.write(CONFIG_PATH, GSON.toJson(data).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
