package me.logicalglitch.fix.proxynotify;

import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import com.velocitypowered.api.proxy.ServerConnection;
import de.myzelyam.api.vanish.VelocityVanishAPI;
import me.logicalglitch.fix.proxynotify.commands.PlayerNotifyCommand;
import me.logicalglitch.fix.proxynotify.commands.ToggleMessagesCommand;
import me.logicalglitch.fix.proxynotify.config.ConfigManager;
import me.logicalglitch.fix.proxynotify.config.ConfigType;

import me.logicalglitch.fix.proxynotify.config.ServerManager;
import me.logicalglitch.fix.proxynotify.listeners.OnPlayerJoin;
import me.logicalglitch.fix.proxynotify.listeners.OnPlayerLeave;
import me.logicalglitch.fix.proxynotify.listeners.OnPlayerSwitch;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;
import me.logicalglitch.fix.proxynotify.runtime.PlayerManager;

import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.LuckPermsProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import net.luckperms.api.cacheddata.CachedMetaData;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.slf4j.Logger;

@Plugin(
        id = "playernotify",
        name = "PlayerNotify",
        authors = "LogicalGlitch",
        version = "1.0",
        dependencies = {
                @Dependency(id = "luckperms", optional = true),
                @Dependency(id = "supervanish", optional = true),
                @Dependency(id = "premiumvanish", optional = true),
        }
)
public class PlayerNotifyPlugin {

    private static PlayerNotifyPlugin instance;
    private final ProxyServer proxy;
    private final Logger logger;
    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final ServerManager serverManager;
    private final Path dataDirectory;

    @Inject
    public PlayerNotifyPlugin(ProxyServer proxy, @DataDirectory Path dataDirectory, Logger logger) throws IOException {
        instance = this;
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        // config
        this.configManager = new ConfigManager(this);
        this.serverManager = new ServerManager(this);
        this.playerManager = new PlayerManager();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) throws IOException, InvalidConfigurationException {
        getLogger().info("Enabling PlayerNotify...");
        configManager.load();
        serverManager.loadServerSettings();
        this.registerCommands();
        this.registerListeners();
        getLogger().info("PlayerNotify Enabled!");
    }

    public void registerCommands() {
        proxy.getCommandManager().register("pnreload", new PlayerNotifyCommand(this));
        proxy.getCommandManager().register("togglemessages", new ToggleMessagesCommand(this));
    }

    public void unregisterCommands(){
        proxy.getCommandManager().unregister("pnreload");
        proxy.getCommandManager().unregister("togglemessages");
    }

    public void registerListeners() {
        proxy.getEventManager().register(this, new OnPlayerJoin(this));
        proxy.getEventManager().register(this, new OnPlayerLeave(this));
        proxy.getEventManager().register(this, new OnPlayerSwitch(this));
    }

    public void unregisterListeners() {
        proxy.getEventManager().unregisterListeners(this);
    }

    public Path getDataDirectory() throws IOException {
        // make sure data directory exists
        if (Files.notExists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }
        return this.dataDirectory;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public InputStream getResource(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public static PlayerNotifyPlugin getInstance() {
        return instance;
    }

    public FileConfiguration getConfig(ConfigType configType) {
        return configManager.getConfig(configType).getConfig();
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public PlayerManager getPlayersManager() {
        return this.playerManager;
    }

    public boolean shouldNotify(Player player, String connectedServer, String disconnectedServer) {
        if (serverManager.isPrivate(connectedServer) || serverManager.isPrivate(disconnectedServer)) return false;
        // ignore the warning below
        if (getSettings().getBoolean("respect-vanish") && VelocityVanishAPI.isInvisible(player)) return false;
        if (getSettings().getBoolean("permission.notify_message")) {
            return player.hasPermission("playernotify.notify");
        }
        return true;
    }

    public void sendNotificationMessage(PluginMessage message, Player player, String connectedServer, String disconnectedServer) {
        if (shouldNotify(player, connectedServer, disconnectedServer)) {
            sendProxyMessage(message, player, connectedServer, disconnectedServer);
        }
    }

    private FileConfiguration getSettings() {
        return getConfig(ConfigType.SETTINGS);
    }

    // main command for sending messages to the entire network
    public void sendProxyMessage(PluginMessage message, Player player, String last_server, String server) {
        String player_username = player.getUsername(); // "%player%"
        String last_server_displayname = getServerDisplayname(last_server); // "%last_server%"
        String server_displayname = getServerDisplayname(server); // "%server%"

        String luckperms_prefix = "null"; // "%lp_prefix%"
        String luckperms_suffix = "null"; // "%lp_suffix%"

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); // "%time%"

        if (this.proxy.getPluginManager().getPlugin("luckperms").isPresent()) {
            try {
                CachedMetaData userData = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData();
                luckperms_prefix = userData.getPrefix();
                luckperms_suffix = userData.getSuffix();
            } catch (Exception ignored) {
                // ignore whatever exceptions in this placeholder
            }
        }

        // compile the message only once
        TextComponent message_compiled = message.getText(
                "%player%", player_username,
                "%last_server%", last_server_displayname,
                "%server%", server_displayname,
                "%lp_prefix%", luckperms_prefix,
                "%lp_suffix%", luckperms_suffix,
                "%time%", time
        );

        for (Player pl : proxy.getAllPlayers()) {
            // skip players that toggled messages off
            if (playerManager.isToggledOff(pl.getUniqueId())) continue;

            Optional<ServerConnection> player_current_server = pl.getCurrentServer();
            if (player_current_server.isPresent()) {
                String player_server_name = player_current_server.get().getServerInfo().getName();
                if (!serverManager.isDisabled(player_server_name)) {
                    pl.sendMessage(message_compiled);
                }
            } else {
                pl.sendMessage(message_compiled);
            }
        }
    }

    public String getServerDisplayname(String serverName) {
        return getServerManager().getServerDispalyname(serverName);
    }

    public void reload() {
        this.unregisterCommands();
        this.unregisterListeners();
        this.configManager.reloadFiles();
        this.serverManager.loadServerSettings();
        this.registerListeners();
        this.registerCommands();
    }
}
