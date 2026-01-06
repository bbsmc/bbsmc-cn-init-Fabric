package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class HostingPackageList extends AlwaysSelectedEntryListWidget<HostingPackageList.Entry> {

    public HostingPackageList(MinecraftClient minecraft, int width, int height, int top, int itemHeight) {
        super(minecraft, width, height, top, itemHeight);

        for (HostingPackage pkg : HostingPackage.getAllPackages()) {
            this.addEntry(new Entry(pkg));
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private final HostingPackage pkg;

        public Entry(HostingPackage pkg) {
            this.pkg = pkg;
        }

        @Override
        public void render(DrawContext context, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = client.textRenderer;

            if (hovering) {
                context.fill(left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
            }

            int borderColor = pkg.getColor();
            context.fill(left, top, left + 4, top + height, borderColor);

            int textLeft = left + 12;
            int line1Y = top + 4;
            int line2Y = top + 18;
            int line3Y = top + 32;

            context.drawText(font, pkg.getName(), textLeft, line1Y, pkg.getColor(), false);

            String tag = pkg.getTag();
            if (tag != null && !tag.isEmpty()) {
                int nameWidth = font.getWidth(pkg.getName());
                int tagX = textLeft + nameWidth + 6;
                int tagY = line1Y;
                int tagWidth = font.getWidth(tag) + 6;
                int tagHeight = 10;
                int tagBgColor = tag.equals("热销") ? 0xFFFF5555 : pkg.getColor();
                context.fill(tagX, tagY - 1, tagX + tagWidth, tagY + tagHeight, tagBgColor);
                context.drawText(font, tag, tagX + 3, tagY, 0xFFFFFFFF, false);
            }

            String priceText = "¥" + pkg.getPrice() + "/月";
            int priceWidth = font.getWidth(priceText);
            context.drawText(font, priceText, left + width - priceWidth - 10, line1Y, 0xFFFFFF55, false);

            String cpuLabel = "CPU: ";
            context.drawText(font, cpuLabel, textLeft, line2Y, 0xFFAAAAAA, false);
            int cpuLabelWidth = font.getWidth(cpuLabel);
            context.drawText(font, pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00, false);

            String memoryText = "内存: " + pkg.getMemory();
            context.drawText(font, memoryText, textLeft, line3Y, 0xFFAAAAAA, false);

            String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
            context.drawText(font, backupText, textLeft + 80, line3Y, 0xFFAAAAAA, false);

            String storageText = "存储: " + pkg.getStorage();
            context.drawText(font, storageText, textLeft + 160, line3Y, 0xFFAAAAAA, false);

            String playersText = "推荐: " + pkg.getRecommendedPlayers();
            context.drawText(font, playersText, textLeft + 250, line3Y, 0xFFAAAAAA, false);
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