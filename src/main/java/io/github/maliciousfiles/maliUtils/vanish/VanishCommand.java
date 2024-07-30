package io.github.maliciousfiles.maliUtils.vanish;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        OfflinePlayer player;

        if (args.length > 0) player = Bukkit.getOfflinePlayer(args[0]);
        else if (sender instanceof Player p) player = p;
        else {
            CommandUtil.error(sender, "You must specify a player to vanish");
            return true;
        }

        CommandUtil.success(sender, "%s is now {}".formatted(player.getName()), VanishHandler.toggle(player.getUniqueId()) ? "vanished" : "visible");
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
