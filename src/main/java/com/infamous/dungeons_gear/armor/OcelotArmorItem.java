package com.infamous.dungeons_gear.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.armor.models.OcelotArmorModel;
import com.infamous.dungeons_gear.init.ItemRegistry;
import com.infamous.dungeons_gear.interfaces.IArmor;
import com.infamous.dungeons_gear.utilties.DescriptionHelper;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.UUID;

public class OcelotArmorItem extends ArmorItem implements IArmor {

    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final boolean unique;
    private final int damageReduceAmount;
    private final float toughness;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public OcelotArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType slotType, Properties properties, boolean unique) {
        super(armorMaterial, slotType, properties);
        this.unique = unique;

        this.damageReduceAmount = armorMaterial.getDamageReductionAmount(slot);
        this.toughness = armorMaterial.getToughness() + 2.0F;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double)this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }this.attributeModifiers = builder.build();
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if(stack.getItem() == ItemRegistry.OCELOT_ARMOR.get() || stack.getItem() == ItemRegistry.OCELOT_ARMOR_HOOD.get()){
            return DungeonsGear.MODID + ":textures/models/armor/ocelot_armor.png";
        }
        else if(stack.getItem() == ItemRegistry.SHADOW_WALKER.get() || stack.getItem() == ItemRegistry.SHADOW_WALKER_HOOD.get()){
            return DungeonsGear.MODID + ":textures/models/armor/shadow_walker.png";
        }
        else return "";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A _default) {
        return (A) new OcelotArmorModel<>(1.0F, slot, entityLiving);
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == this.slot ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack){
        if(this.unique) return Rarity.RARE;
        return Rarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        DescriptionHelper.addLoreDescription(list, stack);
        if (this.unique) {
            list.add(new TranslationTextComponent(
                    "attribute.name.briefInvulnerabilityWhenJumping")
                    .mergeStyle(TextFormatting.GREEN));
        }
    }

    @Override
    public boolean doBriefInvulnerabilityWhenJumping() {
        return this.unique;
    }

    @Override
    public double getHigherJumps() {
        return 25;
    }
}
