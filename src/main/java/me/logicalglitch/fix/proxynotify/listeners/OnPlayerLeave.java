package me.logicalglitch.fix.proxynotify.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;

public class OnPlayerLeave {
    private PlayerNotifyPlugin plugin;

    public OnPlayerLeave(PlayerNotifyPlugin plugin) {
        this.plugin = plugin;
    }
    @Subscribe(order = PostOrder.LAST)
    public void onLeave(DisconnectEvent event) {
        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.CANCELLED_BY_PROXY &&
                event.getLoginStatus() != DisconnectEvent.LoginStatus.CANCELLED_BY_USER &&
                event.getLoginStatus() != DisconnectEvent.LoginStatus.CONFLICTING_LOGIN &&
                event.getLoginStatus() != DisconnectEvent.LoginStatus.CANCELLED_BY_USER_BEFORE_COMPLETE) {
            Player player = event.getPlayer();
            String lastServer = plugin.getPlayersManager().popPlayer(player.getUniqueId());
            plugin.sendNotificationMessage(PluginMessage.EVENT_LEAVE, player, lastServer, null);
        }
    }
}
