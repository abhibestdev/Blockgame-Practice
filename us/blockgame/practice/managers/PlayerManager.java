package us.blockgame.practice.managers;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.util.EnderpearlDelay;
import us.blockgame.practice.util.ItemBuilder;
import us.blockgame.practice.util.PlayerState;
import us.blockgame.practice.util.Util;

public class PlayerManager extends Manager {

    public PlayerManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public void teleportSpawn(Player player) {
        player.setKnockback(null);
        NametagEdit.getApi().clearNametag(player);
        if (managerHandler.getSettingsManager().getLocationsMap().containsKey("spawn")) {
            player.teleport(managerHandler.getSettingsManager().getLocationsMap().get("spawn"));
        }
        for (Player all : managerHandler.getPlugin().getServer().getOnlinePlayers()) {
            player.hidePlayer(all);
            all.hidePlayer(player);
        }
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (EnderpearlDelay.getByPlayer(player) != null) {
            EnderpearlDelay.getByPlayer(player).stop();
        }
    }

    public void teleportEditor(Player player) {
        if (managerHandler.getSettingsManager().getLocationsMap().containsKey("editor")) {
            player.teleport(managerHandler.getSettingsManager().getLocationsMap().get("editor"));
        }
    }

    public void teleportLocation(Player player, String location) {
        if (managerHandler.getSettingsManager().getLocationsMap().containsKey(location)) {
            player.teleport(managerHandler.getSettingsManager().getLocationsMap().get(location));
        }
    }

    public void giveItems(Player player, boolean rematch) {
        PlayerData playerData = managerHandler.getPlayerDataManager().getPlayerData(player);
        playerData.setPlayerState(PlayerState.LOBBY);
        ItemStack unrankedItem = new ItemBuilder(new ItemStack(Material.STONE_SWORD)).setName("&aJoin an Unranked Queue").buildItemStack();
        ItemStack rankedItem = new ItemBuilder(new ItemStack(Material.IRON_SWORD)).setName("&bJoin a Ranked Queue").buildItemStack();
        ItemStack premiumItem = new ItemBuilder(new ItemStack(Material.GOLD_SWORD)).setName("&6Join Premium Queue").buildItemStack();
        ItemStack partyItem = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).setName("&2Create a Party").buildItemStack();
        ItemStack leaderboardItem = new ItemBuilder(new ItemStack(Material.EMERALD)).setName("&5View Leaderboards").buildItemStack();
        ItemStack editKitItem = new ItemBuilder(new ItemStack(Material.BOOK)).setName("&eEdit Kits").buildItemStack();
        ItemStack settingsItem = new ItemBuilder(new ItemStack(Material.WATCH)).setName("&cSettings").buildItemStack();
        ItemStack rematchItem = new ItemBuilder(new ItemStack(Material.BLAZE_POWDER)).setName("&6Rematch Player").buildItemStack();
        ItemStack viewInventoryItem = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bView Opponent's Inventory").buildItemStack();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(0, unrankedItem);
        player.getInventory().setItem(1, rankedItem);
        //player.getInventory().setItem(2, premiumItem);
        player.getInventory().setItem(4, partyItem);
        player.getInventory().setItem(7, editKitItem);
        player.getInventory().setItem(8, leaderboardItem);
        if (rematch) {
            player.getInventory().setItem(2, rematchItem);
            //player.getInventory().setItem(5, viewInventoryItem);
        }
        player.setFireTicks(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getInventory().remove(rematchItem);
                player.getInventory().remove(viewInventoryItem);
                player.updateInventory();
            }
        }.runTaskLaterAsynchronously(this.managerHandler.getPlugin(), 600L);
        player.updateInventory();
        player.setFoodLevel(20);
        player.setHealth(20.0);
        playerData.setParty(null);
    }

    public void giveLeaveItem(Player player) {
        Util.resetPlayer(player);
        ItemStack leaveItem = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cLeave Queue").buildItemStack();
        player.getInventory().setItem(8, leaveItem);
        player.updateInventory();
    }

    public void giveKit(Player player, Ladder ladder) {
        PlayerData playerData = managerHandler.getPlayerDataManager().getPlayerData(player);
        Util.resetPlayer();
        if (playerData.getCustomKitMap().containsKey(ladder)) {
            ItemStack defaultKit = new ItemBuilder(new ItemStack(Material.ENCHANTED_BOOK)).setName("&bDefault Kit").buildItemStack();
            ItemStack customKit = new ItemBuilder(new ItemStack(Material.ENCHANTED_BOOK)).setName("&bCustom Kit").buildItemStack();
            player.getInventory().setItem(0, customKit);
            player.getInventory().setItem(8, defaultKit);
            player.updateInventory();
            return;
        }
        player.getInventory().setContents(ladder.getInventory().getContents());
        player.getInventory().setArmorContents(ladder.getArmor());
        player.updateInventory();
    }

    public void giveEventItem(Player player) {
        Util.resetPlayer(player);
        ItemStack leaveEvent = new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cLeave Event").buildItemStack();
        player.getInventory().setItem(4, leaveEvent);
        player.updateInventory();
    }

    public void givePartyItems(Player player, boolean leader) {
        Util.resetPlayer(player);
        ItemStack partyFFA = new ItemBuilder(new ItemStack(Material.GOLD_SWORD)).setName("&6Party FFA").buildItemStack();
        ItemStack partySplit = new ItemBuilder(new ItemStack(Material.STONE_SWORD)).setName("&3Party Split").buildItemStack();
        ItemStack partyInfo = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bParty Info").buildItemStack();
        ItemStack disbandParty = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cDisband Party").buildItemStack();
        ItemStack leaveParty = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cLeave Party").buildItemStack();
        if (leader) {
            player.getInventory().setItem(0, partyFFA);
            player.getInventory().setItem(1, partySplit);
            player.getInventory().setItem(4, partyInfo);
            player.getInventory().setItem(8, disbandParty);
        } else {
            player.getInventory().setItem(8, leaveParty);
        }
        player.updateInventory();
    }

    public void giveSpectatorItems(Player player) {
        Util.resetPlayer(player);
        ItemStack matchInfo = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bMatch Information").buildItemStack();
        ItemStack unspecItem = new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cStop Spectating").buildItemStack();
        player.getInventory().setItem(0, matchInfo);
        player.getInventory().setItem(8, unspecItem);
        player.updateInventory();
    }
}
