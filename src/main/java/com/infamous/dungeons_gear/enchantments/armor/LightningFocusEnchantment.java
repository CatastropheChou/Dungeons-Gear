package com.infamous.dungeons_gear.enchantments.armor;

import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.damagesources.ElectricShockDamageSource;
import com.infamous.dungeons_gear.damagesources.OffhandAttackDamageSource;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.enchantments.types.FocusEnchantment;
import com.infamous.dungeons_gear.utilties.PlayerAttackHelper;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class LightningFocusEnchantment extends FocusEnchantment {

    public LightningFocusEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onLightningAttack(LivingDamageEvent event){
        if(!(event.getSource() instanceof ElectricShockDamageSource)) return;
        if(event.getEntityLiving().level.isClientSide) return;

        if(event.getSource().getEntity() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            int lightningFocusLevel = EnchantmentHelper.getEnchantmentLevel(ArmorEnchantmentList.LIGHTNING_FOCUS, attacker);
            if(lightningFocusLevel > 0){
                float multiplier = 1 + (float) (DungeonsGearConfig.FOCUS_MULTIPLIER_PER_LEVEL.get() * lightningFocusLevel);
                float currentDamage = event.getAmount();
                event.setAmount(currentDamage * multiplier);
            }
        }
    }
}
