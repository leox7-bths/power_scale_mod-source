package com.example.power_scale_mod.attachment;

import com.mojang.serialization.Codec;
import com.example.power_scale_mod.PowerScaleMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ModAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PowerScaleMod.MODID);

    static final List<String> DEFAULT_ATTRIBUTE_IDS = List.of(
            "minecraft:generic.movement_speed",
            "minecraft:generic.attack_damage",
            "minecraft:generic.attack_speed",
            "minecraft:generic.attack_knockback",
            "minecraft:generic.explosion_knockback_resistance",
            "minecraft:generic.flying_speed",
            "minecraft:generic.follow_range",
            "minecraft:generic.jump_strength",
            "minecraft:generic.knockback_resistance",
            "minecraft:generic.movement_efficiency",
            "minecraft:player.block_break_speed",
            "minecraft:player.block_interaction_range",
            "minecraft:player.entity_interaction_range"
    );

    public static final Supplier<AttachmentType<Integer>> SCALE_LEVEL = ATTACHMENTS.register(
            "scale_level",
            () -> AttachmentType.builder(() -> 5)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build());

    public static final Supplier<AttachmentType<List<String>>> ATTRIBUTE_IDS = ATTACHMENTS.register(
            "attribute_ids",
            () -> AttachmentType.builder(() -> DEFAULT_ATTRIBUTE_IDS)
                    .serialize(Codec.STRING.listOf())
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
