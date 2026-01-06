package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.MinecraftClient;

public class RegionDetector {

    public static boolean shouldShowAds() {
        if (!Config.isAdsEnabled()) {
            return false;
        }

        if (!Config.isChineseOnly()) {
            return true;
        }

        try {
            String mcLanguage = MinecraftClient.getInstance().options.language;
            return "zh_cn".equalsIgnoreCase(mcLanguage);
        } catch (Exception e) {
            String systemLanguage = System.getProperty("user.language", "");
            String systemCountry = System.getProperty("user.country", "");
            return "zh".equalsIgnoreCase(systemLanguage) && "CN".equalsIgnoreCase(systemCountry);
        }
    }
}
