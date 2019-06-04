package us.blockgame.practice.listeners;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;
import us.blockgame.practice.arena.ArenaType;
import us.blockgame.practice.customkit.CustomKit;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.ladder.LadderType;
import us.blockgame.practice.match.*;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.scoreboard.ScoreHelper;
import us.blockgame.practice.util.*;

import java.util.Random;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final Practice plugin;

    public PlayerListener(Practice plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getManagerHandler().getPlayerDataManager().hasPlayerData(player)) {
            this.plugin.getManagerHandler().getPlayerDataManager().addPlayer(player);
            System.out.print("Added data for " + player.getName() + ".");
        }
        if (!this.plugin.getManagerHandler().getSettingsManager().getPlayers().contains(player.getUniqueId().toString())) {
            this.plugin.getManagerHandler().getSettingsManager().getPlayers().add(player.getUniqueId().toString());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
        this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        playerData.setUpdate(true);
        new ScoreHelper(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        ItemStack unrankedItem = new ItemBuilder(new ItemStack(Material.STONE_SWORD)).setName("&aJoin an Unranked Queue").buildItemStack();
        ItemStack rankedItem = new ItemBuilder(new ItemStack(Material.IRON_SWORD)).setName("&bJoin a Ranked Queue").buildItemStack();
        ItemStack premiumItem = new ItemBuilder(new ItemStack(Material.GOLD_SWORD)).setName("&6Join Premium Queue").buildItemStack();
        ItemStack partyItem = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).setName("&2Create a Party").buildItemStack();
        ItemStack leaderboardItem = new ItemBuilder(new ItemStack(Material.EMERALD)).setName("&5View Leaderboards").buildItemStack();
        ItemStack editKitItem = new ItemBuilder(new ItemStack(Material.BOOK)).setName("&eEdit Kits").buildItemStack();
        ItemStack settingsItem = new ItemBuilder(new ItemStack(Material.WATCH)).setName("&cSettings").buildItemStack();
        ItemStack rematchItem = new ItemBuilder(new ItemStack(Material.BLAZE_POWDER)).setName("&6Rematch Player").buildItemStack();
        ItemStack viewInventoryItem = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bView Opponent's Inventory").buildItemStack();
        ItemStack leaveItem = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cLeave Queue").buildItemStack();
        ItemStack defaultKit = new ItemBuilder(new ItemStack(Material.ENCHANTED_BOOK)).setName("&bDefault Kit").buildItemStack();
        ItemStack customKit = new ItemBuilder(new ItemStack(Material.ENCHANTED_BOOK)).setName("&bCustom Kit").buildItemStack();
        ItemStack partyFFA = new ItemBuilder(new ItemStack(Material.GOLD_SWORD)).setName("&6Party FFA").buildItemStack();
        ItemStack partySplit = new ItemBuilder(new ItemStack(Material.STONE_SWORD)).setName("&3Party Split").buildItemStack();
        ItemStack partyInfo = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bParty Info").buildItemStack();
        ItemStack disbandParty = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cDisband Party").buildItemStack();
        ItemStack matchInfo = new ItemBuilder(new ItemStack(Material.PAPER)).setName("&bMatch Information").buildItemStack();
        ItemStack unspecItem = new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cStop Spectating").buildItemStack();
        ItemStack leaveParty = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 1)).setName("&cLeave Party").buildItemStack();
        ItemStack leaveEvent = new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cLeave Event").buildItemStack();
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.SOIL) {
                event.setCancelled(true);
            }
        }
         if (playerData.getPlayerState() == PlayerState.EDITING && (event.getClickedBlock().getType() == Material.ANVIL)) {
            event.setCancelled(true);
            player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getAnvilInventory());
        } else if (playerData.getPlayerState() == PlayerState.EDITING && (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)) {
            Util.resetPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
                    plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
                }
            }.runTaskLater(this.plugin, 60L);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getItemInHand().equals(leaveEvent)) {
                player.chat("/event leave");
            } else if (player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                double health = ((Damageable) player).getHealth();
                double foodLevel = player.getFoodLevel();
                if (health == player.getMaxHealth()) {
                    event.setCancelled(true);
                } else if (health + 7 >= 20) {
                    player.setHealth(20);
                } else {
                    player.setHealth(health + 7);
                }
                if (foodLevel + 7 >= 20) {
                    player.setFoodLevel(20);
                } else {
                    player.setFoodLevel((int) (foodLevel + 7));
                }
                player.getItemInHand().setType(Material.BOWL);
                player.updateInventory();
            } else if (player.getItemInHand().equals(leaveItem)) {
                Ladder ladder = playerData.getQueuedLadder();
                if (ladder.getUnrankedQueue().contains(player.getUniqueId())) {
                    ladder.getUnrankedQueue().remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GOLD + "[Unranked]" + ChatColor.YELLOW + " You left the queue for unranked " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");

                }
                if (ladder.getRankedQueue().contains(player.getUniqueId())) {
                    ladder.getRankedQueue().remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GOLD + "[Ranked]" + ChatColor.YELLOW + " You left the queue for ranked " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");

                }
                this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
            } else if (player.getItemInHand().equals(unrankedItem)) {
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
            } else if (player.getItemInHand().equals(matchInfo)) {
                Match match = playerData.getSpectating();
                player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------------------");
                player.sendMessage(ChatColor.GOLD + "Player One: " + ChatColor.YELLOW + match.getPlayerOne().getName());
                player.sendMessage(ChatColor.GOLD + "Player Two: " + ChatColor.YELLOW + match.getPlayerTwo().getName());
                player.sendMessage(ChatColor.GOLD + "Ladder: " + ChatColor.YELLOW + ChatColor.stripColor(match.getLadder().getDisplayItem().getItemMeta().getDisplayName()));
                player.sendMessage(ChatColor.GOLD + "Ranked: " + ChatColor.YELLOW + match.isRanked());
                player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------------------");
            } else if (player.getItemInHand().equals(unspecItem)) {
                event.setCancelled(true);
                Match match = playerData.getSpectating();
                match.getSpectators().remove(player.getUniqueId());
                playerData.setSpectating(null);
                this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
                this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
            } else if (player.getItemInHand().equals(leaveParty)) {
                player.chat("/party leave");
            } else if (player.getItemInHand().equals(rankedItem)) {
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory());
            } else if (player.getItemInHand().equals(premiumItem)) {
                player.sendMessage(ChatColor.RED + "This feature is coming soon.");
            } else if (player.getItemInHand().equals(partyItem)) {
                event.setCancelled(true);
                player.chat("/party create");
            } else if (player.getItemInHand().equals(leaderboardItem)) {
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getLeaderboardsInventory());
            } else if (player.getItemInHand().equals(editKitItem)) {
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitInventory());
            } else if (player.getItemInHand().equals(settingsItem)) {
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getSettingsInventory());
            } else if (player.getItemInHand().equals(rematchItem)) {
                player.chat("/duel " + playerData.getLastOpponent());
            } else if (player.getItemInHand().equals(viewInventoryItem)) {
                player.chat("/inventory " + playerData.getLastOpponent());
            } else if (player.getItemInHand().equals(partyInfo)) {
                player.chat("/party info");
            } else if (player.getItemInHand().equals(disbandParty)) {
                player.chat("/party disband");
            } else if (player.getItemInHand().equals(partyFFA)) {
                Party party = playerData.getParty();
                if (party.getLeader() != player.getUniqueId()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not the party leader!");
                    return;
                }
                if (party.getMembers().size() < 2) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You must have at least 2 people in the party to do this.");
                    return;
                }
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartyFFAInventory());
            } else if (player.getItemInHand().equals(partySplit)) {
                Party party = playerData.getParty();
                if (party.getLeader() != player.getUniqueId()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not the party leader!");
                    return;
                }
                if (party.getMembers().size() < 2) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You must have at least 2 people in the party to do this.");
                    return;
                }
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartySplitInventory());
            } else if (player.getItemInHand().equals(customKit)) {
                Util.resetPlayer(player);
                Match match = playerData.getMatch();
                try {
                    player.getInventory().setContents(playerData.getCustomKitMap().get(match.getLadder()).getInventory().getContents());
                    player.getInventory().setArmorContents(playerData.getCustomKitMap().get(match.getLadder()).getArmor());
                    player.updateInventory();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (player.getItemInHand().equals(defaultKit)) {
                Util.resetPlayer(player);
                Match match = playerData.getMatch();
                try {
                    player.getInventory().setContents(match.getLadder().getInventory().getContents());
                    player.getInventory().setArmorContents(match.getLadder().getArmor());
                    player.updateInventory();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
                if (playerData.getPlayerState() == PlayerState.PARTY) {
                    Party party = playerData.getParty();
                    if (party.getPartyState() == PartyState.GAME) {
                        PartyMatch partyMatch = party.getPartyMatch();
                        if (partyMatch.getMatchState() == MatchState.STARTING) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if (playerData.getPlayerState() == PlayerState.GAME) {
                    Match match = playerData.getMatch();
                    if (match.getMatchState() == MatchState.STARTING) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (EnderpearlDelay.getByPlayer(player) != null) {
                    player.sendMessage(ChatColor.RED + "You must wait " + EnderpearlDelay.getByPlayer(player).getTime() + "s to do this again.");
                    event.setCancelled(true);
                } else {
                    new EnderpearlDelay(this.plugin, player, 16).start();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (EnderpearlDelay.getByPlayer(player) != null) {
                                EnderpearlDelay.getByPlayer(player).stop();
                                player.sendMessage(ChatColor.GREEN + "You may enderpearl again!");
                            }
                        }
                    }.runTaskLaterAsynchronously(this.plugin, 320L);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getPartySplitInventory())) {
            if (this.plugin.getManagerHandler().getLadderManager().ladderByIdExists(slot)) {
                event.setCancelled(true);
                player.closeInventory();
                Party party = playerData.getParty();
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderById(slot);
                PartyMatch partyMatch = new PartyMatch(this.plugin, party, PartyMatchType.SPLIT, ladder, ArenaType.valueOf(ladder.getLadderType().toString()));
                partyMatch.start();
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getPartyFFAInventory())) {
            if (this.plugin.getManagerHandler().getLadderManager().ladderByIdExists(slot)) {
                event.setCancelled(true);
                player.closeInventory();
                Party party = playerData.getParty();
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderById(slot);
                PartyMatch partyMatch = new PartyMatch(this.plugin, party, PartyMatchType.FFA, ladder, ArenaType.valueOf(ladder.getLadderType().toString()));
                partyMatch.start();
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getEditKitInventory())) {
            if (this.plugin.getManagerHandler().getLadderManager().ladderByEditIdExists(slot)) {
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderByEditId(slot);
                if (!ladder.isEditable()) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Unfortunately, you can not edit that ladder at this time.");
                    return;
                }
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "[Editor] " + ChatColor.YELLOW + "Editing default kit for " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
                Util.resetPlayer(player);
                player.getInventory().setContents(ladder.getInventory().getContents());
                player.getInventory().setArmorContents(ladder.getArmor());
                player.updateInventory();
                playerData.setPlayerState(PlayerState.EDITING);
                playerData.setEditing(ladder);
                this.plugin.getManagerHandler().getPlayerManager().teleportEditor(player);
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getDuelInventory())) {
            Player target = this.plugin.getServer().getPlayer(playerData.getDueled());
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player has logged out.");
                return;
            }
            if (this.plugin.getManagerHandler().getLadderManager().ladderByIdExists(slot)) {
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderByEditId(slot);
                player.sendMessage(ChatColor.GOLD + "[Duel] " + ChatColor.YELLOW + "You sent player " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " a duel request.");
                TextComponent duelMessage = new TextComponent(ChatColor.GOLD + "[Duel] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has sent you a duel request in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()));
                TextComponent acceptMessage = new TextComponent(ChatColor.GREEN + "[Accept]");
                acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept.").create()));
                acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + player.getName()));
                duelMessage.addExtra(" ");
                duelMessage.addExtra(acceptMessage);
                target.spigot().sendMessage(duelMessage);
                event.setCancelled(true);
                player.closeInventory();
                Duel duel = new Duel(player.getUniqueId(), ladder);
                PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
                targetData.getDuels().add(duel);
                new BukkitRunnable() {
                    public void run() {
                        if (targetData.getDuels().contains(duel)) {
                            targetData.getDuels().remove(duel);
                        }
                    }
                }.runTaskLaterAsynchronously(this.plugin, 600L);
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getAnvilInventory())) {
            ItemStack saveKit = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 10)).setName("&aSave Kit").buildItemStack();
            ItemStack deleteKit = new ItemBuilder(new ItemStack(Material.FIRE)).setName("&cDelete Kit").buildItemStack();
            Ladder ladder = playerData.getEditing();
            if (event.getCurrentItem() != null && event.getCurrentItem().equals(saveKit)) {
                event.setCancelled(true);
                CustomKit customKit = new CustomKit(InvUtil.toBase64(player.getInventory()), InvUtil.itemStackArrayToBase64(player.getInventory().getArmorContents()));
                playerData.getCustomKitMap().put(ladder, customKit);
                player.sendMessage(ChatColor.GOLD + "[Editor] " + ChatColor.YELLOW + "Saved custom kit for " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
            } else if (event.getCurrentItem() != null && event.getCurrentItem().equals(deleteKit)) {
                event.setCancelled(true);
                playerData.getCustomKitMap().remove(ladder);
                player.sendMessage(ChatColor.GOLD + "[Editor] " + ChatColor.YELLOW + "Deleted custom kit for " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory())) {
            if (this.plugin.getManagerHandler().getLadderManager().ladderByIdExists(slot)) {
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderById(slot);
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "[Unranked] " + ChatColor.YELLOW + "You have joined the queue for " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ", please wait while we find you an opponent.");
                this.plugin.getManagerHandler().getPlayerManager().giveLeaveItem(player);
                playerData.setQueuedLadder(ladder);
                playerData.setQueuedRanked(false);
                playerData.setPlayerState(PlayerState.QUEUE);
                if (ladder.getUnrankedQueue().size() > 1) {
                    player.sendMessage(ChatColor.RED + "There was an error joining the queue.");
                } else if (ladder.getUnrankedQueue().size() == 1) {
                    for (UUID uuid : ladder.getUnrankedQueue()) {
                        if (uuid != player.getUniqueId()) {
                            Player opponent = this.plugin.getServer().getPlayer(uuid);
                            Match match = new Match(this.plugin, player, opponent, ladder, ArenaType.valueOf(ladder.getLadderType().toString()), false);
                            match.start();
                            ladder.getUnrankedQueue().remove(player.getUniqueId());
                            ladder.getUnrankedQueue().remove(opponent.getUniqueId());
                            return;
                        }
                    }
                }
                ladder.getUnrankedQueue().add(player.getUniqueId());
                return;
            }
        }
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory())) {
            if (this.plugin.getManagerHandler().getLadderManager().ladderByIdExists(slot)) {
                Ladder ladder = this.plugin.getManagerHandler().getLadderManager().getLadderById(slot);
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "[Ranked] " + ChatColor.YELLOW + "You have joined the queue for " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + " with " + ChatColor.GOLD + playerData.getElo(ladder) + ChatColor.YELLOW + " elo, please wait while we find you an opponent.");
                this.plugin.getManagerHandler().getPlayerManager().giveLeaveItem(player);
                ladder.getRankedQueue().add(player.getUniqueId());
                playerData.setQueuedLadder(ladder);
                playerData.setQueuedRanked(true);
                playerData.setPlayerState(PlayerState.QUEUE);
                new BukkitRunnable() {
                    int elo = playerData.getElo(ladder);
                    int minElo = elo - 20;
                    int maxElo = elo + 20;

                    @Override
                    public void run() {
                        if (!ladder.getRankedQueue().contains(player.getUniqueId())) {
                            this.cancel();
                        } else {
                            if (minElo - 20 <= 0) {
                                minElo = 0;
                            } else {
                                minElo -= 20;
                            }
                            playerData.setMaxElo(maxElo);
                            playerData.setMinElo(minElo);
                            maxElo += 20;
                            for (int i = 0; i < ladder.getRankedQueue().size(); i++) {
                                UUID uuid = ladder.getRankedQueue().get(i);
                                if (uuid != player.getUniqueId()) {
                                    Player opponent = plugin.getServer().getPlayer(uuid);
                                    PlayerData opponentData = plugin.getManagerHandler().getPlayerDataManager().getPlayerData(opponent);
                                    int opponentElo = opponentData.getElo(ladder);
                                    int opponentMinElo = opponentData.getMinElo();
                                    int opponentMaxElo = opponentData.getMaxElo();
                                    if (minElo <= opponentElo && maxElo >= opponentElo && opponentMinElo <= elo && opponentMaxElo >= elo) {
                                        Match match = new Match(plugin, player, opponent, ladder, ArenaType.valueOf(ladder.getLadderType().toString()), true);
                                        match.start();
                                        ladder.getRankedQueue().remove(opponent.getUniqueId());
                                        ladder.getRankedQueue().remove(player.getUniqueId());
                                    }
                                }
                            }
                        }
                    }
                }.runTaskTimer(this.plugin, 0L, 20L);
            }
        }
        if (playerData.getPlayerState() == PlayerState.PARTY) {
            Party party = playerData.getParty();
            if (party.getPartyState() != PartyState.GAME) {
                event.setCancelled(true);
            }
        } else if (!playerData.hasBuildMode() && playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.EDITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (!playerData.hasBuildMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (!playerData.hasBuildMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.PARTY) {
            event.setCancelled(true);
            return;
        }
        for (Player all : this.plugin.getServer().getOnlinePlayers()) {
            if (!all.canSee(player)) {
                this.plugin.getEntityHider().hideEntity(all, event.getItemDrop());
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getItemDrop().remove();
            }
        }.runTaskLater(this.plugin, 100L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.PARTY) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() == PlayerState.PARTY) {
            Party party = playerData.getParty();
            if (party.getPartyState() == PartyState.GAME) {
                PartyMatch partyMatch = party.getPartyMatch();
                partyMatch.addDeath(player, MatchEnd.DEATH);
            }
        }
        if (playerData.getPlayerState() == PlayerState.GAME) {
            Match match = playerData.getMatch();
            if (match.getMatchState() != MatchState.ENDING) {
                match.stop(player, MatchEnd.DISCONNECT);
            }
        }
        for (Ladder ladder : this.plugin.getManagerHandler().getLadderManager().getLadders()) {
            if (ladder.getUnrankedQueue().contains(player.getUniqueId())) {
                ladder.getUnrankedQueue().remove(player.getUniqueId());
            }
            if (ladder.getRankedQueue().contains(player.getUniqueId())) {
                ladder.getRankedQueue().remove(player.getUniqueId());
            }
        }
        if (playerData.getPlayerState() == PlayerState.EVENT) {
            if (this.plugin.getManagerHandler().getEventManager().getCurrentEvent().hasStarted()) {
                this.plugin.getManagerHandler().getEventManager().getCurrentEvent().removePlayer(player);
            }
        }
        player.chat("/party disband");
        player.chat("/party leave");
        player.chat("/event leave");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        event.getDrops().clear();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() == PlayerState.PARTY) {
            Party party = playerData.getParty();
            if (party.getPartyState() == PartyState.GAME) {
                PartyMatch partyMatch = party.getPartyMatch();
                partyMatch.addDeath(player, MatchEnd.DEATH);
            }
        }
        if (playerData.getPlayerState() == PlayerState.GAME) {
            Match match = playerData.getMatch();
            if (match.getMatchState() != MatchState.ENDING) {
                if (player.getKiller() != null) {
                    match.stop(player, MatchEnd.KILLED);
                } else {
                    match.stop(player, MatchEnd.DEATH);
                }
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.GAME) {
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.GAME) {
            Match match = playerData.getMatch();
            if (match.getLadder().getLadderType() == LadderType.SUMO) {
                event.setCancelled(true);
                return;
            }
        } else if (playerData.getPlayerState() == PlayerState.GAME) {
            Match match = playerData.getMatch();
            if (match.getMatchState() != MatchState.GAME) {
                event.setCancelled(true);
                return;
            }
        }
        if (!event.isCancelled()) {
            if (event.getFoodLevel() < player.getFoodLevel()) {
                event.setCancelled(new Random().nextInt(100) < 66);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
            if (playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.PARTY && playerData.getPlayerState() != PlayerState.EVENT) {
                event.setCancelled(true);
            } else if (playerData.getParty() != null && playerData.getParty().getPartyState() != PartyState.GAME) {
                event.setCancelled(true);
            } else if (playerData.getPlayerState() == PlayerState.GAME) {
                Match match = playerData.getMatch();
                if (match.getMatchState() != MatchState.GAME) {
                    event.setCancelled(true);
                }
            } else if (playerData.getPlayerState() == PlayerState.PARTY) {
                Party party = playerData.getParty();
                if (party.getPartyState() == PartyState.GAME) {
                    PartyMatch partyMatch = party.getPartyMatch();
                    if (partyMatch.getMatchState() != MatchState.GAME) {
                        event.setCancelled(true);
                    }
                }
            } else if (playerData.getPlayerState() == PlayerState.EVENT) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getDamager();
            Player attacked = (Player) event.getEntity();
            PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
            if (playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.PARTY && playerData.getPlayerState() != PlayerState.EVENT) {
                event.setCancelled(true);
            }
            if (playerData.getParty() != null && playerData.getParty().getPartyState() != PartyState.GAME) {
                event.setCancelled(true);
            }
            if (playerData.getPlayerState() == PlayerState.PARTY) {
                Party party = playerData.getParty();
                if (party.getPartyState() == PartyState.GAME) {
                    PartyMatch partyMatch = party.getPartyMatch();
                    if (partyMatch.getDead().contains(player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                    if (partyMatch.getPartyMatchType() == PartyMatchType.SPLIT) {
                        if ((partyMatch.getTeamOne().contains(player.getUniqueId()) && partyMatch.getTeamOne().contains(attacked.getUniqueId())) || (partyMatch.getTeamTwo().contains(player.getUniqueId()) && partyMatch.getTeamTwo().contains(attacked.getUniqueId()))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                event.setCancelled(true);
            }
            if (playerData.getPlayerState() == PlayerState.EVENT && player.getInventory().contains(new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cLeave Event").buildItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        Location from = event.getFrom();
        Location to = event.getTo();
        if (player.getLocation().getY() <= 15) {
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
        }
        if (playerData.getPlayerState() == PlayerState.PARTY) {
            Party party = playerData.getParty();
            if (party.getPartyState() == PartyState.GAME) {
                PartyMatch partyMatch = party.getPartyMatch();
                if (partyMatch.getMatchState() == MatchState.STARTING && partyMatch.getLadder().getLadderType() == LadderType.SUMO) {
                    if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                        event.setTo(from.setDirection(to.getDirection()));
                    }
                } else if (partyMatch.getMatchState() == MatchState.GAME && partyMatch.getLadder().getLadderType() == LadderType.SUMO) {
                    Location loc = player.getLocation();
                    loc.setY(loc.getY() - 2);
                    if (player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_WATER || player.getWorld().getBlockAt(loc).getType() == Material.WATER || player.getWorld().getBlockAt(loc).getType() == Material.LAVA || player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_LAVA) {
                        partyMatch.addDeath(player, MatchEnd.DEATH);
                    }
                }
            }
        }
        if (playerData.getPlayerState() == PlayerState.GAME) {
            Match match = playerData.getMatch();
            if (match.getMatchState() == MatchState.STARTING && match.getLadder().getLadderType() == LadderType.SUMO) {
                if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                    event.setTo(from.setDirection(to.getDirection()));
                }
            } else if (match.getMatchState() == MatchState.GAME && match.getLadder().getLadderType() == LadderType.SUMO) {
                Location loc = player.getLocation();
                loc.setY(loc.getY() - 2);
                if (player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_WATER || player.getWorld().getBlockAt(loc).getType() == Material.WATER || player.getWorld().getBlockAt(loc).getType() == Material.LAVA || player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_LAVA) {
                    match.stop(player, MatchEnd.OTHER);
                }
            }
        } else if (playerData.getPlayerState() == PlayerState.EVENT) {
            Location loc = player.getLocation();
            loc.setY(loc.getY() - 2);
            if ((player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_WATER || player.getWorld().getBlockAt(loc).getType() == Material.WATER || player.getWorld().getBlockAt(loc).getType() == Material.LAVA || player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_LAVA) && !player.getInventory().contains(new ItemBuilder(new ItemStack(Material.REDSTONE)).setName("&cLeave Event").buildItemStack())) {
                this.plugin.getManagerHandler().getEventManager().getCurrentEvent().removePlayer(player);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if ((playerData.getPlayerState() != PlayerState.GAME && playerData.getPlayerState() != PlayerState.PARTY) && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
        }
        if (playerData.getPlayerState() == PlayerState.PARTY && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Party party = playerData.getParty();
            if (party.getPartyState() == PartyState.GAME) {
                PartyMatch partyMatch = party.getPartyMatch();
                if (partyMatch.getMatchState() == MatchState.STARTING) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getDuelInventory())) {
            playerData.setDueled(null);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final float before = player.getSaturation();

        new BukkitRunnable() {
            public void run() {
                float change = player.getSaturation() - before;
                player.setSaturation((float) (before + change * 2.5D));
            }
        }.runTaskLater(this.plugin, 1L);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            for (Player all : this.plugin.getServer().getOnlinePlayers()) {
                if (!all.canSee(player)) {
                    this.plugin.getEntityHider().hideEntity(all, event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        if (event.getLastToken() != null) {
            for (Player all : this.plugin.getServer().getOnlinePlayers()) {
                if (all.getName().toLowerCase().startsWith(event.getLastToken().toLowerCase()) && !event.getTabCompletions().contains(all.getName())) {
                    event.getTabCompletions().add(all.getName());
                }
            }
        }
    }
}
