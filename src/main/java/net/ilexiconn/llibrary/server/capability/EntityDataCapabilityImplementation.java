package net.ilexiconn.llibrary.server.capability;

import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.0.0
 */
// TODO: 1.13: Move into IEntityDataCapability.Impl
public class EntityDataCapabilityImplementation implements IEntityDataCapability {
    private final Map<String, IEntityData> attachedData;
    // TODO: 1.13: Remove
    private final List<IEntityData> attachedDataList;

    public EntityDataCapabilityImplementation(List<IEntityData> data) {
        this.attachedData = new LinkedHashMap<>(data.size());
        this.attachedDataList = new ArrayList<>(data.size());
        for (IEntityData d : data) {
            this.attachedData.put(d.getID(), d);
            this.attachedDataList.add(d);
        }
    }

    public EntityDataCapabilityImplementation() {
        this(new ArrayList<>());
    }

    @Override
    public void init(Entity entity, World world, boolean init) {
        if (init) {
            for (IEntityData entityData : this.attachedData.values()) {
                entityData.init(entity, world);
            }
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound) {
        for (IEntityData entityData : this.attachedData.values()) {
            NBTTagCompound managerTag = new NBTTagCompound();
            entityData.saveNBTData(managerTag);
            compound.put(entityData.getID(), managerTag);
        }
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound) {
        for (IEntityData entityData : this.attachedData.values()) {
            NBTTagCompound managerTag = compound.getCompound(entityData.getID());
            entityData.loadNBTData(managerTag);
        }
    }

    @Override
    public <T extends Entity> void registerData(IEntityData<T> data) {
        this.attachedData.put(data.getID(), data);
        this.attachedDataList.add(data);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> IEntityData<T> getData(String identifier) {
        IEntityData retrieved = this.attachedData.get(identifier);
        return (IEntityData<T>) retrieved;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<IEntityData<T>> getData() {
        List data = Collections.unmodifiableList(this.attachedDataList);
        return (List<IEntityData<T>>) data;
    }

    @Override
    public INBTBase serializeNBT() {
        Capability<IEntityDataCapability> capability = LLibrary.ENTITY_DATA_CAPABILITY;
        return capability.getStorage().writeNBT(capability, this, null);
    }

    @Override
    public void deserializeNBT(INBTBase nbt) {
        Capability<IEntityDataCapability> capability = LLibrary.ENTITY_DATA_CAPABILITY;
        capability.getStorage().readNBT(capability, this, null, nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == LLibrary.ENTITY_DATA_CAPABILITY) {
            return (LazyOptional<T>) LazyOptional.of(() -> this);
        }
        return null;
    }
}
