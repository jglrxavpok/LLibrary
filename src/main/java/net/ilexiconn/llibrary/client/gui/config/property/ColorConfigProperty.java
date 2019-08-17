package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.ColorElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.minecraftforge.common.ForgeConfigSpec;

public class ColorConfigProperty extends ForgeConfigProperty<Integer> implements IIntProperty {
    public ColorConfigProperty(ForgeConfigSpec.IntValue configProperty) {
        super(configProperty);
    }

    @Override
    public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
        return new ColorElement<>(gui, x, y, 195, 149, this);
    }

    @Override
    public int getInt() {
        return this.property.get();
    }

    @Override
    public void setInt(int value) {
        //this.property.set(value);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidInt(int value) {
        return true;
    }
}
