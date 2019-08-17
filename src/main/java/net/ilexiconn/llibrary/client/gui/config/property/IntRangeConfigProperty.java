package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.server.property.IIntRangeProperty;
import net.ilexiconn.llibrary.server.property.wrapper.IntRangePropertyWrapper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.text.NumberFormat;

public class IntRangeConfigProperty extends ForgeConfigProperty<Integer> implements IIntRangeProperty {
    private final int minIntValue;
    private final int maxIntValue;

    public IntRangeConfigProperty(ForgeConfigSpec.IntValue property) {
        super(property);
        // TODO
/*        this.minIntValue = Integer.parseInt(property.getMinValue());
        this.maxIntValue = Integer.parseInt(property.getMaxValue());*/
        this.minIntValue = Integer.MIN_VALUE;
        this.maxIntValue = Integer.MAX_VALUE;
    }

    @Override
    public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
        return new SliderElement<>(gui, x, y, new IntRangePropertyWrapper(this, NumberFormat.getIntegerInstance()), 1.0F);
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
    public int getMinIntValue() {
        return this.minIntValue;
    }

    @Override
    public int getMaxIntValue() {
        return this.maxIntValue;
    }
}
