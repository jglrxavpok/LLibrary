package net.ilexiconn.llibrary.client.model.qubble;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author iLexiconn
 * @since 1.3.0
 */
public class QubbleAnimation implements INBTSerializable<NBTTagCompound> {
    private String name;
    private List<QubbleAnimationKeyframe> keyframes = new ArrayList<>();

    private QubbleAnimation() {

    }

    public static QubbleAnimation create(String name) {
        QubbleAnimation animation = new QubbleAnimation();
        animation.setName(name);
        return animation;
    }

    public static QubbleAnimation deserialize(NBTTagCompound compound) {
        QubbleAnimation animation = new QubbleAnimation();
        animation.deserializeNBT(compound);
        return animation;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.putString("name", this.name);
        NBTTagList keyframesTag = new NBTTagList();
        for (QubbleAnimationKeyframe keyframe : this.keyframes) {
            keyframesTag.add(keyframe.serializeNBT());
        }
        compound.put("keyframes", keyframesTag);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.name = compound.getString("name");
        NBTTagList keyframesTag = compound.getList("name", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < keyframesTag.size(); i++) {
            this.keyframes.add(QubbleAnimationKeyframe.deserialize(keyframesTag.getCompound(i)));
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QubbleAnimationKeyframe> getKeyframes() {
        return this.keyframes;
    }

    public QubbleAnimation copy() {
        QubbleAnimation animation = QubbleAnimation.create(this.getName());
        animation.getKeyframes().addAll(this.getKeyframes().stream().map(QubbleAnimationKeyframe::copy).collect(Collectors.toList()));
        return animation;
    }
}
