package moe.ytonidc.ytongame_hostingmenu.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ytongame_hostingmenuClient implements ClientModInitializer {
    public static final String MODID = "ytongame-hostingmenu";
    public static final Identifier HOSTING_LOGO = new Identifier(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        Config.load();
        HostingPackage.loadAsync();
    }
}
