package io.github.maliciousfiles.maliUtils.invsee;

import com.mojang.datafixers.util.Pair;
import io.github.maliciousfiles.maliUtils.MaliUtils;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InvseeHandler implements Listener {
    private static final ItemStack FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    static {
        ItemMeta meta = FILLER.getItemMeta();
        meta.displayName(Component.empty());
        FILLER.setItemMeta(meta);
    }

    private static final Map<Inventory, Player> inventories = new HashMap<>();

    private static final ContainerListener listener = new ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu menu, int slotId, net.minecraft.world.item.ItemStack stack) {
            HumanEntity player = menu.getBukkitView().getPlayer();
            List<Inventory> viewers = inventories.entrySet().stream()
                    .filter(e -> e.getValue().equals(player))
                    .map(Map.Entry::getKey).toList();
            if (viewers.isEmpty()) {
                Bukkit.getScheduler().runTask(MaliUtils.instance, () -> menu.removeSlotListener(this));
                return;
            }

            ItemStack item = CraftItemStack.asBukkitCopy(stack);

            if (menu instanceof InventoryMenu) {
                if (slotId == InventoryMenu.SHIELD_SLOT) {
                    viewers.forEach(inv -> inv.setItem(6, item));
                } else if (slotId >= InventoryMenu.ARMOR_SLOT_START && slotId < InventoryMenu.ARMOR_SLOT_END) {
                    viewers.forEach(inv -> inv.setItem(slotId-InventoryMenu.ARMOR_SLOT_START, item));
                } else if (slotId >= InventoryMenu.INV_SLOT_START && slotId < InventoryMenu.INV_SLOT_END) {
                    viewers.forEach(inv -> inv.setItem(slotId-InventoryMenu.INV_SLOT_START+18, item));
                } else if (slotId >= InventoryMenu.USE_ROW_SLOT_START && slotId < InventoryMenu.USE_ROW_SLOT_END) {
                    viewers.forEach(inv -> inv.setItem(slotId-InventoryMenu.USE_ROW_SLOT_START+45, item));
                }
            } else {
                int slot = slotId - menu.getBukkitView().getTopInventory().getSize();
                if (slot < 0) return;

                viewers.forEach(inv -> inv.setItem(slot+18, item));
            }
        }

        @Override
        public void dataChanged(AbstractContainerMenu menu, int property, int value) {}
    };

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

        ((CraftPlayer) opened).getHandle().inventoryMenu.addSlotListener(listener);
        ((CraftPlayer) opened).getHandle().containerMenu.addSlotListener(listener);

        opener.openInventory(inv);
        inventories.put(inv, opened);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent evt) {
        Bukkit.getScheduler().runTask(MaliUtils.instance, () -> {
            ((CraftPlayer) evt.getPlayer()).getHandle().containerMenu.addSlotListener(listener);
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        Player player = inventories.remove(evt.getInventory());
        if (player == null || !player.isOnline()) return;

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
