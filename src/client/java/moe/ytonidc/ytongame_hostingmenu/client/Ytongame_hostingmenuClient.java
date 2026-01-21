package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Ytongame_hostingmenuClient implements ClientModInitializer {
    public static final String MODID = "ytongame-hostingmenu";
    public static final Identifier HOSTING_LOGO = Identifier.of(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Gson GSON = new Gson();

    @Override
    public void onInitializeClient() {
        Config.load();
        HostingPackage.loadAsync();

        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStarted);
    }

    private void onClientStarted(MinecraftClient mc) {
        File configFile = new File(mc.runDirectory, "config/modpack_info.json");
        if (!configFile.exists()) {
            LOGGER.debug("modpack_info.json not found, skipping auto setup");
            return;
        }

        // 检查并设置语言为简体中文
        String currentLang = mc.getLanguageManager().getLanguage();
        if (!"zh_cn".equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            mc.getLanguageManager().setLanguage("zh_cn");
            mc.reloadResources();
        }

        List<String> languagePacks = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);

            JsonObject resourcePackInstall = json.getAsJsonObject("resource_pack_install");
            if (resourcePackInstall != null && resourcePackInstall.has("auto_install_enabled")) {
                boolean autoInstallEnabled = resourcePackInstall.get("auto_install_enabled").getAsBoolean();
                if (autoInstallEnabled) {
                    LOGGER.debug("Auto install already enabled by other means, skipping");
                    return;
                }
            }

            JsonArray packsArray = json.getAsJsonArray("language_packs");
            if (packsArray != null) {
                for (int i = 0; i < packsArray.size(); i++) {
                    languagePacks.add(packsArray.get(i).getAsString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read modpack_info.json", e);
            return;
        }

        if (languagePacks.isEmpty()) {
            return;
        }

        File resourcePacksDir = new File(mc.runDirectory, "resourcepacks");
        List<String> packsToEnable = new ArrayList<>();
        for (String packName : languagePacks) {
            File packFile = new File(resourcePacksDir, packName);
            if (packFile.exists()) {
                packsToEnable.add("file/" + packName);
            } else {
                LOGGER.warn("Resource pack not found: {}", packName);
            }
        }

        if (packsToEnable.isEmpty()) {
            return;
        }

        mc.execute(() -> {
            ResourcePackManager packRepository = mc.getResourcePackManager();
            packRepository.scanPacks();

            Collection<String> selected = new ArrayList<>(packRepository.getEnabledIds());
            boolean changed = false;
            for (String packId : packsToEnable) {
                ResourcePackProfile pack = packRepository.getProfile(packId);
                if (pack != null && !selected.contains(packId)) {
                    selected.add(packId);
                    changed = true;
                    LOGGER.info("Auto-enabled resource pack: {}", packId);
                }
            }

            if (changed) {
                packRepository.setEnabledProfiles(selected);
                mc.reloadResources();
            }
        });
    }
}