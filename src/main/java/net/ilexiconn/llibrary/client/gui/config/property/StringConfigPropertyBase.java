package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class StringConfigPropertyBase extends ForgeConfigProperty<String> implements IStringProperty {
    public StringConfigPropertyBase(ForgeConfigSpec.ConfigValue<String> property) {
        super(property);
    }

    @Override
    public String getString() {
        return this.property.get();
    }

    @Override
    public void setString(String value) {
        //this.property.set(value);
        throw new UnsupportedOperationException();
    }

    public static StringConfigPropertyBase factory(ForgeConfigSpec.ConfigValue property) {
        /*if (property.getValidValues().length > 0) {
            return new StringSelectionConfigProperty(property);
        } else {*/
            return new StringConfigProperty(property); // TODO
//        }
    }
}
