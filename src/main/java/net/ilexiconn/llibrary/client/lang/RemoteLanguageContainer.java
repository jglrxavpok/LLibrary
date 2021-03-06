package net.ilexiconn.llibrary.client.lang;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author gegy1000
 * @since 1.1.0
 */
@OnlyIn(Dist.CLIENT)
public class RemoteLanguageContainer {
    public LangContainer[] languages;

    public class LangContainer {
        public String locale;
        public String downloadURL;
    }
}
