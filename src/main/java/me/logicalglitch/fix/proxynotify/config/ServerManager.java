package me.logicalglitch.fix.proxynotify.config;

import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;

import java.io.IOException;
import java.util.*;

public class ServerManager {
    private PlayerNotifyPlugin plugin;
    private Set<String> disabledServers;
    private Set<String> privateServers;
    private Map<String, String> serverNames;

    public ServerManager(PlayerNotifyPlugin plugin) throws IOException {
        this.plugin = plugin;
        disabledServers = new HashSet<>();
        privateServers = new HashSet<>();
        serverNames = new HashMap<>();
    }

    public FileConfiguration getSettings() {
        return plugin.getConfig(ConfigType.SETTINGS);
    }

    public void cacheServerNames() {
        serverNames.clear();
        List<String> serverNamesList = getSettings().getStringList("servers");
        if (serverNamesList.isEmpty()) {
            plugin.getLogger().warn("no display name is set for servers, please set server names in config.yml");
        }
        for (String entry : serverNamesList) {
            String[] keyval = entry.split(":");

            this.serverNames.put((keyval[0]).toLowerCase(), keyval[1]);
        }
    }

    public String getServerDispalyname(String server) {
        if (server == null) return null;
        return serverNames.getOrDefault(server.toLowerCase(), server.toUpperCase());
    }

    public void loadServerSettings() {
        this.disabledServers = new HashSet<>(getSettings().getStringList("disabled-servers"));
        this.privateServers = new HashSet<>(getSettings().getStringList("private-servers"));
        this.cacheServerNames();
    }

    public boolean isPrivate(String server) {
        if (privateServers == null || server == null) return false;
        return this.privateServers.contains(server.toLowerCase());
    }

    public boolean isDisabled(String server) {
        if (disabledServers == null || server == null) return false;
        return this.disabledServers.contains(server.toLowerCase());
    }
}
