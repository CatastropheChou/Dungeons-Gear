package com.infamous.dungeons_gear.utilties;

import com.infamous.dungeons_gear.capabilities.bow.Bow;
import com.infamous.dungeons_gear.capabilities.bow.BowProvider;
import com.infamous.dungeons_gear.capabilities.bow.IBow;
import com.infamous.dungeons_gear.capabilities.combo.Combo;
import com.infamous.dungeons_gear.capabilities.combo.ComboProvider;
import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class CapabilityHelper {

    @Nullable
    public static ICombo getComboCapability(Entity entity)
    {
        LazyOptional<ICombo> lazyCap = entity.getCapability(ComboProvider.COMBO_CAPABILITY);
        return lazyCap.orElse(new Combo());
    }

    @Nullable
    public static IBow getWeaponCapability(ItemStack stack)
    {
        LazyOptional<IBow> lazyCap = stack.getCapability(BowProvider.WEAPON_CAPABILITY);
        return lazyCap.orElse(new Bow());
    }
}
