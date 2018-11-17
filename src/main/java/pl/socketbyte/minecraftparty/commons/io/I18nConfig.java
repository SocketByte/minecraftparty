package pl.socketbyte.minecraftparty.commons.io;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.socketbyte.minecraftparty.MinecraftParty;

import java.io.File;
import java.io.IOException;

public class I18nConfig {

    private final File file;
    private final File directory;

    public I18nConfig(String filePath) {
        this.directory = new File(MinecraftParty.getInstance().getDataFolder().getAbsolutePath());
        this.file = new File(this.directory + File.separator + filePath);
        create();
        load();
    }

    protected void create() {
        if (!this.file.exists()) {
            this.directory.mkdirs();
        }
        saveDefault();
    }

    protected void saveDefault() {
        MinecraftParty.getInstance().saveResource(this.file.getName(), false);
    }

    protected void load() {
        I18n.set(new I18n(YamlConfiguration.loadConfiguration(file)));
    }

}
