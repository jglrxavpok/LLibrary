package net.ilexiconn.llibrary.server.capability;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * @author gegy1000
 * @since 1.0.0
 */
public class EntityDataCapabilityStorage implements Capability.IStorage<IEntityDataCapability> {
    @Override
    public INBTBase writeNBT(Capability<IEntityDataCapability> capability, IEntityDataCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        instance.saveToNBT(compound);
        return compound;
    }

    @Override
    public void readNBT(Capability<IEntityDataCapability> capability, IEntityDataCapability instance, EnumFacing side, INBTBase nbt) {
        instance.loadFromNBT((NBTTagCompound) nbt);
    }
}
