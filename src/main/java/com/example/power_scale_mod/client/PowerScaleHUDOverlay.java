package com.example.power_scale_mod.client;

import com.example.power_scale_mod.PowerScaleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = PowerScaleMod.MODID, value = Dist.CLIENT)
public class PowerScaleHUDOverlay {

    private static final int BAR_COLOR_ACTIVE = 0xFF00CC66;
    private static final int BAR_COLOR_INACTIVE = 0xFF333333;
    private static final int BAR_COLOR_BORDER = 0xFF888888;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int SLOT_SIZE = 8;
    private static final int GAP = 2;

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int currentLevel = ClientScaleData.getScaleLevel();

        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int totalWidth = 5 * SLOT_SIZE + 4 * GAP;

        int hotbarX = (screenWidth - 182) / 2;
        int hotbarY = screenHeight - 22;

        int barX = hotbarX - totalWidth - 8;
        int barY = hotbarY + 7;

        for (int i = 0; i < 5; i++) {
            int x = barX + i * (SLOT_SIZE + GAP);
            boolean filled = i < currentLevel;

            int bgColor = BAR_COLOR_INACTIVE;
            int borderColor = BAR_COLOR_BORDER;

            if (filled) {
                bgColor = BAR_COLOR_ACTIVE;
                borderColor = 0xFF00FF88;
            }

            graphics.fill(x, barY, x + SLOT_SIZE, barY + SLOT_SIZE, bgColor);

            graphics.fill(x, barY, x + 1, barY + SLOT_SIZE, borderColor);
            graphics.fill(x, barY, x + SLOT_SIZE, barY + 1, borderColor);
            graphics.fill(x + SLOT_SIZE - 1, barY, x + SLOT_SIZE, barY + SLOT_SIZE, borderColor);
            graphics.fill(x, barY + SLOT_SIZE - 1, x + SLOT_SIZE, barY + SLOT_SIZE, borderColor);
        }

        String label = currentLevel + "/5";
        int labelWidth = font.width(label);
        graphics.drawString(font, label, barX + (totalWidth - labelWidth) / 2, barY - 11, TEXT_COLOR);
    }
}
