package net.ilexiconn.llibrary.server.network;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.snackbar.SnackbarPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

public class SnackbarMessage extends AbstractMessage<SnackbarMessage> {
    private Snackbar snackbar;

    public SnackbarMessage() {

    }

    public SnackbarMessage(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft client, SnackbarMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {
        LLibrary.PROXY.showSnackbar(message.snackbar);
    }

    @Override
    public void onServerReceived(MinecraftServer server, SnackbarMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {

    }

    @Override
    public void fromBytes(PacketBuffer byteBuf) {
        Snackbar snackbar = Snackbar.create(byteBuf.readString(2000));
        snackbar.setDuration(byteBuf.readInt());
        snackbar.setColor(byteBuf.readInt());
        snackbar.setPosition(SnackbarPosition.values()[byteBuf.readInt()]);
    }

    @Override
    public void toBytes(PacketBuffer byteBuf) {
        byteBuf.writeString(this.snackbar.getMessage());
        byteBuf.writeInt(this.snackbar.getDuration());
        byteBuf.writeInt(this.snackbar.getColor());
        byteBuf.writeInt(this.snackbar.getPosition().ordinal());
    }

    @Override
    public boolean canSideReceive(Dist side) {
        return side.isClient();
    }
}
