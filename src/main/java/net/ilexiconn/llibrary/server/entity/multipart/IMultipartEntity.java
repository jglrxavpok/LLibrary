package net.ilexiconn.llibrary.server.entity.multipart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;

public interface IMultipartEntity {
    Entity[] getParts();
    EntityType<PartEntity> getPartEntityType();

    default void onUpdateParts() {
        for (Entity entity : this.getParts()) {
            entity.tick();
        }
    }

    default PartEntity create(float radius, float angleYaw, float offsetY, float sizeX, float sizeY) {
        return this.create(radius, angleYaw, offsetY, sizeX, sizeY, 1.0F);
    }

    default PartEntity create(float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        return new PartEntity(getPartEntityType(), this.getEntity(), radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    default EntityLiving getEntity() {
        return (EntityLiving) this;
    }
}
