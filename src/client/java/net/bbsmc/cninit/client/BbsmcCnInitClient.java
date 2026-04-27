package net.bbsmc.cninit.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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

    public static void markAgreed() {
        userAgreement = true;
    }

    public static Screen interceptScreen() {
        if (userAgreement) {
            return null;
        }
        if (!configLoaded) {
            loadConfig(MinecraftClient.getInstance());
        }
        if (userAgreement) {
            return null;
        }
        return new LocalizationNoticeScreen(modpackJson, languagePacks, configFile);
    }

    public static void setupLanguageAndPacks(MinecraftClient mc, List<String> packs) {
        String currentLang = mc.getLanguageManager().getLanguage();
        String targetLang = "zh_cn";
        boolean languageChanged = false;
        if (!targetLang.equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            mc.getLanguageManager().setLanguage(targetLang);
            mc.options.language = targetLang;
            mc.options.write();
            LOGGER.info("Language set to '{}'", targetLang);
            languageChanged = true;
        }

        boolean packsChanged = false;
        if (!packs.isEmpty()) {
            File resourcePacksDir = new File(mc.runDirectory, "resourcepacks");
            ResourcePackManager packRepository = mc.getResourcePackManager();
            packRepository.scanPacks();

            List<String> packsToEnable = new ArrayList<>();
            for (String packName : packs) {
                File packFile = new File(resourcePacksDir, packName);
                String packId = "file/" + packName;
                if (packFile.exists() && packRepository.getProfile(packId) != null) {
                    packsToEnable.add(packId);
                } else {
                    LOGGER.warn("Resource pack not found: {}", packName);
                }
            }

            if (!packsToEnable.isEmpty()) {
                // 用 getEnabledProfiles() 拿到有序 Collection（getEnabledIds 返回 ImmutableSet 无序）
                List<String> selected = new ArrayList<>();
                for (ResourcePackProfile p : packRepository.getEnabledProfiles()) {
                    selected.add(p.getId());
                }

                // 末尾 = 最高优先级（FallbackResourceManager 从末尾向前查找资源）
                // 快速判断：packsToEnable 是否已按相同顺序排在 selected 末尾，是就直接跳过
                int n = packsToEnable.size();
                int s = selected.size();
                boolean alreadyAtTop = s >= n;
                if (alreadyAtTop) {
                    for (int i = 0; i < n; i++) {
                        if (!packsToEnable.get(i).equals(selected.get(s - n + i))) {
                            alreadyAtTop = false;
                            break;
                        }
                    }
                }

                if (!alreadyAtTop) {
                    for (String packId : packsToEnable) {
                        selected.remove(packId);
                    }
                    selected.addAll(packsToEnable);
                    packRepository.setEnabledProfiles(selected);
                    packsChanged = true;
                    LOGGER.info("Promoted resource packs to highest priority: {}", packsToEnable);
                } else {
                    LOGGER.debug("Resource packs already at highest priority, skipping reload");
                }
            }
        }

        if (languageChanged || packsChanged) {
            mc.reloadResources();
        }
    }

    private static void loadConfig(MinecraftClient mc) {
        if (configLoaded) {
            return;
        }
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
        if (configLoaded) {
            return;
        }

        if (mc.currentScreen == null && mc.world == null) {
            return;
        }

        loadConfig(mc);
    }
}
