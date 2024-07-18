package me.logicalglitch.fix.proxynotify.messages;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum PluginMessage {

    PREFIX("GENERAL.PREFIX"),
    CONFIG_RELOAD("GENERAL.CONFIG_RELOAD"),
    NO_PERMISSION("GENERAL.NO_PERMISSION"),
    COMMAND_NO_PERMISSION("COMMAND.NO_PERMISSION"),
    COMMAND_NOT_FOUND("COMMAND.NOT_FOUND"),
    COMMAND_TOGGLEMESSAGES_ON("COMMAND.TOGGLEMESSAGES_ON"),
    COMMAND_TOGGLEMESSAGES_OFF("COMMAND.TOGGLEMESSAGES_OFF"),
    COMMAND_IN_GAME_ONLY("COMMAND.IN_GAME_ONLY"),

    HELP_MESSGAGE("HELP_MESSGAGE"),

    EVENT_JOIN("EVENT.JOIN"),
    EVENT_SWITCH("EVENT.SWITCH"),
    EVENT_LEAVE("EVENT.LEAVE");

    private static FileConfiguration config;
    private final String path;

    PluginMessage(String path) {
        this.path = path;
    }

    public static void setConfiguration(FileConfiguration c) {
        config = c;
    }

    public @NotNull TextComponent getText(Object... replacements) {
        Object value = config.get("Messages." + this.path);
        TextComponent message = Component.text("");
        if (value == null) {
            value = "%prefix% message not found (" + this.path + ")";
        }

        if (value instanceof List) {
            message = ColorAPI.process(replace((List<String>) value, replacements));
        } else if (value instanceof String) {
            message = ColorAPI.process(replace((String) value, replacements));
        } else {
            message = Component.text("%prefix%: error loading message (" + this.path + ")");
        }

        return message;
    }

    public void send(CommandSource receiver, Object... replacements) {
        receiver.sendMessage(getText(replacements));
    }

    public void send(Player receiver, Object... replacements) {
        receiver.sendMessage(getText(replacements));
    }

    private String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            message = message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        String prefix = config.getString("Messages." + PREFIX.getPath());
        return message.replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "");
    }

    private List<String> replace(List<String> messages, Object... replacements) {
        List<String> result = new ArrayList<>();
        for (String message: messages) {
            result.add(replace(message, replacements));
        }
        return result;
    }

    public String getPath() {
        return this.path;
    }

}
