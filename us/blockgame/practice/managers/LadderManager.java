package us.blockgame.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.ladder.LadderType;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.util.InvUtil;
import us.blockgame.practice.util.ItemBuilder;
import us.blockgame.practice.util.Util;

import java.util.ArrayList;
import java.util.List;

public class LadderManager extends Manager {

    private ArrayList<Ladder> ladders;
    private List<String> rawLadders;

    public LadderManager(ManagerHandler managerHandler) {
        super(managerHandler);
        ladders = new ArrayList<>();
        rawLadders = managerHandler.getPlugin().getConfig().getStringList("ladders");
        load();
    }

    private void load() {
        for (String rawLadder : rawLadders) {
            try {
                String displayName = ChatColor.translateAlternateColorCodes('&', managerHandler.getPlugin().getConfig().getString("ladder." + rawLadder + ".displayname"));
                int item = managerHandler.getPlugin().getConfig().getInt("ladder." + rawLadder + ".item");
                int extension = managerHandler.getPlugin().getConfig().getInt("ladder." + rawLadder + ".short");
                boolean editable = managerHandler.getPlugin().getConfig().getBoolean("ladder." + rawLadder + ".editable");
                LadderType ladderType = LadderType.valueOf(managerHandler.getPlugin().getConfig().getString("ladder." + rawLadder + ".type"));
                ItemStack displayItem = new ItemBuilder(new ItemStack(Material.getMaterial(item), 1, (short) extension)).setName(displayName).buildItemStack();
                Inventory inventory = InvUtil.fromBase64(managerHandler.getPlugin().getConfig().getString("ladder." + rawLadder + ".inventory"));
                ItemStack[] armor = InvUtil.itemStackArrayFromBase64(managerHandler.getPlugin().getConfig().getString("ladder." + rawLadder + ".armor"));
                int id = Util.getFilledSlots(managerHandler.getInventoryManager().getUnrankedInventory());
                int editId = Util.getFilledSlots(managerHandler.getInventoryManager().getEditKitInventory());
                managerHandler.getInventoryManager().getEditKitInventory().setItem(editId, displayItem);
                managerHandler.getInventoryManager().getLeaderboardsInventory().setItem(id, displayItem);
                managerHandler.getInventoryManager().getUnrankedInventory().setItem(id, displayItem);
                managerHandler.getInventoryManager().getPartyFFAInventory().setItem(id, displayItem);
                managerHandler.getInventoryManager().getPartySplitInventory().setItem(id, displayItem);
                managerHandler.getInventoryManager().getDuelInventory().setItem(id, displayItem);
                Ladder ladder = new Ladder(rawLadder, ladderType, inventory, armor, displayItem, id, editId, editable);
                ladders.add(ladder);
                System.out.print("Loaded ladder " + rawLadder + ".");
            } catch (Exception ex) {
                System.out.print("Error loading ladder " + rawLadder + ".");
            }
        }
    }

    public ArrayList<Ladder> getLadders() {
        return ladders;
    }

    public boolean ladderExists(String name) {
        return managerHandler.getPlugin().getConfig().getStringList("ladders").contains(name);
    }

    public boolean ladderByIdExists(int id) {
        return getLadderById(id) != null;
    }

    public Ladder getLadderById(int id) {
        for (Ladder ladder : ladders) {
            if (ladder.getId() == id) {
                return ladder;
            }
        }
        return null;
    }

    public boolean ladderByEditIdExists(int editId) {
        return getLadderByEditId(editId) != null;
    }

    public Ladder getLadderByEditId(int editId) {
        for (Ladder ladder : ladders) {
            if (ladder.getEditId() == editId) {
                return ladder;
            }
        }
        return null;
    }

    public void createLadder(String name, String displayName) {
        rawLadders.add(name);
        managerHandler.getPlugin().getConfig().set("ladders", rawLadders);
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".displayname", displayName);
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".item", 276);
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".short", 0);
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".editable", false);
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".type", LadderType.NORMAL.toString());
        managerHandler.getPlugin().saveConfig();
    }

    public void deleteLadder(String name) {
        rawLadders.remove(name);
        managerHandler.getPlugin().getConfig().set("ladders", rawLadders);
        managerHandler.getPlugin().getConfig().set("ladder." + name, null);
        managerHandler.getPlugin().saveConfig();
    }

    public void setInventory(String name, Inventory inventory, ItemStack[] armor) {
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".inventory", InvUtil.toBase64(inventory));
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".armor", InvUtil.itemStackArrayToBase64(armor));
        managerHandler.getPlugin().saveConfig();
    }

    public void setDisplayItem(String name, ItemStack item) {
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".item", item.getType().getId());
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".short", item.getDurability());
        managerHandler.getPlugin().saveConfig();
    }

    public void setType(String name, LadderType ladderType) {
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".type", ladderType.toString());
        managerHandler.getPlugin().saveConfig();
    }

    public void setEditable(String name) {
        managerHandler.getPlugin().getConfig().set("ladder." + name + ".editable", true);
        managerHandler.getPlugin().saveConfig();
    }

}
