package com.infamous.dungeons_gear;


import com.infamous.dungeons_gear.capabilities.bow.IBow;
import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import com.infamous.dungeons_gear.capabilities.combo.RollHelper;
import com.infamous.dungeons_gear.compat.DungeonsGearCompatibility;
import com.infamous.dungeons_gear.effects.CustomEffects;
import com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList;
import com.infamous.dungeons_gear.enchantments.ranged.BurstBowstringEnchantment;
import com.infamous.dungeons_gear.enchantments.ranged.FuseShotEnchantment;
import com.infamous.dungeons_gear.enchantments.ranged.RollChargeEnchantment;
import com.infamous.dungeons_gear.items.GildedItemHelper;
import com.infamous.dungeons_gear.registry.PotionList;
import com.infamous.dungeons_gear.utilties.ArmorEffectHelper;
import com.infamous.dungeons_gear.utilties.CapabilityHelper;
import com.infamous.dungeons_gear.utilties.ProjectileEffectHelper;
import com.infamous.dungeons_libraries.items.interfaces.IComboWeapon;
import com.infamous.dungeons_libraries.utils.PetHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

import static com.infamous.dungeons_gear.DungeonsGear.PROXY;
import static com.infamous.dungeons_gear.config.DungeonsGearConfig.ENABLE_FRIENDLY_PET_FIRE;
import static com.infamous.dungeons_libraries.capabilities.minionmaster.MinionMasterHelper.getMinionCapability;
import static net.minecraft.item.Items.*;


@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class GlobalEvents {

    public static final String STUNNED_TAG = "Stunned";

    @SubscribeEvent
    public static void onArrowJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof AbstractArrowEntity) {
            AbstractArrowEntity arrowEntity = (AbstractArrowEntity) event.getEntity();
            //if(arrowEntity.getTags().contains("BonusProjectile") || arrowEntity.getTags().contains("ChainReactionProjectile")) return;
            Entity shooterEntity = arrowEntity.getOwner();
            if (shooterEntity instanceof LivingEntity) {
                LivingEntity shooter = (LivingEntity) shooterEntity;
                ItemStack mainhandStack = shooter.getMainHandItem();
                ItemStack offhandStack = shooter.getOffhandItem();
                // This should guarantee the arrow came from the correct itemstack
                if (mainhandStack.getItem() instanceof BowItem || mainhandStack.getItem() instanceof CrossbowItem) {
                    handleRangedEnchantments(arrowEntity, shooter, mainhandStack);
                } else if (offhandStack.getItem() instanceof BowItem || offhandStack.getItem() instanceof CrossbowItem) {
                    handleRangedEnchantments(arrowEntity, shooter, offhandStack);
                }
            }
        } else if (event.getEntity() instanceof ServerPlayerEntity) {
//            gildedGearTest((ServerPlayerEntity) event.getEntity());
        }
    }

    private static void gildedGearTest(ServerPlayerEntity entity) {
        dropGildedItem(entity, DIAMOND_SWORD);
        dropGildedItem(entity, DIAMOND_HELMET);
        dropGildedItem(entity, DIAMOND_CHESTPLATE);
        dropGildedItem(entity, DIAMOND_LEGGINGS);
        dropGildedItem(entity, DIAMOND_BOOTS);
    }

    private static void dropGildedItem(ServerPlayerEntity entity, Item item) {
        ItemStack sword = new ItemStack(item);
        ItemStack gildedItem = GildedItemHelper.getGildedItem(entity.getRandom(), sword);
        ItemEntity gildedItemDrop = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), gildedItem);
        entity.level.addFreshEntity(gildedItemDrop);
    }

    private static void handleRangedEnchantments(AbstractArrowEntity arrowEntity, LivingEntity shooter, ItemStack stack) {
        int fuseShotLevel = EnchantmentHelper.getItemEnchantmentLevel(RangedEnchantmentList.FUSE_SHOT, stack);
        if (fuseShotLevel > 0) {
            IBow weaponCap = CapabilityHelper.getWeaponCapability(stack);
            if (weaponCap == null) return;
            int fuseShotCounter = weaponCap.getFuseShotCounter();
            // 6 - 1, 6 - 2, 6 - 3
            // zero indexing, so subtract 1 as well
            if (fuseShotCounter == 6 - fuseShotLevel - 1) {
                arrowEntity.addTag(FuseShotEnchantment.FUSE_SHOT_TAG);
                weaponCap.setFuseShotCounter(0);
            } else {
                weaponCap.setFuseShotCounter(fuseShotCounter + 1);
            }
        }

        if (shooter instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) shooter;
            boolean soulsCriticalBoost = ProjectileEffectHelper.soulsCriticalBoost(playerEntity, stack);
            if (soulsCriticalBoost) {
                PROXY.spawnParticles(playerEntity, ParticleTypes.SOUL);
                arrowEntity.setCritArrow(true);
                arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * 2);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCancelAttackBecauseStunned(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity) event.getSource().getEntity();
            if (attacker.getEffect(CustomEffects.STUNNED) != null)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void comboForceCrit(CriticalHitEvent event) {
        if (event.getPlayer().getMainHandItem().getItem() instanceof IComboWeapon) {
            PlayerEntity p = event.getPlayer();
            ItemStack is = p.getMainHandItem();
            IComboWeapon ic = (IComboWeapon) is.getItem();
            ICombo cap = CapabilityHelper.getComboCapability(p);
            if (cap != null && ic.shouldProcSpecialEffects(is, p, cap.getComboCount())) {
                event.setResult(Event.Result.ALLOW);
                event.setDamageModifier(ic.damageMultiplier(is, p, cap.getComboCount()));
                cap.setComboCount(cap.getComboCount() % ic.getComboLength(is, p));
            } else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void resetCombo(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlotType.MAINHAND) {
            Optional.ofNullable(CapabilityHelper.getComboCapability(event.getEntityLiving())).ifPresent((a) -> a.setComboCount(0));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();

        if (living instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) living;
            if (mobEntity.getEffect(CustomEffects.STUNNED) != null && !mobEntity.getTags().contains(STUNNED_TAG)) {
                if (!mobEntity.isNoAi()) {
                    mobEntity.setNoAi(true);
                    mobEntity.addTag(STUNNED_TAG);
                }
            } else if (mobEntity.isNoAi() && mobEntity.getTags().contains(STUNNED_TAG)) {
                mobEntity.setNoAi(false);
                mobEntity.removeTag(STUNNED_TAG);
            }
        }
    }

    @SubscribeEvent
    public static void onCapabilityAttack(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) event.getSource().getEntity();

            ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
            if (comboCap == null) return;
            if (comboCap.getShadowForm()) {
                float originalDamage = event.getAmount();
                event.setAmount(originalDamage * 2.0F);
                comboCap.setShadowForm(false);
                playerEntity.removeEffect(Effects.INVISIBILITY);
            }
        }
    }

    @SubscribeEvent
    public static void onShadowFormAdded(LivingEntityUseItemEvent.Finish event) {
        if (PotionUtils.getPotion(event.getItem()) == PotionList.SHADOW_BREW) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
                ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
                if (comboCap == null) return;
                comboCap.setShadowForm(true);
            }
        }
    }

    @SubscribeEvent
    public static void onShadowFormRemoved(PotionEvent.PotionRemoveEvent event) {
        if (event.getPotion() == Effects.INVISIBILITY) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
                ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
                if (comboCap == null) return;
                comboCap.setShadowForm(false);
            }
        }
    }

    @SubscribeEvent
    public static void petDeath(LivingDamageEvent event) {
        //cancel friendly fire
        LivingEntity ouch = event.getEntityLiving();
        if (!ENABLE_FRIENDLY_PET_FIRE.get() && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity bonk = (LivingEntity) event.getSource().getEntity();
            if (PetHelper.isPetOrColleagueRelation(ouch, bonk)) {
                event.setCanceled(true);
                ouch.setLastHurtByMob(null);
                if (ouch instanceof MobEntity)
                    ((MobEntity) ouch).setTarget(null);
                bonk.setLastHurtByMob(null);
                if (bonk instanceof MobEntity)
                    ((MobEntity) bonk).setTarget(null);
            }
        }
        if (DungeonsGearCompatibility.saveYourPets) {
            if (getMinionCapability(event.getEntityLiving()) != null && event.getAmount() > event.getEntityLiving().getMaxHealth()) {
                event.getEntityLiving().remove();
                //so summoned wolves and llamas are disposable
            }
        }
    }

    @SubscribeEvent
    public static void handleJumpAbilities(LivingEvent.LivingJumpEvent event) {
        LivingEntity jumper = event.getEntityLiving();
        if (jumper instanceof PlayerEntity && !DungeonsGearCompatibility.elenaiDodge) {
            PlayerEntity playerEntity = (PlayerEntity) jumper;
            ItemStack helmet = playerEntity.getItemBySlot(EquipmentSlotType.HEAD);
            ItemStack chestplate = playerEntity.getItemBySlot(EquipmentSlotType.CHEST);
            ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
            if (comboCap == null) return;
            int jumpCooldownTimer = comboCap.getJumpCooldownTimer();

            if (jumpCooldownTimer <= 0) {
                ArmorEffectHelper.handleJumpBoost(playerEntity, helmet, chestplate);

                ArmorEffectHelper.handleInvulnerableJump(playerEntity, helmet, chestplate);

                ArmorEffectHelper.handleJumpEnchantments(playerEntity, helmet, chestplate);

                BurstBowstringEnchantment.activateBurstBowString(jumper);

                RollChargeEnchantment.activateRollCharge(jumper);
            }
            RollHelper.incrementJumpCounter(playerEntity);

            if(jumpCooldownTimer <= 0 && RollHelper.hasReachedJumpLimit(playerEntity)){
                RollHelper.startCooldown(jumper, comboCap);
            }
        }
    }

}
