package net.ilexiconn.llibrary.server.network;

import net.ilexiconn.llibrary.server.entity.block.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class BlockEntityMessage extends AbstractMessage<BlockEntityMessage> {
    private BlockPos pos;
    private NBTTagCompound compound;

    public BlockEntityMessage() {

    }

    public BlockEntityMessage(BlockEntity entity) {
        this.pos = entity.getPos();
        this.compound = new NBTTagCompound();
        entity.saveTrackingSensitiveData(this.compound);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft client, BlockEntityMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {
        BlockPos pos = message.pos;
        if (player.world.isBlockLoaded(pos)) {
            BlockEntity blockEntity = (BlockEntity) player.world.getTileEntity(pos);
            blockEntity.loadTrackingSensitiveData(message.compound);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, BlockEntityMessage message, EntityPlayer player, NetworkEvent.Context messageContext) {

    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.compound = buf.readCompoundTag();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeCompoundTag(this.compound);
    }

    @Override
    public boolean canSideReceive(Dist side) {
        return side.isClient();
    }
}
