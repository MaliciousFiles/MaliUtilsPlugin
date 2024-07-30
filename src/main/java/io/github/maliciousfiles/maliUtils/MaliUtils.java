package io.github.maliciousfiles.maliUtils;

import io.github.maliciousfiles.maliUtils.vanish.VanishCommand;
import io.github.maliciousfiles.maliUtils.vanish.VanishHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class MaliUtils extends JavaPlugin {

    /* TODO
        - vanish
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

        Bukkit.getPluginManager().registerEvents(new VanishHandler(), this);
    }

    @Override
    public void onDisable() {

    }
}
