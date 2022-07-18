package com.infamous.dungeons_gear.enchantments.armor;

import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.utilties.AreaOfEffectHelper;
import com.infamous.dungeons_gear.utilties.ModEnchantmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class AltruisticEnchantment extends DungeonsEnchantment {

    public AltruisticEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET});
    }

    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
    LivingEntity livingEntity = event.getEntityLiving();
        if(ModEnchantmentHelper.hasEnchantment(livingEntity, ArmorEnchantmentList.ALTRUISTIC)){
            int altruisticLevel = EnchantmentHelper.getEnchantmentLevel(ArmorEnchantmentList.ALTRUISTIC, livingEntity);
            float damage = event.getAmount();
            float damageToHealingMultiplier = DungeonsGearConfig.ALTRUISTIC_DAMAGE_TO_HEALING_PER_LEVEL.get().floatValue() * altruisticLevel;
            AreaOfEffectHelper.healNearbyAllies(livingEntity, damage * damageToHealingMultiplier, 12);
        }
    }
}
