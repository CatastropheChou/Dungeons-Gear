package com.infamous.dungeons_gear.capabilities.combo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ComboStorage implements Capability.IStorage<ICombo> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ICombo> capability, ICombo instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("comboTimer", instance.getComboTimer());
        tag.putInt("comboCount", instance.getComboCount());
        //tag.putBoolean("ghostForm", instance.getGhostForm());
        tag.putBoolean("shadowForm", instance.getShadowForm());

        tag.putInt("flamingArrowsCount", instance.getFlamingArrowsCount());
        tag.putInt("tormentArrowsCount", instance.getTormentArrowsCount());
        tag.putInt("thunderingArrowsCount", instance.getThunderingArrowsCount());
        tag.putInt("harpoonCount", instance.getHarpoonCount());

        tag.putInt("arrowsInCounter", instance.getArrowsInCounter());
        tag.putInt("jumpCooldownTimer", instance.getJumpCooldownTimer());

        BlockPos lastExplorerCheckpoint = instance.getLastExplorerCheckpoint();
        tag.put("lastExplorerCheckpoint", this.newDoubleNBTList(lastExplorerCheckpoint.getX(), lastExplorerCheckpoint.getY(), lastExplorerCheckpoint.getZ()));

        BlockPos lastLuckyExplorerCheckpoint = instance.getLastLuckyExplorerCheckpoint();
        tag.put("lastLuckyExplorerCheckpoint", this.newDoubleNBTList(lastLuckyExplorerCheckpoint.getX(), lastLuckyExplorerCheckpoint.getY(), lastLuckyExplorerCheckpoint.getZ()));

        tag.putBoolean("artifactSynergy", instance.hasArtifactSynergy());

        tag.putInt("painCycleStacks", instance.getPainCycleStacks());

        tag.putInt("jumpCounter", instance.getJumpCounter());
        tag.putInt("refreshmentCounter", instance.getRefreshmentCounter());

        tag.putInt("echoCooldown", instance.getEchoCooldown());

        return tag;
    }

    @Override
    public void readNBT(Capability<ICombo> capability, ICombo instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setComboTimer(tag.getInt("comboTimer"));
        instance.setComboCount(tag.getInt("comboCount"));
        //instance.setGhostForm(tag.getBoolean("ghostForm"));
        instance.setShadowForm(tag.getBoolean("shadowForm"));

        instance.setFlamingArrowsCount(tag.getInt("flamingArrowsCount"));
        instance.setTormentArrowCount(tag.getInt("tormentArrowsCount"));
        instance.setThunderingArrowsCount(tag.getInt("thunderingArrowsCount"));
        instance.setHarpoonCount(tag.getInt("harpoonCount"));

        instance.setArrowsInCounter(tag.getInt("arrowsInCounter"));
        instance.setJumpCooldownTimer(tag.getInt("jumpCooldownTimer"));

        ListNBT listnbt = tag.getList("lastExplorerCheckpoint", 6);
        BlockPos lastExplorerCheckpoint = new BlockPos(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
        instance.setLastExplorerCheckpoint(lastExplorerCheckpoint);

        ListNBT listnbt1 = tag.getList("lastLuckyExplorerCheckpoint", 6);
        BlockPos lastLuckyExplorerCheckpoint = new BlockPos(listnbt1.getDouble(0), listnbt1.getDouble(1), listnbt1.getDouble(2));
        instance.setLastLuckyExplorerCheckpoint(lastLuckyExplorerCheckpoint);

        instance.setArtifactSynergy(tag.getBoolean("artifactSynergy"));

        instance.setPainCycleStacks(tag.getInt("painCycleStacks"));

        instance.setJumpCounter(tag.getInt("jumpCounter"));
        instance.setRefreshmentCounter(tag.getInt("refreshmentCounter"));

        instance.setEchoCooldown(tag.getInt("echoCooldown"));
    }

    private ListNBT newDoubleNBTList(double... numbers){
        ListNBT listnbt = new ListNBT();

        for(double d0 : numbers) {
            listnbt.add(DoubleNBT.valueOf(d0));
        }

        return listnbt;
    }
}
