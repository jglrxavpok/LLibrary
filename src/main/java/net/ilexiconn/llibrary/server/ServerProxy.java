package net.ilexiconn.llibrary.server;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.capability.EntityDataCapabilityImplementation;
import net.ilexiconn.llibrary.server.capability.EntityDataCapabilityStorage;
import net.ilexiconn.llibrary.server.capability.IEntityDataCapability;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.ilexiconn.llibrary.server.network.SnackbarMessage;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EntityPropertiesHandler.INSTANCE);
        CapabilityManager.INSTANCE.register(IEntityDataCapability.class, new EntityDataCapabilityStorage(), EntityDataCapabilityImplementation::new);
    }

    public void onInit() {
    }

    public void onPostInit() {
    }

    public <T extends AbstractMessage<T>> void handleMessage(final T message, final NetworkEvent.Context messageContext) {
        WorldServer world = (WorldServer) messageContext.getSender().world;
        world.addScheduledTask(() -> message.onServerReceived(world.getServer(), message, messageContext.getSender(), messageContext));
    }

    public float getPartialTicks() {
        return 0.0F;
    }

    public void showSnackbar(Snackbar snackbar) {
        LLibrary.NETWORK_WRAPPER.send(PacketDistributor.ALL.noArg(), new SnackbarMessage(snackbar));
    }

    public void setTPS(float tickRate) {
    }
}
