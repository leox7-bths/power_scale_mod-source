package com.example.power_scale_mod;

import com.example.power_scale_mod.attachment.ModAttachments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(PowerScaleMod.MODID)
public class PowerScaleMod {
    public static final String MODID = "power_scale_mod";

    public PowerScaleMod(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.register(modEventBus);
    }
}
