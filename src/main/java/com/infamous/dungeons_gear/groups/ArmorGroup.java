package com.infamous.dungeons_gear.groups;

import com.infamous.dungeons_gear.registry.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ArmorGroup extends CreativeModeTab
{
    public ArmorGroup()
    {
        super("armor");
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(ItemRegistry.ROYAL_GUARD_ARMOR.getChest().get());
    }
}
