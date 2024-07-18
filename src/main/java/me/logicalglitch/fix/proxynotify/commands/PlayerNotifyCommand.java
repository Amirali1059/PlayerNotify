package me.logicalglitch.fix.proxynotify.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.logicalglitch.fix.proxynotify.PlayerNotifyPlugin;
import me.logicalglitch.fix.proxynotify.messages.PluginMessage;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.List;

public class PlayerNotifyCommand implements SimpleCommand {
    PlayerNotifyPlugin plugin;

    public PlayerNotifyCommand(PlayerNotifyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        plugin.reload();
        PluginMessage.CONFIG_RELOAD.send(invocation.source());
    }
}