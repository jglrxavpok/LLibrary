package net.ilexiconn.llibrary.server.entity.block;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.network.BlockEntityMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author iLexiconn
 * @since 1.3.0
 */
public abstract class BlockEntity extends TileEntity implements ITickable {
    private NBTTagCompound lastCompound;
    private int trackingUpdateTimer = 0;

    public BlockEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public final void tick() {
        int trackingUpdateFrequency = this.getTrackingUpdateTime();
        if (this.trackingUpdateTimer < trackingUpdateFrequency) {
            this.trackingUpdateTimer++;
        }
        if (this.trackingUpdateTimer >= trackingUpdateFrequency) {
            this.trackingUpdateTimer = 0;
            NBTTagCompound compound = new NBTTagCompound();
            this.saveTrackingSensitiveData(compound);
            if (!compound.equals(this.lastCompound)) {
                if (!this.world.isRemote) {
                    this.onSync();
                    PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 0, this.world.getDimension().getType());
                    LLibrary.NETWORK_WRAPPER.send(PacketDistributor.NEAR.with(() -> point), point);
                }
                this.lastCompound = compound;
            }
        }
        this.onUpdate();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
        this.read(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        this.saveNBTData(compound);
        return compound;
    }

    @Override
    public final void read(NBTTagCompound compound) {
        super.read(compound);
        this.loadNBTData(compound);
    }

    @Override
    public final NBTTagCompound write(NBTTagCompound compound) {
        super.write(compound);
        this.saveNBTData(compound);
        return compound;
    }

    /**
     * Write any tracking sensitive data to this NBT. The tracker will fire if
     * the NBT isn't equal and the tracking timer is ready.
     *
     * @param compound the compound to save to
     */
    public void saveTrackingSensitiveData(NBTTagCompound compound) {
        this.saveNBTData(compound);
    }

    /**
     * Client reads tracking sensitive data from this hook
     *
     * @param compound the compound to load from
     */
    public void loadTrackingSensitiveData(NBTTagCompound compound) {
        this.loadNBTData(compound);
    }

    /**
     * Save all needed data to the tag.
     *
     * @param compound the compound to save to
     */
    public abstract void saveNBTData(NBTTagCompound compound);

    /**
     * Load all needed data from the tag
     *
     * @param compound the compound to load from
     */
    public abstract void loadNBTData(NBTTagCompound compound);

    /**
     * Called every tick
     */
    public void onUpdate() {

    }

    /**
     * Called when the data is syncing
     */
    public void onSync() {

    }

    /**
     * @return how often the tracking sensitive data is compared
     */
    public int getTrackingUpdateTime() {
        return 0;
    }
}
