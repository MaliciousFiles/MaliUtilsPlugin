package io.github.maliciousfiles.maliUtils.swap;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SwapCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            CommandUtil.error(sender, "No player specified");
            return true;
        }

        Player target1 = Bukkit.getPlayer(args[0]);
        Player target2 = Bukkit.getPlayer(args[1]);
        if (target1 == null || target2 == null ) {
            CommandUtil.error(sender, "Player not found");
            return true;
        }

        Location loc = target1.getLocation();
        target1.teleport(target2.getLocation());
        target2.teleport(loc);

        CommandUtil.success(sender, "Swapped {} and {}", target1.getName(), target2.getName());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1 || args.length == 2) {
            options.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return options.stream().sorted().filter(option -> option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
