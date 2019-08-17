package net.ilexiconn.llibrary.server.network;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.capability.EntityDataHandler;
import net.ilexiconn.llibrary.server.capability.IEntityData;
import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

public class PropertiesMessage extends AbstractMessage<PropertiesMessage> {
    private String propertyID;
    private NBTTagCompound compound;
    private int entityID;

    public PropertiesMessage() {

    }

    public PropertiesMessage(EntityProperties<?> properties, Entity entity) {
        this.propertyID = properties.getID();
        NBTTagCompound compound = new NBTTagCompound();
        properties.saveTrackingSensitiveData(compound);
        this.compound = compound;
        this.entityID = entity.getEntityId();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft client, PropertiesMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {
        Entity entity = player.world.getEntityByID(message.entityID);
        if (entity != null) {
            IEntityData<?> extendedProperties = EntityDataHandler.INSTANCE.getEntityData(entity, message.propertyID);
            if (extendedProperties instanceof EntityProperties) {
                EntityProperties<?> properties = (EntityProperties) extendedProperties;
                properties.loadTrackingSensitiveData(message.compound);
                properties.onSync();
            }
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, PropertiesMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {

    }

    @Override
    public boolean canSideReceive(Dist side) {
        return side.isClient();
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        this.propertyID = buf.readString(2000);
        this.compound = buf.readCompoundTag();
        this.entityID = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.propertyID);
        buf.writeCompoundTag(this.compound);
        buf.writeInt(this.entityID);
    }
}
