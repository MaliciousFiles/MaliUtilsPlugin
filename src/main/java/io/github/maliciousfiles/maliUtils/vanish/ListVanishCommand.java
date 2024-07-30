package io.github.maliciousfiles.maliUtils.vanish;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListVanishCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CommandUtil.success(sender, "Vanished players:");

        for (OfflinePlayer op : VanishHandler.getVanished()) {
            CommandUtil.success(sender, "  - {%s} {}".formatted(op.isOnline() ? "green" : "red"), "●", op.getName());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
