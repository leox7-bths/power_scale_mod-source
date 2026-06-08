package com.example.power_scale_mod.client;

import com.example.power_scale_mod.PowerScaleMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = PowerScaleMod.MODID, value = Dist.CLIENT)
public class ModKeyMappings {
    private static final String KEY_CATEGORY = "key.categories." + PowerScaleMod.MODID;

    public static final Lazy<KeyMapping> INCREASE_SCALE_KEY = Lazy.of(() -> new KeyMapping(
            "key." + PowerScaleMod.MODID + ".increase_scale",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            KEY_CATEGORY));

    public static final Lazy<KeyMapping> DECREASE_SCALE_KEY = Lazy.of(() -> new KeyMapping(
            "key." + PowerScaleMod.MODID + ".decrease_scale",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            KEY_CATEGORY));

    public static final Lazy<KeyMapping> OPEN_ATTRIBUTE_SCREEN_KEY = Lazy.of(() -> new KeyMapping(
            "key." + PowerScaleMod.MODID + ".open_attribute_screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            KEY_CATEGORY));

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(INCREASE_SCALE_KEY.get());
        event.register(DECREASE_SCALE_KEY.get());
        event.register(OPEN_ATTRIBUTE_SCREEN_KEY.get());
    }
}
