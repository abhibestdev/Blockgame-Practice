package us.blockgame.practice.customkit;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.blockgame.practice.util.InvUtil;

import java.io.IOException;

public class CustomKit {

    private String inventory = "none";
    private String armor = "none";

    public CustomKit(String inventory, String armor) {
        this.inventory = inventory;
        this.armor = armor;
    }

    public String getRawInventory() {
        return inventory;
    }

    public String getRawArmor() {
        return armor;
    }

    public Inventory getInventory() throws IOException {
        return InvUtil.fromBase64(inventory);
    }

    public ItemStack[] getArmor() throws IOException {
        return InvUtil.itemStackArrayFromBase64(armor);
    }
}
