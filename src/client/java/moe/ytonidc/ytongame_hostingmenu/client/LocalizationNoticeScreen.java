package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends Screen {

    private static final String TITLE_TEXT = "\u00a7l\u00a7eBBSMC汉化包使用须知";
    private static final String[] NOTICE_LINES = {
        "\u00a7f感谢您选择BBSMC汉化包，在正式使用BBSMC汉化包进行游戏前，我们希望您能阅读以下内容。",
        "",
        "\u00a76\u00a7l一、汉化内容",
        "\u00a7fBBSMC汉化包通过AI翻译+人工精校生成，汉化内容势必存在少量问题，如果您发现了任何汉化质量问题，您可以前往我们的网站：\u00a7n\u00a7bhttps://bbsmc.net\u00a7r\u00a7f，加入我们的玩家QQ群进行反馈，我们会及时处理您的反馈，并重新发布修改后的汉化包。",
        "\u00a7f若您当前游玩的整合包已经有完整的人工翻译，我们也相当欢迎您使用更精准更优质的人工翻译。",
        "",
        "\u00a76\u00a7l二、广告内容",
        "\u00a7fBBSMC汉化包含有仅出现在服务器多人列表和创建世界导航标签页面的服务器广告，不会对游戏体验造成影响。我们需要一定的收入来支撑汉化服务器的运转。\u00a7c\u00a7l如果觉得广告影响游戏体验，请先点击\"拒绝并退出\"关闭游戏，然后手动删除mods文件夹内的YTGame-HostingMenu.jar文件，再重新启动游戏即可。",
        ""
    };
    private static final String AGREE_TEXT = "同意并继续";
    private static final String DECLINE_TEXT = "拒绝并退出";

    private final JsonObject modpackJson;
    private final List<String> languagePacks;
    private final File configFile;

    private final List<OrderedText> wrappedLines = new ArrayList<>();

    public LocalizationNoticeScreen(JsonObject modpackJson, List<String> languagePacks, File configFile) {
        super(Text.literal(TITLE_TEXT));
        this.modpackJson = modpackJson;
        this.languagePacks = languagePacks;
        this.configFile = configFile;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int gap = 10;
        int totalWidth = buttonWidth * 2 + gap;
        int startX = (this.width - totalWidth) / 2;
        int buttonY = this.height - 40;

        this.addDrawableChild(new ButtonWidget(startX, buttonY, buttonWidth, buttonHeight,
                Text.literal(AGREE_TEXT),
                btn -> onAgree()));
        this.addDrawableChild(new ButtonWidget(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight,
                Text.literal(DECLINE_TEXT),
                btn -> onDecline()));

        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String line : NOTICE_LINES) {
            if (line.isEmpty()) {
                wrappedLines.add(OrderedText.EMPTY);
            } else {
                
                wrappedLines.addAll(this.textRenderer.wrapLines(Text.literal(line), maxWidth));
            }
        }
    }

    private void onAgree() {
        try {
            modpackJson.addProperty("user_agreement", true);
            Ytongame_hostingmenuClient.writeJsonToFile(configFile, modpackJson);
            Ytongame_hostingmenuClient.LOGGER.info("User agreed to localization notice, user_agreement set to true");
        } catch (Exception e) {
            Ytongame_hostingmenuClient.LOGGER.error("Failed to write modpack_info.json", e);
        }

        Ytongame_hostingmenuClient.markAgreed();
        Ytongame_hostingmenuClient.setupLanguageAndPacks(this.client, languagePacks);
        this.client.setScreen(new TitleScreen());
    }

    private void onDecline() {
        Ytongame_hostingmenuClient.LOGGER.info("User declined localization notice, shutting down");
        this.client.scheduleStop();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        int textX = 30;
        int textY = 40;
        int lineHeight = 11;

        for (OrderedText line : wrappedLines) {
            if (line != OrderedText.EMPTY) {
                this.textRenderer.drawWithShadow(matrices, line, textX, textY, 0xDDDDDD);
            }
            textY += lineHeight;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            onDecline();
            return true;
        }
        return false;
    }

}
