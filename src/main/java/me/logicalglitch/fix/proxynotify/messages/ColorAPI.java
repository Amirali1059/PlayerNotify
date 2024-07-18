package me.logicalglitch.fix.proxynotify.messages;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ColorAPI {
    public static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static TextComponent process(String s) {
        // translates legacy color codes
        return LEGACY.deserialize(s);
    }

    public static TextComponent process(List<String> strings) {
        TextComponent.Builder results = Component.text();
        for (String str: strings){
            results.append(process(str + "\n"));
        }

        return results.build();
    }

    public static String color(String string, Color color) {
        return Component.text(string).color(TextColor.color(color.getRGB())).toString();
    }

    public static TextColor getColor(String string) {
        return TextColor.fromHexString(string);
    }

    public static String stripColorFormatting(String string) {
        return string.replaceAll("<#[0-9A-F]{6}>|[&§][a-f0-9lnokm]|<[/]?[A-Z]{5,8}(:[0-9A-F]{6})?[0-9]*>", "");
    }
}