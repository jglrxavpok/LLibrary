package net.ilexiconn.llibrary.client.model.qubble.vanilla;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.7.5
 */
public class QubbleVanillaTexture implements INBTSerializable<NBTTagCompound> {
    private String texture;
    private Map<String, String> properties = new HashMap<>();

    private QubbleVanillaTexture() {
    }

    public static QubbleVanillaTexture create(String value) {
        QubbleVanillaTexture texture = new QubbleVanillaTexture();
        texture.texture = value;
        return texture;
    }

    public static QubbleVanillaTexture deserialize(NBTTagCompound compound) {
        QubbleVanillaTexture texture = new QubbleVanillaTexture();
        texture.deserializeNBT(compound);
        return texture;
    }

    public String getTexture() {
        return this.texture;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    public boolean getBoolean(String key) {
        String property = this.getProperty(key);
        return property != null && property.equals("true");
    }

    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.putString("texture", this.texture);
        NBTTagList propertyList = new NBTTagList();
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            NBTTagCompound property = new NBTTagCompound();
            property.putString("key", entry.getKey());
            property.putString("value", entry.getValue());
            propertyList.add(property);
        }
        compound.put("properties", propertyList);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.texture = compound.getString("texture");
        NBTTagList propertyList = compound.getList("properties", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < propertyList.size(); i++) {
            NBTTagCompound property = propertyList.getCompound(i);
            if (property.contains("key") && property.contains("value")) {
                this.properties.put(property.getString("key"), property.getString("value"));
            }
        }
    }

    public QubbleVanillaTexture copy() {
        QubbleVanillaTexture texture = QubbleVanillaTexture.create(this.texture);
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            texture.setProperty(entry.getKey(), entry.getValue());
        }
        return texture;
    }
}
