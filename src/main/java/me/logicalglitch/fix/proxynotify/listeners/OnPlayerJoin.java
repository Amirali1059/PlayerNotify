package me.logicalglitch.fix.proxynotify.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;

import java.util.concurrent.TimeUnit;

public class OnPlayerJoin {
    private PlayerNotifyPlugin plugin;

    public OnPlayerJoin(PlayerNotifyPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onJoin(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            if (!player.isActive()) return;

            String server = player.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse(null);
            if (server == null) return;
            plugin.getPlayersManager().cachePlayerServer(player.getUniqueId(), server);
            plugin.sendNotificationMessage(PluginMessage.EVENT_JOIN, player, server, null);
        }).delay(1, TimeUnit.SECONDS).schedule();
    }
}