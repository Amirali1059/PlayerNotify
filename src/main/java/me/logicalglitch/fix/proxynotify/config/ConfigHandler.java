package me.logicalglitch.fix.proxynotify.config;

import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigHandler {
    public final PlayerNotifyPlugin plugin;
    public final String name;
    public final File file;
    public FileConfiguration configuration;

    public ConfigHandler(PlayerNotifyPlugin plugin, String name) throws IOException {
        this.plugin = PlayerNotifyPlugin.getInstance();
        this.name = name;
        this.file = plugin.getDataDirectory().resolve(name + ".yml").toFile();
        this.configuration = new YamlConfiguration();
    }

    public void ensureExists() throws IOException {
        if (!this.file.exists()) {
            try (InputStream stream = PlayerNotifyPlugin.getInstance().getResource(this.name + ".yml")) {
                Files.copy(stream, this.file.toPath());
            }
        }
    }

    public void load() throws IOException, InvalidConfigurationException {
        this.ensureExists();
        this.configuration.load(this.file);
    }

    public void saveDefault() throws IOException, InvalidConfigurationException {
        this.load();
        getConfig().save(this.file);
    }

    public void save() throws IOException {
        if (configuration == null || file == null) return;
        getConfig().save(file);
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return configuration;
    }
}
