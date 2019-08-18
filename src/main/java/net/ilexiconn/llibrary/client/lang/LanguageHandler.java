package net.ilexiconn.llibrary.client.lang;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.1.0
 */
@OnlyIn(Dist.CLIENT)
public enum LanguageHandler {
    INSTANCE;

    private Map<String, Map<String, String>> localizations = new HashMap<>();

    public RemoteLanguageContainer loadRemoteLocalization(String modId) throws Exception {
        try (InputStream in = LanguageHandler.class.getResourceAsStream("/assets/" + modId.toLowerCase() + "/lang.json")) {
            if (in != null) {
                return new Gson().fromJson(new InputStreamReader(in), RemoteLanguageContainer.class);
            }
        }
        return null;
    }

    public void load() {
        File cacheDir = new File("llibrary/lang");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        for (File child : cacheDir.listFiles()) {
            if (child.isFile()) {
                try (FileInputStream cachedStream = new FileInputStream(child)) {
                    Map<String, String> lang = (new Gson()).fromJson(new InputStreamReader(cachedStream), Map.class);
                    this.localizations.put(child.getName().substring(0, child.getName().length() - ".lang".length()), lang);
                } catch (Exception e) {
                    LLibraryPlugin.LOGGER.error("An exception occurred while loading {} from cache.", child.getName(), e);
                }
            }
        }
        ModList.get().getMods().stream().forEach(modInfo -> {
            String modId = modInfo.getModId();
            try {
                RemoteLanguageContainer container = this.loadRemoteLocalization(modId);
                if (container != null) {
                    for (RemoteLanguageContainer.LangContainer language : container.languages) {
                        Map<String, String> lang = (new Gson()).fromJson(new InputStreamReader(new URL(language.downloadURL).openStream()), Map.class);
                        String locale = language.locale;
                        if (this.localizations.containsKey(locale)) {
                            lang.putAll(this.localizations.get(locale));
                        }
                        this.localizations.put(locale, lang);
                    }
                }
            } catch (Exception e) {
                LLibraryPlugin.LOGGER.error("An exception occurred while loading remote lang container for {}", modId, e);
            }
        });
        for (Map.Entry<String, Map<String, String>> entry : this.localizations.entrySet()) {
            String language = entry.getKey();
            File cache = new File(cacheDir, language + ".lang");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache), Charsets.UTF_8))) {
                for (Map.Entry<String, String> langEntry : entry.getValue().entrySet()) {
                    writer.append(langEntry.getKey()).append("=").append(langEntry.getValue()).append("\n");
                }
            } catch (Exception e) {
                LLibraryPlugin.LOGGER.error("An exception occurred while saving cache for {}", language);
            }
        }
    }

    public void addRemoteLocalizations(String language, Map<String, String> properties) {
        Map<String, String> localizationsForLang = this.localizations.get(language);
        if (localizationsForLang != null) {
            properties.putAll(localizationsForLang);
        }
    }
}
