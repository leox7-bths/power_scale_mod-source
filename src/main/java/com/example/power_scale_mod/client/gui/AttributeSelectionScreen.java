package com.example.power_scale_mod.client.gui;

import com.example.power_scale_mod.PowerScaleMod;
import com.example.power_scale_mod.client.ClientScaleData;
import com.example.power_scale_mod.network.payload.SyncAttributeListPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttributeSelectionScreen extends Screen {
    private static final int ITEM_HEIGHT = 22;

    private final Set<String> workingTracked;
    private final List<AttributeEntry> allAttributes;
    private AttributeListWidget listWidget;
    private EditBox searchBox;
    private String lastSearch = "";

    public AttributeSelectionScreen() {
        super(Component.translatable("screen." + PowerScaleMod.MODID + ".attribute_selection"));
        this.workingTracked = new HashSet<>(ClientScaleData.getTrackedAttributeIds());
        this.allAttributes = new ArrayList<>();
        for (Attribute attr : BuiltInRegistries.ATTRIBUTE) {
            ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attr);
            if (key != null) {
                allAttributes.add(new AttributeEntry(key.toString(), attr));
            }
        }
        allAttributes.sort(Comparator.comparing(e -> e.id));
    }

    @Override
    protected void init() {
        int margin = 20;
        int listWidth = width - margin * 2;

        searchBox = new EditBox(font, margin, 28, listWidth, 20,
                Component.literal(""));
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        listWidget = new AttributeListWidget(minecraft, listWidth, height - 115, 55, ITEM_HEIGHT);
        listWidget.setX(margin);
        populateList();
        addRenderableWidget(listWidget);

        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                btn -> saveAndClose()
        ).bounds(width / 2 - 50, height - 28, 100, 20).build());
    }

    private void populateList() {
        listWidget.children().clear();
        String search = lastSearch.toLowerCase();
        for (AttributeEntry entry : allAttributes) {
            if (!search.isEmpty()) {
                String displayName = Component.translatable(entry.attribute.getDescriptionId()).getString().toLowerCase();
                if (!entry.id.contains(search) && !displayName.contains(search)) continue;
            }
            listWidget.children().add(new AttributeListEntry(entry));
        }
    }

    private void onSearchChanged(String search) {
        lastSearch = search;
        populateList();
    }

    private void toggleAttribute(String id) {
        if (workingTracked.contains(id)) {
            workingTracked.remove(id);
        } else {
            workingTracked.add(id);
        }
    }

    private void saveAndClose() {
        PacketDistributor.sendToServer(new SyncAttributeListPayload(new ArrayList<>(workingTracked)));
        onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, title, (width - font.width(title)) / 2, 8, 0xFFFFFF);
        guiGraphics.drawString(font,
                Component.translatable("screen." + PowerScaleMod.MODID + ".attribute_selection.hint"),
                20, height - 50, 0x888888);
    }

    private record AttributeEntry(String id, Attribute attribute) {}

    private class AttributeListWidget extends ObjectSelectionList<AttributeListEntry> {
        public AttributeListWidget(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return Math.min(440, width - 10);
        }
    }

    private class AttributeListEntry extends ObjectSelectionList.Entry<AttributeListEntry> {
        private final AttributeEntry entry;

        AttributeListEntry(AttributeEntry entry) {
            this.entry = entry;
        }

        @Override
        public Component getNarration() {
            return Component.literal(entry.id);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            boolean tracked = workingTracked.contains(entry.id);

            String check = tracked ? "\u2713" : "\u25CB";
            int checkColor = tracked ? 0xFF00CC66 : 0xFF888888;
            guiGraphics.drawString(font, check, left + 2, top + 1, checkColor);

            Component displayName = Component.translatable(entry.attribute.getDescriptionId());
            guiGraphics.drawString(font, displayName, left + 16, top + 1, 0xFFFFFF);

            guiGraphics.drawString(font, entry.id, left + 16, top + 11, 0xFF888888);

            if (hovering && tracked) {
                guiGraphics.fill(left + 2, top + 1, left + 14, top + 11, 0x33FF4444);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            toggleAttribute(entry.id);
            return true;
        }
    }
}
