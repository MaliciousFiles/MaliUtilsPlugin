package io.github.maliciousfiles.maliUtils.vanish;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.github.maliciousfiles.maliUtils.MaliUtils;
import io.github.maliciousfiles.maliUtils.utils.ConfigObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishHandler implements Listener {

    private static final ConfigObject<List<String>> vanished = new ConfigObject<>("vanished", new ArrayList<>());

    @EventHandler
    public void onPing(PaperServerListPingEvent evt) {
        evt.getListedPlayers().removeIf(p -> isVanished(p.id()));
        evt.setNumPlayers(evt.getListedPlayers().size());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        if (isVanished(evt.getPlayer().getUniqueId())) {
            evt.joinMessage(Component.empty());
        }

        Bukkit.getScheduler().runTask(MaliUtils.instance, () -> {
            if (isVanished(evt.getPlayer().getUniqueId())) vanishAll(evt.getPlayer());

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p != evt.getPlayer() && isVanished(p.getUniqueId())) vanish(p, evt.getPlayer());
            });
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        if (isVanished(evt.getPlayer().getUniqueId())) {
            evt.quitMessage(Component.empty());
        }
    }

    private static boolean isVanished(UUID uuid) {
        return vanished.get().contains(uuid.toString());
    }

    private static void vanishAll(Player vanisher) {
        Bukkit.getOnlinePlayers().forEach(p -> { if (p != vanisher) vanish(vanisher, p); });
    }

    private static void unvanishAll(Player vanisher) {
        Bukkit.getOnlinePlayers().forEach(p -> { if (p != vanisher) unvanish(vanisher, p); });
    }

    private static void vanish(Player vanisher, Player viewer) {
        viewer.hidePlayer(MaliUtils.instance, vanisher);
    }

    private static void unvanish(Player vanisher, Player viewer) {
        viewer.showPlayer(MaliUtils.instance, vanisher);
        viewer.sendMessage(Component.text("%s joined the game".formatted(vanisher.getName())).color(NamedTextColor.YELLOW));
    }

    public static boolean toggle(UUID uuid) {
        Player player = Bukkit.getPlayerExact(uuid);
        String str = uuid.toString();

        boolean[] ret = new boolean[1];
        vanished.update(l -> {
            if (l.contains(str)) {
                if (player != null) unvanishAll(player);
                ret[0] = false;
                l.remove(str);
            } else {
                if (player != null) {
                    vanishAll(player);
                    Bukkit.broadcast(Component.text("%s left the game".formatted(player.getName())).color(NamedTextColor.YELLOW));
                }
                ret[0] = true;
                l.add(str);
            }
        });

        ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().updateSleepingPlayerList();
        return ret[0];
    }

    public static List<OfflinePlayer> getVanished() {
        return vanished.get().stream().map(s->Bukkit.getOfflinePlayer(UUID.fromString(s))).toList();
    }
}
