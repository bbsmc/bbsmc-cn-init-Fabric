package net.bbsmc.cninit.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BbsmcCnInitClient implements ClientModInitializer {
    public static final String MODID = "bbsmc-cn-init";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Gson GSON = new Gson();
    public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private static boolean configLoaded = false;
    private static boolean userAgreement = false;
    private static List<String> languagePacks = new ArrayList<>();
    private static JsonObject modpackJson = null;
    private static File configFile = null;

    @Override
    public void onInitializeClient() {
        Config.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    public static void writeJsonToFile(File file, JsonObject json) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            GSON_PRETTY.toJson(json, writer);
        }
    }

    public static void markAgreed() { userAgreement = true; }

    public static Screen interceptScreen() {
        if (userAgreement) return null;
        if (!configLoaded) loadConfig(MinecraftClient.getInstance());
        if (userAgreement) return null;
        return new LocalizationNoticeScreen(modpackJson, languagePacks, configFile);
    }

    public static void setupLanguageAndPacks(MinecraftClient mc, List<String> languagePacks) {
        String currentLang = mc.getLanguageManager().getLanguage().getCode();
        String targetLang = "zh_cn";
        boolean languageChanged = false;
        if (!targetLang.equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            LanguageDefinition zhCn = mc.getLanguageManager().getLanguage(targetLang);
            if (zhCn != null) {
                mc.getLanguageManager().setLanguage(zhCn);
                mc.options.language = targetLang;
                mc.options.write();
                LOGGER.info("Language set to '{}'", targetLang);
                languageChanged = true;
            }
        }

        boolean packsChanged = false;
        if (!languagePacks.isEmpty()) {
            File resourcePacksDir = new File(mc.runDirectory, "resourcepacks");
            ResourcePackManager packRepository = mc.getResourcePackManager();
            packRepository.scanPacks();

            List<String> packsToEnable = new ArrayList<>();
            for (String packName : languagePacks) {
                File packFile = new File(resourcePacksDir, packName);
                if (packFile.exists()) {
                    packsToEnable.add("file/" + packName);
                } else {
                    LOGGER.warn("Resource pack not found: {}", packName);
                }
            }

            Collection<String> selected = new ArrayList<>(packRepository.getEnabledNames());
            for (String packId : packsToEnable) {
                ResourcePackProfile pack = packRepository.getProfile(packId);
                if (pack != null && !selected.contains(packId)) {
                    selected.add(packId);
                    packsChanged = true;
                    LOGGER.info("Auto-enabled resource pack: {}", packId);
                }
            }

            if (packsChanged) {
                packRepository.setEnabledProfiles(selected);
            }
        }

        if (languageChanged || packsChanged) {
            mc.reloadResources();
        }
    }

    private static void loadConfig(MinecraftClient mc) {
        if (configLoaded) return;
        configLoaded = true;

        configFile = new File(mc.runDirectory, "config/modpack_info.json");
        if (!configFile.exists()) {
            LOGGER.debug("modpack_info.json not found, skipping auto setup");
            userAgreement = true;
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            modpackJson = GSON.fromJson(reader, JsonObject.class);
            if (modpackJson.has("user_agreement")) {
                userAgreement = modpackJson.get("user_agreement").getAsBoolean();
            }
            JsonArray packsArray = modpackJson.getAsJsonArray("language_packs");
            if (packsArray != null) {
                for (int i = 0; i < packsArray.size(); i++) {
                    languagePacks.add(packsArray.get(i).getAsString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read modpack_info.json", e);
            userAgreement = true;
        }

    }

    private void onClientTick(MinecraftClient mc) {
        if (configLoaded) return;
        if (mc.currentScreen == null && mc.world == null) return;
        loadConfig(mc);
        setupLanguageAndPacks(mc, languagePacks);
    }
}
