package me.logicalglitch.fix.proxynotify.config;

import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final PlayerNotifyPlugin plugin;
    private final Map<ConfigType, ConfigHandler> configurations;

    public ConfigManager(PlayerNotifyPlugin plugin) {
        this.configurations = new HashMap<>();
        this.plugin = plugin;
    }

    public void load() throws IOException, InvalidConfigurationException  {
        // register config.yml
        registerConfig(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
        // register config.yml
        registerConfig(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));

        // save default configs if it does not exist
        for (ConfigHandler configHandler : configurations.values()) {
            configHandler.saveDefault();
        }

        PluginMessage.setConfiguration(getConfig(ConfigType.MESSAGES).getConfig());
    }

    public ConfigHandler getConfig(ConfigType type) {
        return configurations.get(type);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigHandler::reload);
        PluginMessage.setConfiguration(getConfig(ConfigType.MESSAGES).getConfig());
    }

    public void save() throws IOException {
        getConfig(ConfigType.DATA).save();
    }

    private void registerConfig(ConfigType type, ConfigHandler config) {
        configurations.put(type, config);
    }

    public FileConfiguration getFileConfiguration(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

}
