package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.server.property.IDoubleRangeProperty;
import net.ilexiconn.llibrary.server.property.wrapper.DoubleRangePropertyWrapper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleRangeConfigProperty extends ForgeConfigProperty<Double> implements IDoubleRangeProperty {
    private final double minDoubleValue;
    private final double maxDoubleValue;
    public DoubleRangeConfigProperty(ForgeConfigSpec.DoubleValue property) {
        super(property);
        // TODO
/*        this.minDoubleValue = Double.parseDouble(this.property.getMinValue());
        this.maxDoubleValue = Double.parseDouble(this.property.getMaxValue());*/
        this.minDoubleValue = Double.NEGATIVE_INFINITY;
        this.maxDoubleValue = Double.POSITIVE_INFINITY;
    }

    @Override
    public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        return new SliderElement<>(gui, x, y, new DoubleRangePropertyWrapper(this, new DecimalFormat("#.#", symbols)), 0.1F);
    }

    @Override
    public double getDouble() {
        return this.property.get();
    }

    @Override
    public void setDouble(double value) {
        //this.property.set(value);
        throw new UnsupportedOperationException();
    }

    @Override
    public double getMinDoubleValue() {
        return this.minDoubleValue;
    }

    @Override
    public double getMaxDoubleValue() {
        return this.maxDoubleValue;
    }
}
