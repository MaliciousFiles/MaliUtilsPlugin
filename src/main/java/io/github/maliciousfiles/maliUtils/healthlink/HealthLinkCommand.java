package io.github.maliciousfiles.maliUtils.healthlink;

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

public class HealthLinkCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            CommandUtil.error(sender, "Invalid subcommand");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                CommandUtil.error(sender, "Invalid link name");
                return true;
            }

            if (HealthLinkHandler.createLink(args[1])) {
                CommandUtil.success(sender, "Created new link {}", args[1]);
            } else {
                CommandUtil.error(sender, "Link already exists");
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                CommandUtil.error(sender, "Invalid link name");
                return true;
            }

            if (HealthLinkHandler.deleteLink(args[1])) {
                CommandUtil.success(sender, "Deleted link {}", args[1]);
            } else {
                CommandUtil.error(sender, "Link does not exist");
            }
        } else if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                CommandUtil.error(sender, "Invalid link name");
                return true;
            }

            if (args.length < 3) {
                CommandUtil.error(sender, "Invalid player");
                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
            if (HealthLinkHandler.joinLink(player, args[1])) {
                CommandUtil.success(sender, "{} joined link {}", player.getName(), args[1]);
            } else {
                CommandUtil.error(sender, "Link does not exist");
            }
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (args.length < 2) {
                CommandUtil.error(sender, "Invalid player");
                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (HealthLinkHandler.leaveLink(player)) {
                CommandUtil.success(sender, "{} left all links", player.getName());
            } else {
                CommandUtil.error(sender, "Player is not in any links");
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                CommandUtil.error(sender, "Invalid link name");
                return true;
            }

            if (args.length < 3) {
                CommandUtil.error(sender, "Invalid health");
                return true;
            }

            double health;
            try {
                health = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                CommandUtil.error(sender, "Invalid health");
                return true;
            }

            if (health < 0.01 || health > 20) {
                CommandUtil.error(sender, "Invalid health");
                return true;
            }

            if (HealthLinkHandler.setHealth(args[1], health)) {
                CommandUtil.success(sender, "Set health of link {} to {}", args[1], health);
            } else {
                CommandUtil.error(sender, "Link does not exist");
            }
        } else {
            CommandUtil.error(sender, "Invalid subcommand");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options = List.of("create", "delete", "join", "leave", "set");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("set")) {
                options = HealthLinkHandler.getLinkNames();
            } else if (args[0].equalsIgnoreCase("leave")) {
                options = HealthLinkHandler.getAllLinkedPlayers().stream().map(OfflinePlayer::getName).toList();
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("join")) {
                options = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
        }

        return options.stream().sorted().filter(option -> option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
