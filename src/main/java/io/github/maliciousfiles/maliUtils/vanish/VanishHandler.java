package io.github.maliciousfiles.maliUtils.vanish;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.github.maliciousfiles.maliUtils.MaliUtils;
import io.github.maliciousfiles.maliUtils.utils.ConfigObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.*;

public class VanishHandler implements Listener {

    private static final ConfigObject<List<String>> vanished = new ConfigObject<>("vanished", new ArrayList<>());

    @EventHandler
    public void onPing(PaperServerListPingEvent evt) {
        evt.getListedPlayers().removeIf(p -> isVanished(p.id()));
        evt.setNumPlayers(evt.getListedPlayers().size());
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent evt) {
        Bukkit.broadcastMessage(evt.getCompletions().toString());
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

    private static boolean isVanished(String uuid) {
        return vanished.get().contains(uuid);
    }

    private static void vanishAll(Player vanisher) {
        Bukkit.getOnlinePlayers().forEach(p -> { if (p != vanisher) vanish(vanisher, p); });
    }

    private static void unvanishAll(Player vanisher) {
        Bukkit.getOnlinePlayers().forEach(p -> { if (p != vanisher) unvanish(vanisher, p); });
    }

    private static void vanish(Player vanisher, Player viewer) {
        ((CraftPlayer) viewer).getHandle().connection.send(new ClientboundBundlePacket(List.of(
                new ClientboundPlayerInfoRemovePacket(List.of(vanisher.getUniqueId())),
                new ClientboundRemoveEntitiesPacket(vanisher.getEntityId())
        )));
    }

    private static void unvanish(Player vanisher, Player viewer) {
        ServerPlayer vanisherHandle = ((CraftPlayer) vanisher).getHandle();
        ServerPlayer viewerHandle = ((CraftPlayer) viewer).getHandle();

        viewerHandle.connection.send(new ClientboundBundlePacket(List.of(
                new ClientboundPlayerInfoUpdatePacket(EnumSet.copyOf(
                        Arrays.stream(ClientboundPlayerInfoUpdatePacket.Action.values()).toList()), List.of(vanisherHandle)),
                vanisherHandle.getAddEntityPacket(viewerHandle.moonrise$getTrackedEntity().serverEntity),
                new ClientboundSetEntityDataPacket(vanisherHandle.getId(), vanisherHandle.getEntityData().getNonDefaultValues()),
                new ClientboundSystemChatPacket(Component.text("%s joined the game".formatted(vanisher.getName())).color(NamedTextColor.YELLOW), false)
        )));
    }

    public static boolean toggle(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
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

        return ret[0];
    }
}
