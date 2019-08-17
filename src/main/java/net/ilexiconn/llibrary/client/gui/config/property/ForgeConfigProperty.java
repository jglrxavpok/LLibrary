package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigProperty;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.stream.Collectors;

public abstract class ForgeConfigProperty<T> extends ConfigProperty {
    protected final ForgeConfigSpec.ConfigValue<T> property;

    public ForgeConfigProperty(ForgeConfigSpec.ConfigValue<T> property) {
        super(property.getPath().stream().collect(Collectors.joining("/")), property.toString());
        this.property = property;
    }

    public static ForgeConfigProperty factory(ForgeConfigSpec.ConfigValue<?> property) {
        if(property instanceof ForgeConfigSpec.BooleanValue) {
            return new BooleanConfigProperty((ForgeConfigSpec.BooleanValue) property);
        }
        if(property instanceof ForgeConfigSpec.DoubleValue) {
            return new DoubleRangeConfigProperty((ForgeConfigSpec.DoubleValue) property);
        }
        if(property instanceof ForgeConfigSpec.IntValue) {
            return new IntRangeConfigProperty((ForgeConfigSpec.IntValue) property);
        }
        return StringConfigProperty.factory(property);
         /*   case COLOR:
                return new ColorConfigProperty(property);
            case STRING:
                return StringConfigProperty.factory(property);*/
    }
}
