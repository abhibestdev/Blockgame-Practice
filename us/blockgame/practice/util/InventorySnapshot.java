package us.blockgame.practice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySnapshot {
    private Inventory inventory;

    private static HashMap<UUID, InventorySnapshot> inv = new HashMap<>();

    public InventorySnapshot(final Player player) {
        final ItemStack[] contents1 = player.getInventory().getContents();
        final ItemStack[] armor1 = player.getInventory().getArmorContents();
        List<ItemStack> contents = new ArrayList<>();
        List<ItemStack> armor = new ArrayList<>();
        for (int i = 0; i < contents1.length; i++) {
            ItemStack itemStack = contents1[i];
            contents.add(i, itemStack);
        }
        for (int i = 0; i < armor1.length; i++) {
            ItemStack itemStack = armor1[i];
            armor.add(i, itemStack);
        }

        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Inventory of " + player.getName());
        final int potCount = (int) Arrays.stream(contents1).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();
        final double health = ((Damageable) player).getHealth();
        for (int i = 0; i < 9; ++i) {
            if (contents.size() >= (i + 27) + 1) {
                this.inventory.setItem(i + 27, contents.get(i));
                this.inventory.setItem(i + 18, contents.get(i + 27));
                this.inventory.setItem(i + 9, contents.get(i + 18));
                this.inventory.setItem(i, contents.get(i + 9));
            }
        }
        this.inventory.setItem(48, ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.GREEN + "Hearts: " + Math.round(health / 2) + " / 10 \u2764", ((int) Math.round(health / 2.0) < 1 ? 1 : (int) Math.round(health / 2.0))));
        this.inventory.setItem(50, ItemUtil.createItem(Material.POTION, ChatColor.RED + "Pots: " + potCount + "\u2764", (potCount < 1 ? 1 : potCount), (short) 16421));
        for (int i = 0; i < 4; ++i) {
            if (contents.size() >= i) {
                this.inventory.setItem(39 - i, armor.get(i));
            }
            inv.put(player.getUniqueId(), this);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static InventorySnapshot getByPlayer(Player p) {
        return inv.get(p.getUniqueId());

    }
}
