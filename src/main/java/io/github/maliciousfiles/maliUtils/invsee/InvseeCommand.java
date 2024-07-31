package io.github.maliciousfiles.maliUtils.invsee;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InvseeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandUtil.error(sender, "Only players can use this command");
            return true;
        }

        if (args.length == 0) {
            CommandUtil.error(sender, "No player specified");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            CommandUtil.error(sender, "Player not found");
            return true;
        }

        InvseeHandler.openInventory(player, target);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return options.stream().sorted().filter(option -> option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
