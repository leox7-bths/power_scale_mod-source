package com.example.power_scale_mod.event;

import com.example.power_scale_mod.PowerScaleMod;
import com.example.power_scale_mod.attachment.ModAttachments;
import com.example.power_scale_mod.network.payload.SyncAttributeListPayload;
import com.example.power_scale_mod.network.payload.SyncScalePayload;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = PowerScaleMod.MODID)
public class PowerScaleEvents {

    private static final ResourceLocation POWER_SCALE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(PowerScaleMod.MODID, "power_scale");

    private static final double[] SCALE_MULTIPLIERS = {0.2, 0.4, 0.6, 0.8, 1.0};

    public static void changeScale(ServerPlayer player, int direction) {
        int currentLevel = player.getData(ModAttachments.SCALE_LEVEL.get());
        int newLevel = Math.max(1, Math.min(5, currentLevel + direction));

        if (newLevel == currentLevel) return;

        player.setData(ModAttachments.SCALE_LEVEL.get(), newLevel);
        applyPowerScale(player, newLevel);

        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
        player.displayClientMessage(
                Component.translatable("message." + PowerScaleMod.MODID + ".scale_changed", newLevel),
                true);

        PacketDistributor.sendToPlayer(player, new SyncScalePayload(newLevel));
    }

    public static void clearPowerScale(ServerPlayer player) {
        List<String> attributeIds = player.getData(ModAttachments.ATTRIBUTE_IDS.get());
        for (String attrId : attributeIds) {
            ResourceLocation loc = ResourceLocation.parse(attrId);
            var holderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(loc);
            if (holderOpt.isEmpty()) continue;
            AttributeInstance instance = player.getAttribute(holderOpt.get());
            if (instance != null) {
                instance.removeModifier(POWER_SCALE_MODIFIER_ID);
            }
        }
    }

    public static void applyPowerScale(ServerPlayer player, int level) {
        double multiplier = SCALE_MULTIPLIERS[level - 1];
        List<String> attributeIds = player.getData(ModAttachments.ATTRIBUTE_IDS.get());

        for (String attrId : attributeIds) {
            ResourceLocation loc = ResourceLocation.parse(attrId);
            var holderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(loc);
            if (holderOpt.isEmpty()) continue;
            Holder<Attribute> holder = holderOpt.get();

            AttributeInstance instance = player.getAttribute(holder);
            if (instance == null) continue;

            instance.removeModifier(POWER_SCALE_MODIFIER_ID);

            if (multiplier >= 1.0) continue;

            double baseValue = instance.getBaseValue();
            double currentValue = instance.getValue();

            if (currentValue > baseValue) {
                double targetValue = baseValue + (currentValue - baseValue) * multiplier;
                double modifierAmount = targetValue / currentValue - 1.0;

                instance.addTransientModifier(new AttributeModifier(
                        POWER_SCALE_MODIFIER_ID, modifierAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            int level = serverPlayer.getData(ModAttachments.SCALE_LEVEL.get());
            applyPowerScale(serverPlayer, level);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncScalePayload(level));
            PacketDistributor.sendToPlayer(serverPlayer, new SyncAttributeListPayload(
                    serverPlayer.getData(ModAttachments.ATTRIBUTE_IDS.get())));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            int level = serverPlayer.getData(ModAttachments.SCALE_LEVEL.get());
            applyPowerScale(serverPlayer, level);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncScalePayload(level));
            PacketDistributor.sendToPlayer(serverPlayer, new SyncAttributeListPayload(
                    serverPlayer.getData(ModAttachments.ATTRIBUTE_IDS.get())));
        }
    }
}
