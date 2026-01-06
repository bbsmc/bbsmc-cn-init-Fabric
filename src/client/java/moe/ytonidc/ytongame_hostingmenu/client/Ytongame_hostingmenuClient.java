package moe.ytonidc.ytongame_hostingmenu.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ytongame_hostingmenuClient implements ClientModInitializer {
    public static final String MODID = "ytongame-hostingmenu";
    public static final Identifier HOSTING_LOGO = new Identifier(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        Config.load();
        HostingPackage.loadAsync();
    }
}