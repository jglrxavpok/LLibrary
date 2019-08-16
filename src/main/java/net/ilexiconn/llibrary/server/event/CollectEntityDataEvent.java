package net.ilexiconn.llibrary.server.event;

import net.ilexiconn.llibrary.server.capability.IEntityData;
import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collection;

public class CollectEntityDataEvent extends Event {
    private final Entity entity;
    private final Collection<IEntityData> data;

    public CollectEntityDataEvent(Entity entity, Collection<IEntityData> data) {
        this.entity = entity;
        this.data = data;
    }

    public <T extends Entity> void registerData(IEntityData<T> data) {
        this.data.add(data);
    }

    public Entity getEntity() {
        return this.entity;
    }
}
