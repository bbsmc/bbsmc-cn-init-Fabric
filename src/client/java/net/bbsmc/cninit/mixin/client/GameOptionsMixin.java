package net.bbsmc.cninit.mixin.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.option.GameOptions;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    public String language;

    @Shadow
    public List<String> resourcePacks;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bbsmcOnOptionsLoaded(CallbackInfo ci) {
        try {
            File gameDir = net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getGameDir().toFile();
            File configFile = new File(gameDir, "config/modpack_info.json");
            if (!configFile.exists()) return;

            JsonObject config;
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                config = new Gson().fromJson(reader, JsonObject.class);
            }

            if (!"zh_cn".equals(this.language)) {
                this.language = "zh_cn";
            }

            JsonArray packsArray = config.getAsJsonArray("language_packs");
            if (packsArray != null) {
                File rpDir = new File(gameDir, "resourcepacks");
                // options.resourcePacks 末尾 = 最高优先级（FallbackResourceManager 从末尾向前查找）
                // 强制把汉化包放到列表末尾，覆盖其他第三方资源包
                for (int i = 0; i < packsArray.size(); i++) {
                    String packName = packsArray.get(i).getAsString();
                    String packId = "file/" + packName;
                    if (!new File(rpDir, packName).exists()) continue;
                    this.resourcePacks.remove(packId);
                    this.resourcePacks.add(packId);
                }
            }

            ((GameOptions) (Object) this).write();

        } catch (Exception e) {
            LoggerFactory.getLogger("bbsmc-cn-init")
                    .warn("Failed to pre-configure options: {}", e.getMessage());
        }
    }
}
