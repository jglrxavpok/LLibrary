package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.minecraftforge.common.ForgeConfigSpec;

public class StringConfigProperty extends StringConfigPropertyBase {
    public StringConfigProperty(ForgeConfigSpec.ConfigValue<String> property) {
        super(property);
    }

    @Override
    public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
        return new PropertyInputElement(gui, x, y, 192, this);
    }

    @Override
    public boolean isValidString(String value) {
        return false;
    }
}
