package us.blockgame.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.util.ItemBuilder;

public class InventoryManager extends Manager {

    public InventoryManager(ManagerHandler managerHandler) {
        super(managerHandler);
        load();
    }

    private Inventory unrankedInventory;
    private Inventory rankedInventory;
    private Inventory premiumInventory;
    private Inventory leaderboardsInventory;
    private Inventory editKitInventory;
    private Inventory settingsInventory;
    private Inventory anvilInventory;
    private Inventory partyFFAInventory;
    private Inventory partySplitInventory;
    private Inventory duelInventory;

    private void load() {
        unrankedInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Select an Unranked Queue");
        rankedInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Select a Ranked Queue");
        premiumInventory = managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.DARK_GREEN + "Join Premium Queue");
        leaderboardsInventory = managerHandler.getPlugin().getServer().createInventory(null, 36, ChatColor.DARK_GREEN + "Leaderboards | Top 10");
        editKitInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Select a Kit to edit");
        settingsInventory = managerHandler.getPlugin().getServer().createInventory(null, 18, ChatColor.DARK_GREEN + "Settings");
        anvilInventory = managerHandler.getPlugin().getServer().createInventory(null, 18, ChatColor.DARK_GREEN + "Options");
        partyFFAInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Party FFA");
        partySplitInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Party Split");
        duelInventory = managerHandler.getPlugin().getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Select a Ladder");
        loadAnvil();
    }

    private void loadAnvil() {
        ItemStack saveKit = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 10)).setName("&aSave Kit").buildItemStack();
        ItemStack deleteKit = new ItemBuilder(new ItemStack(Material.FIRE)).setName("&cDelete Kit").buildItemStack();
        anvilInventory.setItem(4, saveKit);
        anvilInventory.setItem(13, deleteKit);
    }

    public Inventory getUnrankedInventory() {
        return unrankedInventory;
    }

    public Inventory getRankedInventory() {
        return rankedInventory;
    }

    public Inventory getPremiumInventory() {
        return premiumInventory;
    }

    public Inventory getLeaderboardsInventory() {
        return leaderboardsInventory;
    }

    public Inventory getEditKitInventory() {
        return editKitInventory;
    }

    public Inventory getSettingsInventory() {
        return settingsInventory;
    }

    public Inventory getAnvilInventory() {
        return anvilInventory;
    }

    public Inventory getPartyFFAInventory() {
        return partyFFAInventory;
    }

    public Inventory getPartySplitInventory() {
        return partySplitInventory;
    }

    public Inventory getDuelInventory() {
        return duelInventory;
    }
}
