package us.blockgame.practice.match;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.ArenaType;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.ladder.LadderType;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.util.Timer;
import us.blockgame.practice.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class PartyMatch implements IPartyMatch, Listener {

    private Practice plugin;
    private Party party;
    private PartyMatchType partyMatchType;
    private Ladder ladder;
    private ArenaType arenaType;
    private MatchState matchState;
    private Timer timer;
    private ArrayList<UUID> teamOne;
    private ArrayList<UUID> teamTwo;
    private ArrayList<UUID> members;
    private ArrayList<UUID> dead;

    public PartyMatch(Practice plugin, Party party, PartyMatchType partyMatchType, Ladder ladder, ArenaType arenaType) {
        this.plugin = plugin;
        this.party = party;
        this.partyMatchType = partyMatchType;
        this.ladder = ladder;
        this.arenaType = arenaType;
        teamOne = new ArrayList<>();
        teamTwo = new ArrayList<>();
        members = new ArrayList<>();
        dead = new ArrayList<>();
        timer = new Timer(this.plugin, 5, true);
        party.setPartyState(PartyState.GAME);
        party.setPartyMatch(this);
        load();
    }

    private void load() {
        for (UUID uuid : party.getMembers()) {
            members.add(uuid);
        }
    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public Arena getArena() {
        return this.plugin.getManagerHandler().getArenaManager().getRandomArena(arenaType);
    }

    @Override
    public PartyMatchType getPartyMatchType() {
        return partyMatchType;
    }

    @Override
    public void start() {
        matchState = MatchState.STARTING;
        Arena arena = getArena();
        Util.loadChunks(arena.getSpawnOne(), arena.getSpawnTwo());
        for (UUID uuid : party.getMembers()) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            for (UUID uuid2 : party.getMembers()) {
                Player player2 = this.plugin.getServer().getPlayer(uuid2);
                if (player != player2) {
                    player.showPlayer(player2);
                    player2.showPlayer(player);
                }
            }
        }
        if (partyMatchType == PartyMatchType.SPLIT) {
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.sendMessage(ChatColor.YELLOW + "Party split event starting in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
                members.sendMessage(ChatColor.YELLOW + "Arena: " + ChatColor.GOLD + arena.getDisplayName());
            }
            splitIntoGroups();
            for (UUID uuid : teamOne) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.teleport(arena.getSpawnOne());
                Util.resetPlayer(members);
                NametagEdit.getApi().setNametag(members, ChatColor.AQUA + "", "");
                this.plugin.getManagerHandler().getPlayerManager().giveKit(members, ladder);
                members.sendMessage(ChatColor.YELLOW + "You belong to " + ChatColor.AQUA + "Blue" + ChatColor.YELLOW + ".");
            }
            for (UUID uuid : teamTwo) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.teleport(arena.getSpawnTwo());
                Util.resetPlayer(members);
                NametagEdit.getApi().setNametag(members, ChatColor.RED + "", "");
                this.plugin.getManagerHandler().getPlayerManager().giveKit(members, ladder);
                members.sendMessage(ChatColor.YELLOW + "You belong to " + ChatColor.RED + "Red" + ChatColor.YELLOW + ".");
            }
        } else if (partyMatchType == PartyMatchType.FFA) {
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.teleport(arena.getSpawnOne());
                Util.resetPlayer(members);
                this.plugin.getManagerHandler().getPlayerManager().giveKit(members, ladder);
            }
        }
        if (ladder.getLadderType() == LadderType.SUMO) {
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
            }
        }
        new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                if (matchState == MatchState.ENDING) {
                    this.cancel();
                } else {
                    for (UUID uuid : party.getMembers()) {
                        Player members = plugin.getServer().getPlayer(uuid);
                        members.sendMessage(ChatColor.YELLOW + "Starting in " + ChatColor.GOLD + i + "...");
                        members.playSound(members.getLocation(), Sound.NOTE_STICKS, 20L, 20L);
                    }
                    i--;
                    if (i <= 0) {
                        matchState = MatchState.GAME;
                        for (UUID uuid : party.getMembers()) {
                            Player members = plugin.getServer().getPlayer(uuid);
                            members.sendMessage(ChatColor.GREEN + "Match started, good luck!");
                            members.playSound(members.getLocation(), Sound.NOTE_PLING, 20L, 20L);
                        }
                        this.cancel();
                        timer = new Timer(plugin, -1, false);
                        timer.start();
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
    }

    @Override
    public void stop() {
        party.setPartyState(PartyState.ENDING);
    }

    @Override
    public void addDeath(Player player, MatchEnd matchEnd) {
        Util.resetPlayer(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealthScale(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (UUID uuid : party.getMembers()) {
            Player members = this.plugin.getServer().getPlayer(uuid);
            members.hidePlayer(player);
            if (matchEnd == MatchEnd.DEATH) {
                members.sendMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " died.");
            } else if (matchEnd == MatchEnd.DISCONNECT) {
                members.sendMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " disconnected.");
            }
        }
        if (!dead.contains(player.getUniqueId())) {
            dead.add(player.getUniqueId());
        }
        if (teamOne.contains(player.getUniqueId())) {
            teamOne.remove(player.getUniqueId());
        }
        if (teamTwo.contains(player.getUniqueId())) {
            teamTwo.remove(player.getUniqueId());
        }
        if (partyMatchType == PartyMatchType.FFA) {
            if (party.getMembers().size() - dead.size() == 1) {
                for (UUID winnerUUID : party.getMembers()) {
                    if (!dead.contains(winnerUUID)) {
                        Player winner = this.plugin.getServer().getPlayer(winnerUUID);
                        for (UUID uuid : party.getMembers()) {
                            Player members = this.plugin.getServer().getPlayer(uuid);
                            members.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.GREEN + winner.getName());
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (UUID uuid : party.getMembers()) {
                                    Player members = plugin.getServer().getPlayer(uuid);
                                    plugin.getManagerHandler().getPlayerManager().teleportSpawn(members);
                                    if (party.getLeader() != uuid) {
                                        plugin.getManagerHandler().getPlayerManager().givePartyItems(members, false);
                                    } else {
                                        plugin.getManagerHandler().getPlayerManager().givePartyItems(members, true);
                                    }
                                    party.setPartyState(PartyState.LOBBY);
                                    party.setPartyMatch(null);
                                }
                            }
                        }.runTaskLater(this.plugin, 60L);
                    }
                }
            }
        } else if (partyMatchType == PartyMatchType.SPLIT) {
            if (teamTwo.size() == 0 || teamOne.size() == 0) {
                String winner = null;
                if (teamTwo.size() == 0) {
                    winner = "Blue";
                } else if (teamOne.size() == 0) {
                    winner = "Red";
                }
                for (UUID uuid : party.getMembers()) {
                    Player members = this.plugin.getServer().getPlayer(uuid);
                    members.sendMessage(ChatColor.YELLOW + "The winning team is " + ChatColor.GOLD + winner + ChatColor.YELLOW + "!");
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (UUID uuid : party.getMembers()) {
                            Player members = plugin.getServer().getPlayer(uuid);
                            plugin.getManagerHandler().getPlayerManager().teleportSpawn(members);
                            if (party.getLeader() != uuid) {
                                plugin.getManagerHandler().getPlayerManager().givePartyItems(members, false);
                            } else {
                                plugin.getManagerHandler().getPlayerManager().givePartyItems(members, true);
                            }
                            party.setPartyState(PartyState.LOBBY);
                            party.setPartyMatch(null);
                        }
                    }
                }.runTaskLater(this.plugin, 60L);
            }
        }
    }

    @Override
    public ArrayList<UUID> getTeamOne() {
        return teamOne;
    }

    @Override
    public ArrayList<UUID> getTeamTwo() {
        return teamTwo;
    }

    @Override
    public MatchState getMatchState() {
        return matchState;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public Ladder getLadder() {
        return ladder;
    }

    public ArrayList<UUID> getDead() {
        return dead;
    }

    private void splitIntoGroups() {
        if (members.size() > 1) {
            int groupSize = (int) (Math.round(members.size() / 2));
            for (int i = 0; i < groupSize; i++) {
                int randInt = new Random().nextInt(members.size());
                teamOne.add(members.get(randInt));
                members.remove(randInt);
                for (UUID uuid : teamOne) {
                    //  Bukkit.broadcastMessage(uuid.toString() + " - 1");
                }
            }
            for (UUID uuid : members) {
                teamTwo.add(uuid);
                //Bukkit.broadcastMessage(uuid.toString() + " - 2");
            }
        }
    }
}
