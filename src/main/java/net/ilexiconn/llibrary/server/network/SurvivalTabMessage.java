package net.ilexiconn.llibrary.server.network;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.event.SurvivalTabClickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

public class SurvivalTabMessage extends AbstractMessage<SurvivalTabMessage> {
    private String label;

    public SurvivalTabMessage() {

    }

    public SurvivalTabMessage(String label) {
        this.label = label;
    }

    @Override
    public void onClientReceived(Minecraft client, SurvivalTabMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {

    }

    @Override
    public void onServerReceived(MinecraftServer server, SurvivalTabMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {
        MinecraftForge.EVENT_BUS.post(new SurvivalTabClickEvent(message.label, player));
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        this.label = buf.readString(2000);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.label);
    }

    @Override
    public boolean canSideReceive(Dist side) {
        return side.isDedicatedServer();
    }
}
