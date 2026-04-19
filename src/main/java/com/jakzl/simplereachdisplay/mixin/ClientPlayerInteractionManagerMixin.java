package com.jakzl.simplereachdisplay.mixin;

import com.jakzl.simplereachdisplay.ModConfig;
import com.jakzl.simplereachdisplay.ReachDisplayState;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        ModConfig config;
        try {
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception e) {
            return;
        }

        if (!config.enabled) return;

        // Filter check
        boolean shouldShow = switch (config.targetFilter) {
            case PLAYERS_ONLY -> target instanceof PlayerEntity;
            case HOSTILE_MOBS_ONLY -> target instanceof HostileEntity;
            case PLAYERS_AND_HOSTILE -> target instanceof PlayerEntity || target instanceof HostileEntity;
            case PASSIVE_MOBS_ONLY -> target instanceof PassiveEntity;
            case ANY_ENTITY -> true;
        };

        if (!shouldShow) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        double distance = client.player.getPos().distanceTo(target.getPos());
        ReachDisplayState.recordHit(distance);
    }
}
