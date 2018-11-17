package pl.socketbyte.minecraftparty.commons.io;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class I18n {

    private static I18n i18n;

    public static I18n get() {
        return i18n;
    }

    public static void set(I18n i18n) {
        I18n.i18n = i18n;
    }

    private FileConfiguration config;

    public I18n(FileConfiguration config) {
        this.config = config;
    }

    public String message(String key) {
        return this.config.getString(key);
    }

    public String message(String key, Object... replacements) {
        String value = message(key);

        for (int i = 0; i < replacements.length; i += 2) {
            String k = (String) replacements[i];
            Object v = replacements[i + 1];

            value = value.replace(k, String.valueOf(v));
        }
        return value;
    }

    public List<String> list(String key) {
        return this.config.getStringList(key);
    }

}
