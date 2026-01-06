package moe.ytonidc.ytongame_hostingmenu.mixin.client.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationWidget.class)
public interface TabNavigationWidgetAccessor {
    @Accessor("tabs")
    ImmutableList<Tab> getTabs();
}