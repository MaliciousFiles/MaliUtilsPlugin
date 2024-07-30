package io.github.maliciousfiles.maliUtils;

import io.github.maliciousfiles.maliUtils.invsee.InvseeCommand;
import io.github.maliciousfiles.maliUtils.invsee.InvseeHandler;
import io.github.maliciousfiles.maliUtils.vanish.ListVanishCommand;
import io.github.maliciousfiles.maliUtils.smite.SmiteCommand;
import io.github.maliciousfiles.maliUtils.vanish.VanishCommand;
import io.github.maliciousfiles.maliUtils.vanish.VanishHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MaliUtils extends JavaPlugin {

    /* TODO
        - prevent /msg to vanished
        - health link
        - swap players
        - beepers
        - invsee
        - smite
        - demon mobs
     */

    public static MaliUtils instance;

    private void registerCommand(String command, Object executor) {
        getCommand(command).setExecutor((CommandExecutor) executor);
        getCommand(command).setTabCompleter((TabCompleter) executor);
    }

    @Override
    public void onEnable() {
        instance = this;

        registerCommand("vanish", new VanishCommand());
        registerCommand("listvanish", new ListVanishCommand());
        registerCommand("smite", new SmiteCommand());
        registerCommand("invsee", new InvseeCommand());

        Bukkit.getPluginManager().registerEvents(new VanishHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InvseeHandler(), this);
    }

    @Override
    public void onDisable() {

    }
}
