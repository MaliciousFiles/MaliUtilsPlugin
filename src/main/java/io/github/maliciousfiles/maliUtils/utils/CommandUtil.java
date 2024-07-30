package io.github.maliciousfiles.maliUtils.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtil {
    public static void error(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message).color(NamedTextColor.RED));
    }

    public static void success(CommandSender sender, String message, Object... args) {
        Component component = Component.empty();

        Matcher colors = Pattern.compile("\\{[a-z_]*}").matcher(message);
        String[] sections = message.split("\\{[a-z_]*}");
        for (int i = 0; i < args.length+1; i++) {
            String section = i >= sections.length ? "" : sections[i];

            component = component.append(Component.text(section).color(NamedTextColor.DARK_AQUA));

            TextColor color = NamedTextColor.GOLD;
            if (colors.find()) {
                String name = colors.group();
                name = name.substring(1, name.length()-1);

                if (!name.isEmpty()) color = NamedTextColor.NAMES.value(name);
            }
            if (i != args.length) component = component.append(Component.text(args[i].toString()).color(color));
        }

        sender.sendMessage(component);
    }

}
