package com.infamous.dungeons_gear.enchantments.armor;

import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.HealthAbilityEnchantment;
import com.infamous.dungeons_gear.utilties.ModEnchantmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FrenziedEnchantment extends HealthAbilityEnchantment {
    private static final UUID FRENZY = UUID.fromString("86ded262-f5b3-41f0-a1ca-b881f6abfcff");

    public FrenziedEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET});
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player == null) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (player.isAlive()) {
            float maxHealth = player.getMaxHealth();
            float currentHealth = player.getHealth();
            player.getAttribute(Attributes.ATTACK_SPEED).removeModifier(FRENZY);
            if (currentHealth <= maxHealth / 2) {
                if (ModEnchantmentHelper.hasEnchantment(player, ArmorEnchantmentList.FRENZIED)) {
                    int frenziedLevel = EnchantmentHelper.getEnchantmentLevel(ArmorEnchantmentList.FRENZIED, player);
                    player.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(FRENZY, "frenzy multiplier", DungeonsGearConfig.FRENZIED_MULTIPLIER_PER_LEVEL.get() * frenziedLevel, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
        }
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return DungeonsGearConfig.ENABLE_OVERPOWERED_ENCHANTMENT_COMBOS.get() || !(enchantment instanceof HealthAbilityEnchantment);
    }
}
