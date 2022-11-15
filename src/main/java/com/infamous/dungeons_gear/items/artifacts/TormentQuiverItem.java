package com.infamous.dungeons_gear.items.artifacts;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import com.infamous.dungeons_gear.network.NetworkHandler;
import com.infamous.dungeons_gear.network.BreakItemMessage;
import com.infamous.dungeons_gear.utilties.CapabilityHelper;
import com.infamous.dungeons_libraries.capabilities.soulcaster.SoulCasterHelper;
import com.infamous.dungeons_libraries.items.interfaces.ISoulConsumer;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

import static com.infamous.dungeons_libraries.attribute.AttributeRegistry.SOUL_GATHERING;
import com.infamous.dungeons_libraries.items.artifacts.ArtifactItem;
import com.infamous.dungeons_libraries.items.artifacts.ArtifactUseContext;

public class TormentQuiverItem extends ArtifactItem implements ISoulConsumer {
    public static final String TORMENT_ARROW = "TormentArrow";

    public TormentQuiverItem(Properties properties) {
        super(properties);
    }

    public ActionResult<ItemStack> procArtifact(ArtifactUseContext c) {
        PlayerEntity playerIn = c.getPlayer();
        ItemStack itemStack = c.getItemStack();

        if(SoulCasterHelper.consumeSouls(playerIn, this.getActivationCost(itemStack))){

            ICombo comboCap = CapabilityHelper.getComboCapability(playerIn);
            if(comboCap == null) return new ActionResult<>(ActionResultType.FAIL, itemStack);

            comboCap.setTormentArrowCount(3);

            itemStack.hurtAndBreak(1, playerIn, (entity) -> NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new BreakItemMessage(entity.getId(), itemStack)));

            ArtifactItem.putArtifactOnCooldown(playerIn, itemStack.getItem());
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }



    @Override
    public int getCooldownInSeconds() {
        return 1;
    }

    @Override
    public int getDurationInSeconds() {
        return 0;
    }

    @Override
    public float getActivationCost(ItemStack stack) {
        return 10;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(int slotIndex) {
        return getAttributeModifiersForSlot(getUUIDForSlot(slotIndex));
    }

    private ImmutableMultimap<Attribute, AttributeModifier> getAttributeModifiersForSlot(UUID slot_uuid) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(SOUL_GATHERING.get(), new AttributeModifier(slot_uuid, "Artifact modifier", 1, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }
}
