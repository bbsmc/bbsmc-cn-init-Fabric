package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class HostingPackageList extends AlwaysSelectedEntryListWidget<HostingPackageList.Entry> {

    public HostingPackageList(MinecraftClient minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);

        for (HostingPackage pkg : HostingPackage.getAllPackages()) {
            this.addEntry(new Entry(pkg));
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 6;
    }

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private final HostingPackage pkg;

        public Entry(HostingPackage pkg) {
            this.pkg = pkg;
        }

        @Override
        public void render(MatrixStack matrices, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = client.textRenderer;

            if (hovering) {
                DrawableHelper.fill(matrices, left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
            }

            int borderColor = pkg.getColor();
            DrawableHelper.fill(matrices, left, top, left + 4, top + height, borderColor);

            int textLeft = left + 12;
            int line1Y = top + 4;
            int line2Y = top + 18;
            int line3Y = top + 32;

            font.draw(matrices, pkg.getName(), textLeft, line1Y, pkg.getColor());

            String tag = pkg.getTag();
            if (tag != null && !tag.isEmpty()) {
                int nameWidth = font.getWidth(pkg.getName());
                int tagX = textLeft + nameWidth + 6;
                int tagY = line1Y;
                int tagWidth = font.getWidth(tag) + 6;
                int tagHeight = 10;
                int tagBgColor = tag.equals("热销") ? 0xFFFF5555 : pkg.getColor();
                DrawableHelper.fill(matrices, tagX, tagY - 1, tagX + tagWidth, tagY + tagHeight, tagBgColor);
                font.draw(matrices, tag, tagX + 3, tagY, 0xFFFFFFFF);
            }

            String priceText = "¥" + pkg.getPrice() + "/月";
            int priceWidth = font.getWidth(priceText);
            font.draw(matrices, priceText, left + width - priceWidth - 10, line1Y, 0xFFFFFF55);

            String cpuLabel = "CPU: ";
            font.draw(matrices, cpuLabel, textLeft, line2Y, 0xFFAAAAAA);
            int cpuLabelWidth = font.getWidth(cpuLabel);
            font.draw(matrices, pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00);

            String memoryText = "内存: " + pkg.getMemory();
            font.draw(matrices, memoryText, textLeft, line3Y, 0xFFAAAAAA);

            String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
            font.draw(matrices, backupText, textLeft + 80, line3Y, 0xFFAAAAAA);

            String storageText = "存储: " + pkg.getStorage();
            font.draw(matrices, storageText, textLeft + 160, line3Y, 0xFFAAAAAA);

            String playersText = "推荐: " + pkg.getRecommendedPlayers();
            font.draw(matrices, playersText, textLeft + 250, line3Y, 0xFFAAAAAA);
        }

        @Override
        public Text getNarration() {
            return Text.literal(pkg.getName() + " - ¥" + pkg.getPrice() + "/月");
        }

        public HostingPackage getPackage() {
            return pkg;
        }
    }
}
