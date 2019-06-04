package us.blockgame.practice.match;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.ArenaType;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.ladder.LadderType;
import us.blockgame.practice.util.PlayerState;
import us.blockgame.practice.util.Timer;
import us.blockgame.practice.util.Util;

import java.util.ArrayList;
import java.util.UUID;

public class Match implements IMatch {

    private final Practice plugin;
    private final Player playerOne;
    private final Player playerTwo;
    private final Ladder ladder;
    private final ArenaType arenaType;
    private final boolean ranked;
    private MatchState matchState;
    private Timer timer;
    private Player winner;
    private ArrayList<UUID> spectators;
    private Arena arena;

    public Match(Practice plugin, Player playerOne, Player playerTwo, Ladder ladder, ArenaType arenaType, boolean ranked) {
        this.plugin = plugin;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.ladder = ladder;
        this.arenaType = arenaType;
        this.ranked = ranked;
        spectators = new ArrayList<>();
        this.timer = new Timer(this.plugin, 6, true);
    }

    @Override
    public Arena getArena() {
        return this.plugin.getManagerHandler().getArenaManager().getRandomArena(arenaType);
    }

    @Override
    public void start() {
        arena = getArena();
        for (Player all : this.plugin.getServer().getOnlinePlayers()) {
            playerOne.hidePlayer(all);
            playerTwo.hidePlayer(all);
            all.hidePlayer(playerOne);
            all.hidePlayer(playerTwo);
        }
        playerOne.showPlayer(playerTwo);
        playerTwo.showPlayer(playerOne);
        PlayerData playerOneData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(playerOne);
        PlayerData playerTwoData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(playerTwo);
        playerOneData.setPlayerState(PlayerState.GAME);
        playerTwoData.setPlayerState(PlayerState.GAME);
        playerOneData.setMatch(this);
        playerTwoData.setMatch(this);
        matchState = MatchState.STARTING;
        this.plugin.getManagerHandler().getPlayerManager().giveKit(playerOne, ladder);
        this.plugin.getManagerHandler().getPlayerManager().giveKit(playerTwo, ladder);
        if (ladder.getLadderType() == LadderType.SUMO) {
            playerOne.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
            playerTwo.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
        }
        if (ranked) {
            ladder.getRankedQueue().remove(playerOne.getUniqueId());
            ladder.getRankedQueue().remove(playerTwo.getUniqueId());
            ladder.getRankedMatch().add(playerOne.getUniqueId());
            ladder.getRankedMatch().add(playerTwo.getUniqueId());
            playerOne.sendMessage(ChatColor.YELLOW + "Match started against " + ChatColor.GOLD + playerTwo.getName() + " [" + playerTwoData.getElo(ladder) + "]" + ChatColor.YELLOW + " in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
            playerTwo.sendMessage(ChatColor.YELLOW + "Match started against " + ChatColor.GOLD + playerOne.getName() + " [" + playerOneData.getElo(ladder) + "]" + ChatColor.YELLOW + " in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
        } else {
            ladder.getUnrankedQueue().remove(playerOne.getUniqueId());
            ladder.getUnrankedQueue().remove(playerTwo.getUniqueId());
            ladder.getUnrankedMatch().add(playerOne.getUniqueId());
            ladder.getUnrankedMatch().add(playerTwo.getUniqueId());
            playerOne.sendMessage(ChatColor.YELLOW + "Match started against " + ChatColor.GOLD + playerTwo.getName() + ChatColor.YELLOW + " in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
            playerTwo.sendMessage(ChatColor.YELLOW + "Match started against " + ChatColor.GOLD + playerOne.getName() + ChatColor.YELLOW + " in ladder " + ChatColor.DARK_AQUA + ChatColor.stripColor(ladder.getDisplayItem().getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
        }
        Util.sendMessage(ChatColor.YELLOW + "Arena: " + ChatColor.GOLD + arena.getDisplayName(), playerOne, playerTwo);
        Util.loadChunks(arena.getSpawnOne(), arena.getSpawnTwo());
        playerOne.teleport(arena.getSpawnOne());
        playerTwo.teleport(arena.getSpawnTwo());
        this.timer.start();
        new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                if (matchState == MatchState.ENDING) {
                    this.cancel();
                } else {
                    Util.sendMessage(ChatColor.YELLOW + "Starting in " + ChatColor.GOLD + i + "...", playerOne, playerTwo);
                    Util.playSound(Sound.NOTE_STICKS, playerOne, playerTwo);
                    i -= 1;
                    if (i <= 0) {
                        matchState = MatchState.GAME;
                        Util.sendMessage(ChatColor.GREEN + "Match started, good luck!", playerOne, playerTwo);
                        Util.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8 &8 &1 &3 &3 &7 &8"), playerOne, playerTwo);
                        Util.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING! " + ChatColor.GRAY + "Butterfly clicking is strongly discouraged and could result in a punishment. Butterfly click at your own risk.", playerOne, playerTwo);
                        Util.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8 &8 &1 &3 &3 &7 &8"), playerOne, playerTwo);
                        Util.playSound(Sound.NOTE_PLING, playerOne, playerTwo);
                        this.cancel();
                        timer = new Timer(plugin, -1, false);
                        timer.start();
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
    }

    @Override
    public void stop(Player loser, MatchEnd matchEnd) {
        if (matchState == MatchState.ENDING) {
            return;
        }
        timer.stop();
        matchState = MatchState.ENDING;
        winner = null;
        if (playerOne == loser) {
            winner = playerTwo;
        } else {
            winner = playerOne;
        }
        Util.saveInventory(winner, loser);
        Util.resetPlayer(winner, loser);
        winner.hidePlayer(loser);
        PlayerData winnerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(winner);
        PlayerData loserData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(loser);
        winnerData.setLastOpponent(loser.getName());
        loserData.setLastOpponent(winner.getName());
        if (matchEnd == MatchEnd.DEATH) {
            Util.sendMessage(ChatColor.RED + loser.getName() + ChatColor.GRAY + " has died.", winner, loser);
        } else if (matchEnd == MatchEnd.KILLED) {
            Util.sendMessage(ChatColor.RED + loser.getName() + ChatColor.GRAY + " was slain by " + ChatColor.GREEN + winner.getName() + ChatColor.GRAY + ".", winner, loser);
        } else if (matchEnd == MatchEnd.OTHER) {
            Util.sendMessage(ChatColor.RED + "Match ended.", winner, loser);
        } else if (matchEnd == MatchEnd.DISCONNECT) {
            Util.sendMessage(ChatColor.RED + loser.getName() + ChatColor.GRAY + " disconnected.", winner, loser);
        }
        Util.sendMessage(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + " (Click to view)", winner, loser);
        Util.sendMessage("", winner, loser);
        TextComponent winnerComponent = new TextComponent(ChatColor.GOLD + "Winner" + ChatColor.GRAY + ": ");
        TextComponent winnerName = new TextComponent(ChatColor.GREEN + winner.getName());
        winnerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view inventory.").create()));
        winnerName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + winner.getName()));
        winnerComponent.addExtra(winnerName);
        TextComponent loserComponent = new TextComponent(ChatColor.GOLD + "Loser" + ChatColor.GRAY + ": ");
        TextComponent loserName = new TextComponent(ChatColor.RED + loser.getName());
        loserName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view inventory.").create()));
        loserName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + loser.getName()));
        loserComponent.addExtra(loserName);
        winner.spigot().sendMessage(winnerComponent);
        winner.spigot().sendMessage(loserComponent);
        loser.spigot().sendMessage(winnerComponent);
        loser.spigot().sendMessage(loserComponent);
        for (UUID uuid : getSpectators()) {
            Player spectators = this.plugin.getServer().getPlayer(uuid);
            if (spectators != null) {
                spectators.sendMessage(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + " (Click to view)");
                spectators.sendMessage("");
                spectators.spigot().sendMessage(winnerComponent);
                spectators.spigot().sendMessage(loserComponent);
            }
        }
        if (ranked) {
            int winnerElo = winnerData.getElo(ladder);
            int loserElo = loserData.getElo(ladder);
            int[] newElo = Util.getNewRankings(winnerElo, loserElo, true);
            int eloDifference = Math.abs(newElo[0] - winnerElo);
            winnerData.setElo(ladder, newElo[0]);
            loserData.setElo(ladder, newElo[1]);
            String eloChanges = ChatColor.GOLD + "Elo Changes" + ChatColor.GRAY + ": " + ChatColor.GREEN + winner.getName() + " +" + eloDifference + " (" + newElo[0] + ") " + ChatColor.RED + loser.getName() + " -" + eloDifference + " (" + newElo[1] + ")";
            Util.sendMessage(eloChanges, winner, loser);
            for (UUID uuid : getSpectators()) {
                Player spectators = this.plugin.getServer().getPlayer(uuid);
                spectators.sendMessage(eloChanges);
            }
        }
        new BukkitRunnable() {
            public void run() {
                ladder.getUnrankedMatch().remove(playerOne.getUniqueId());
                ladder.getUnrankedMatch().remove(playerTwo.getUniqueId());
                ladder.getRankedMatch().remove(playerOne.getUniqueId());
                ladder.getRankedMatch().remove(playerTwo.getUniqueId());
                if (playerOne != null) {
                    plugin.getManagerHandler().getPlayerManager().teleportSpawn(playerOne);
                    plugin.getManagerHandler().getPlayerManager().giveItems(playerOne, true);
                }
                if (playerTwo != null) {
                    plugin.getManagerHandler().getPlayerManager().teleportSpawn(playerTwo);
                    plugin.getManagerHandler().getPlayerManager().giveItems(playerTwo, true);
                }
                for (UUID uuid : getSpectators()) {
                    Player spectators = plugin.getServer().getPlayer(uuid);
                    if (spectators != null) {
                        plugin.getManagerHandler().getPlayerManager().teleportSpawn(spectators);
                        plugin.getManagerHandler().getPlayerManager().giveItems(spectators, false);
                    }
                }
                Util.unloadChunks(arena.getSpawnOne(), arena.getSpawnTwo());
            }
        }.runTaskLater(this.plugin, 60L);
    }

    @Override
    public MatchState getMatchState() {
        return matchState;
    }

    @Override
    public Player getPlayerOne() {
        return playerOne;
    }

    @Override
    public Player getPlayerTwo() {
        return playerTwo;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public Ladder getLadder() {
        return ladder;
    }

    @Override
    public boolean isRanked() {
        return ranked;
    }

    @Override
    public ArrayList<UUID> getSpectators() {
        return spectators;
    }

    public Player getWinner() {
        return winner;
    }
}
