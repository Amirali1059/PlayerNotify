package me.logicalglitch.fix.proxynotify.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;
import net.kyori.adventure.text.Component;

public class ToggleMessagesCommand implements SimpleCommand {
    PlayerNotifyPlugin plugin;

    public ToggleMessagesCommand(PlayerNotifyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (source instanceof Player) {
            Player player = (Player) source;
            if (plugin.getPlayersManager().toggleMessagesIsOn(player.getUniqueId())) {
                PluginMessage.COMMAND_TOGGLEMESSAGES_ON.send(player); // "Message notifications toggled on"
            } else {
                PluginMessage.COMMAND_TOGGLEMESSAGES_OFF.send(player); // "Message notifications toggled off"
            }
        } else {
            PluginMessage.COMMAND_IN_GAME_ONLY.send(source); //"Only players can use this command."
        }
    }
}