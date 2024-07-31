package io.github.maliciousfiles.maliUtils.healthlink;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.maliciousfiles.maliUtils.utils.ConfigObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class HealthLinkHandler implements Listener {

    private static final ConfigObject<List<Link>> links = new ConfigObject<>("healthlinks", new ArrayList<>());

    public static boolean createLink(String name) {
        Link link = new Link(name, new ArrayList<>(), 20, 0, 0, 0, 0, 20);

        if (getLinkNames().contains(name)) return false;

        links.update(m -> { m.add(link); });
        return true;
    }

    public static boolean deleteLink(String name) {
        boolean[] ret = new boolean[1];
        links.update(m -> { ret[0] = m.removeIf(l-> l.name.equals(name)); });

        return ret[0];
    }

    public static boolean leaveLink(OfflinePlayer player) {
        boolean[] ret = new boolean[1];
        links.update(m -> {
            for (Link link : m) ret[0] |= link.players.remove(player.getUniqueId());
        });

        return ret[0];
    }

    public static boolean joinLink(OfflinePlayer player, String name) {
        leaveLink(player);

        boolean[] ret = new boolean[1];
        links.update(m -> {
            m.stream().filter(l -> l.name.equals(name)).findFirst().ifPresent(l -> {
                l.players.add(player.getUniqueId());

                if (isValidPlayer(player)) syncToLink(player.getPlayer());

                ret[0] = true;
            });
        });

        return ret[0];
    }

    public static boolean setHealth(String name, double health) {
        boolean[] ret = new boolean[1];
        links.update(m -> {
            m.stream().filter(l -> l.name.equals(name)).findFirst().ifPresent(l -> {
                l.health = health;
                l.players.stream().map(Bukkit::getPlayer).forEach(p -> { if (isValidPlayer(p)) p.setHealth(health); });

                ret[0] = true;
            });
        });

        return ret[0];
    }

    private static boolean isValidPlayer(OfflinePlayer p) {
        return p != null && p.isOnline() && !p.getPlayer().isDead();
    }

    public static List<String> getLinkNames() {
        return new ArrayList<>(links.get().stream().map(link -> link.name).toList());
    }

    public static List<OfflinePlayer> getAllLinkedPlayers() {
        return links.get().stream().flatMap(link -> link.players.stream()).map(Bukkit::getOfflinePlayer).toList();
    }

    private static void setAbsorption(Player player, int duration, int amplifier) {
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        if (duration > 0) player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(duration, amplifier));
    }

    private static void syncToLink(Player player) {
        links.update(l -> {
            l.stream()
                    .filter(link -> link.players.contains(player.getUniqueId()))
                    .findFirst().ifPresent(link -> {
                        player.setHealth(link.health);
                        player.setFoodLevel(link.food);

                        int duration = link.absorptionDuration - (int) ((System.currentTimeMillis() - link.absorptionTimestamp) / 50);
                        if (duration <= 0) {
                            link.absorptionTimestamp = 0;
                            link.absorptionAmplifier = 0;
                            link.absorptionDuration = 0;
                            link.absorptionAmount = 0;
                        }
                        player.setAbsorptionAmount(link.absorptionAmount);
                        setAbsorption(player, duration, link.absorptionAmplifier);
                    });
        });

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        syncToLink(evt.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent evt) {
        links.update(l -> {
            l.stream().filter(link -> link.players.contains(evt.getPlayer().getUniqueId()))
                    .findFirst().ifPresent(link -> {
                        if (link.health == 0) {
                            link.health = 20;
                            link.food = 20;
                            link.absorptionTimestamp = 0;
                            link.absorptionDuration = 0;
                            link.absorptionAmplifier = 0;
                            link.absorptionAmount = 0;
                        } else {
                            syncToLink(evt.getPlayer());
                        }
                    });
        });
    }

    @EventHandler
    public void onAbsorption(EntityPotionEffectEvent evt) {
        if (evt.getCause() == EntityPotionEffectEvent.Cause.PLUGIN) return;

        if (evt.getEntity() instanceof Player player && evt.getModifiedType() == PotionEffectType.ABSORPTION && (evt.getAction() != EntityPotionEffectEvent.Action.CHANGED)) {
            links.update(l -> {
                l.stream()
                        .filter(link -> link.players.contains(player.getUniqueId()))
                        .findFirst().ifPresent(link -> {
                            link.absorptionTimestamp = evt.getNewEffect() == null ? 0 : System.currentTimeMillis();
                            link.absorptionDuration = evt.getNewEffect() == null ? 0 : evt.getNewEffect().getDuration();
                            link.absorptionAmplifier = evt.getNewEffect() == null ? 0 : evt.getNewEffect().getAmplifier();

                            link.players.stream().map(Bukkit::getPlayer).forEach(p -> { if (isValidPlayer(p)) setAbsorption(p, link.absorptionDuration, link.absorptionAmplifier); });
                            link.absorptionAmount = player.getAbsorptionAmount();
                        });
            });
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player player) {
            links.update(l -> {
                l.stream()
                        .filter(link -> link.players.contains(player.getUniqueId()))
                        .findFirst().ifPresent(link -> {
                            link.food = Math.clamp(evt.getFoodLevel(), 0, 20);
                            link.players.stream().map(Bukkit::getPlayer).forEach(p -> { if (isValidPlayer(p) && p != player) p.setFoodLevel(link.food); });
                        });
            });
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

        if (evt.getEntity() instanceof Player player) {
            links.update(m -> {
                m.stream()
                        .filter(link -> link.players.contains(player.getUniqueId()))
                        .findFirst().ifPresent(link -> {
                            double absDamage = evt.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION);

                            link.absorptionAmount += absDamage;
                            link.health = Math.clamp(link.health - evt.getFinalDamage(), 0, 20);

                            link.players.stream().map(Bukkit::getPlayer).forEach(p -> { if (isValidPlayer(p) && p != player) p.damage(evt.getFinalDamage()-absDamage); });
                        });
            });
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent evt) {
        if (evt.getRegainReason() == EntityRegainHealthEvent.RegainReason.CUSTOM) return;

        if (evt.getEntity() instanceof Player player) {
            links.update(m -> {
                m.stream()
                        .filter(link -> link.players.contains(player.getUniqueId()))
                        .findFirst().ifPresent(link -> {
                            link.health = Math.clamp(link.health + evt.getAmount(), 0, 20);
                            link.players.stream().map(Bukkit::getPlayer).forEach(p -> { if (isValidPlayer(p) && p != player) p.heal(evt.getAmount(), EntityRegainHealthEvent.RegainReason.CUSTOM); });
                        });
            });
        }
    }

    public static class Link implements ConfigurationSerializable {
        private final String name;
        private final List<UUID> players;
        private double health;
        private long absorptionTimestamp;
        private int absorptionDuration;
        private int absorptionAmplifier;
        private double absorptionAmount;
        private int food;

        public Link(String name, List<UUID> players, double health, long absorptionTimestamp, int absorptionDuration, int absorptionAmplifier, double absorptionAmount, int food) {
            this.name = name;
            this.players = players;
            this.health = health;
            this.absorptionTimestamp = absorptionTimestamp;
            this.absorptionDuration = absorptionDuration;
            this.absorptionAmplifier = absorptionAmplifier;
            this.absorptionAmount = absorptionAmount;
            this.food = food;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("players", players.stream().map(UUID::toString).toList());
            map.put("health", health);
            map.put("absorptionTimestamp", absorptionTimestamp);
            map.put("absorptionDuration", absorptionDuration);
            map.put("absorptionAmplifier", absorptionAmplifier);
            map.put("absorptionAmount", absorptionAmount);
            map.put("food", food);
            return map;
        }

        public static Link deserialize(Map<String, Object> map) {
            return new Link(
                (String) map.get("name"),
                new ArrayList<>(((List<String>) map.get("players")).stream().map(UUID::fromString).toList()),
                (double) map.get("health"),
                Long.parseLong(map.get("absorptionTimestamp").toString()),
                (int) map.get("absorptionDuration"),
                (int) map.get("absorptionAmplifier"),
                (double) map.get("absorptionAmount"),
                (int) map.get("food")
            );
        }
    }
}
