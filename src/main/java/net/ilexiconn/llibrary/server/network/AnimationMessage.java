package net.ilexiconn.llibrary.server.network;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class AnimationMessage extends AbstractMessage<AnimationMessage> {
    private int entityID;
    private int index;

    public AnimationMessage() {

    }

    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft client, AnimationMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {
        IAnimatedEntity entity = (IAnimatedEntity) player.world.getEntityByID(message.entityID);
        if (entity != null) {
            if (message.index == -1) {
                entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            } else {
                entity.setAnimation(entity.getAnimations()[message.index]);
            }
            entity.setAnimationTick(0);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, AnimationMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {

    }

    @Override
    public void fromBytes(PacketBuffer byteBuf) {
        this.entityID = byteBuf.readInt();
        this.index = byteBuf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer byteBuf) {
        byteBuf.writeInt(this.entityID);
        byteBuf.writeInt(this.index);
    }

    @Override
    public boolean canSideReceive(Dist side) {
        return side.isClient();
    }
}
