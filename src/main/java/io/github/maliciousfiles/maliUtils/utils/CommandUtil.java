package io.github.maliciousfiles.maliUtils.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CommandUtil {
    public static void error(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message).color(NamedTextColor.RED));
    }

    public static void success(CommandSender sender, String message, Object... args) {
        Component component = Component.empty();

        String[] sections = message.split("\\{}");
        for (int i = 0; i < args.length+1; i++) {
            String section = i >= sections.length ? "" : sections[i];

            component = component.append(Component.text(section).color(NamedTextColor.DARK_AQUA));

            if (i != args.length) component = component.append(Component.text(args[i].toString()).color(NamedTextColor.GOLD));
        }

        sender.sendMessage(component);
    }

}
