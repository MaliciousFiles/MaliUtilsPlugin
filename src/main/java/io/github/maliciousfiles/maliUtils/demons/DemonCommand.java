package io.github.maliciousfiles.maliUtils.demons;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DemonCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandUtil.error(sender, "Only players can use this command");
            return true;
        }

        if (args.length == 0) {
            CommandUtil.error(sender, "Invalid entity");
            return true;
        }

        EntityType<?> type = args[0].equalsIgnoreCase("sheep") ? EntityType.SHEEP :
                args[0].equalsIgnoreCase("pig") ? EntityType.PIG :
                args[0].equalsIgnoreCase("cow") ? EntityType.COW :
                args[0].equalsIgnoreCase("chicken") ? EntityType.CHICKEN :
                args[0].equalsIgnoreCase("horse") ? EntityType.HORSE :
                args[0].equalsIgnoreCase("donkey") ? EntityType.DONKEY :
                args[0].equalsIgnoreCase("mule") ? EntityType.MULE : null;
        if (type == null) {
            CommandUtil.error(sender, "Invalid entity");
            return true;
        }

        ServerLevel level = ((CraftPlayer) player).getHandle().serverLevel();

        DemonAnimal demon = new DemonAnimal(level, type);
        demon.setPos(CraftLocation.toVec3D(player.getLocation()));
        level.addFreshEntity(demon);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options = List.of("sheep", "pig", "cow", "chicken", "horse", "donkey", "mule");
        }

        return options.stream().sorted().filter(option -> option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
