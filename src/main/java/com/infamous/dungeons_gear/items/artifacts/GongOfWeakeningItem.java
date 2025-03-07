package com.infamous.dungeons_gear.items.artifacts;

import com.infamous.dungeons_gear.network.NetworkHandler;
import com.infamous.dungeons_libraries.network.BreakItemMessage;
import com.infamous.dungeons_libraries.items.artifacts.ArtifactItem;
import com.infamous.dungeons_libraries.items.artifacts.ArtifactUseContext;
import com.infamous.dungeons_gear.utilties.AreaOfEffectHelper;
import com.infamous.dungeons_gear.utilties.SoundHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

public class GongOfWeakeningItem extends ArtifactItem {
    public GongOfWeakeningItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> procArtifact(ArtifactUseContext c) {
        Player playerIn = c.getPlayer();
        ItemStack itemstack = c.getItemStack();
        Level world = c.getLevel();

        SoundHelper.playBellSound(playerIn);
        AreaOfEffectHelper.weakenAndMakeNearbyEnemiesVulnerable(playerIn, world, 5);

        itemstack.hurtAndBreak(1, playerIn, (entity) -> NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new BreakItemMessage(entity.getId(), itemstack)));

        ArtifactItem.putArtifactOnCooldown(playerIn, itemstack.getItem());
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }


    @Override
    public int getCooldownInSeconds() {
        return 20;
    }

    @Override
    public int getDurationInSeconds() {
        return 7;
    }
}
