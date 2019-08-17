package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StringSelectionConfigProperty extends StringConfigPropertyBase implements IStringSelectionProperty {
    private final Set<String> validStringValues;

    // TODO: Are String arrays usable?
    public StringSelectionConfigProperty(ForgeConfigSpec.ConfigValue<String> property) {
        super(property);
        this.validStringValues = new HashSet<>();
// TODO        Collections.addAll(this.validStringValues, property.getValidValues());
    }

    @Override
    public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
        return new DropdownButtonElement(gui, x, y, 192, 12, this);
    }

    @Override
    public Set<String> getValidStringValues() {
        return this.validStringValues;
    }
}
