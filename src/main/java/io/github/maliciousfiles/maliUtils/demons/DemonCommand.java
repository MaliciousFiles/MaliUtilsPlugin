package io.github.maliciousfiles.maliUtils.demons;

import io.github.maliciousfiles.maliUtils.utils.CommandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.sheep.Sheep;
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

        ServerLevel level = ((CraftPlayer) player).getHandle().level();

        net.minecraft.world.entity.animal.Animal entity =
                args[0].equalsIgnoreCase("sheep") ? new Sheep(EntityType.SHEEP, level) :
                args[0].equalsIgnoreCase("pig") ? new Pig(EntityType.PIG, level) :
                args[0].equalsIgnoreCase("cow") ? new Cow(EntityType.COW, level) :
                args[0].equalsIgnoreCase("chicken") ? new Chicken(EntityType.CHICKEN, level) :
                args[0].equalsIgnoreCase("horse") ? new Horse(EntityType.HORSE, level) :
                args[0].equalsIgnoreCase("donkey") ? new Donkey(EntityType.DONKEY, level) :
                args[0].equalsIgnoreCase("mule") ? new Mule(EntityType.MULE, level) : null;
        if (entity == null) {
            CommandUtil.error(sender, "Invalid entity");
            return true;
        }

        DemonAnimal demon = new DemonAnimal(level, entity);
        demon.setPos(CraftLocation.toVec3(player.getLocation()));
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
