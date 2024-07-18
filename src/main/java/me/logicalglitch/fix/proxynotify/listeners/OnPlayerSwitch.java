package me.logicalglitch.fix.proxynotify.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;

public class OnPlayerSwitch {
    private PlayerNotifyPlugin plugin;

    public OnPlayerSwitch(PlayerNotifyPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onSwitch(ServerConnectedEvent event) {
        // if the server is no longer present, ignore
        if (!event.getPreviousServer().isPresent()) return;
        // get player
        Player player = event.getPlayer();
        String lastServer = event.getPreviousServer().get().getServerInfo().getName();
        String currentServer = event.getServer().getServerInfo().getName();
        // send global message
        plugin.sendNotificationMessage(PluginMessage.EVENT_SWITCH, player, lastServer, currentServer);
        plugin.getPlayersManager().cachePlayerServer(player.getUniqueId(), currentServer);
    }
}
