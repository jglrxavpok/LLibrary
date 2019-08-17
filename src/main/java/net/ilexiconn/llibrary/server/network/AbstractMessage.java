package net.ilexiconn.llibrary.server.network;

import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public abstract class AbstractMessage<T extends AbstractMessage<T>> {

    public void onMessage(Supplier<NetworkEvent.Context> messageContextSupplier) {
        LLibrary.PROXY.handleMessage((T)this, messageContextSupplier.get());
    }

    /**
     * Executes when the message is received on CLIENT side. Never use fields directly from the class you're in, but
     * use data from the 'message' argument instead.
     *
     * @param client         the minecraft client instance.
     * @param message        The message instance with all variables.
     * @param player         The client player entity.
     * @param messageContext the message context.
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void onClientReceived(Minecraft client, T message, EntityPlayer player, NetworkEvent.Context messageContext);

    /**
     * Executes when the message is received on SERVER side. Never use fields directly from the class you're in, but
     * use data from the 'message' argument instead.
     *
     * @param server         the minecraft server instance.
     * @param message        The message instance with all variables.
     * @param player         The player who sent the message to the server.
     * @param messageContext the message context.
     */
    public abstract void onServerReceived(MinecraftServer server, T message, EntityPlayer player, NetworkEvent.Context messageContext);

    /**
     * @param side the current side
     * @return whether this message should be registered on the given side. Only used for messages registered with {@link net.ilexiconn.llibrary.server.network.NetworkHandler#registerMessage(net.minecraftforge.fml.network.simple.SimpleChannel, Class)}
     */
    public abstract boolean canSideReceive(Dist side);

    public abstract void toBytes(PacketBuffer packetBuffer);
    public abstract void fromBytes(PacketBuffer packetBuffer);
}
