package io.github.maliciousfiles.maliUtils.invsee;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InvseeHandler implements Listener {
    private static final ItemStack FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    static {
        ItemMeta meta = FILLER.getItemMeta();
        meta.displayName(Component.empty());
        FILLER.setItemMeta(meta);
    }

    private static final Map<Inventory, Player> inventories = new HashMap<>();

    public static void openInventory(Player opener, Player opened) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(opened.getName()+"'s Inventory"));

        ItemStack[] contents = new ItemStack[54];
        Arrays.fill(contents, FILLER);

        contents[0] = opened.getInventory().getHelmet();
        contents[1] = opened.getInventory().getChestplate();
        contents[2] = opened.getInventory().getLeggings();
        contents[3] = opened.getInventory().getBoots();
        contents[5] = opened.getInventory().getItemInMainHand();
        contents[6] = opened.getInventory().getItemInOffHand();

        for (int i = 18; i < 45; i++) {
            contents[i] = opened.getInventory().getItem(i-9);
        }
        for (int i = 45; i < 54; i++) {
            contents[i] = opened.getInventory().getItem(i-45);
        }

        inv.setContents(contents);
        opener.openInventory(inv);

        inventories.put(inv, opened);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        Player player = inventories.get(evt.getInventory());
        if (player == null) return;

        inventories.remove(evt.getInventory());
        if (!player.isOnline()) return;

        ItemStack[] contents = new ItemStack[41];

        for (int i = 0; i < 9; i++) {
            contents[i] = evt.getInventory().getItem(45+i);
        }
        for (int i = 9; i < 36; i++) {
            contents[i] = evt.getInventory().getItem(i+9);
        }
        for (int i = 3; i >= 0; i--) {
            contents[39-i] = evt.getInventory().getItem(i);
        }
        contents[40] = evt.getInventory().getItem(6);

        player.getInventory().setContents(contents);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (!inventories.containsKey(evt.getInventory())) return;

        if (evt.getRawSlot() == 5) {
            evt.setCancelled(true);
        }
        if (FILLER.equals(evt.getCurrentItem()) || FILLER.equals(evt.getCursor())) {
            evt.setCancelled(true);
        }
    }
}
