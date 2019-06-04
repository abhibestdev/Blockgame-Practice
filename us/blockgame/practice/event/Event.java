package us.blockgame.practice.event;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.blockgame.practice.Practice;
import us.blockgame.practice.util.Timer;
import us.blockgame.practice.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Event implements IEvent {

    private Practice plugin;
    private String host;
    private EventType eventType;
    private boolean started;
    private ArrayList<UUID> members;
    private ArrayList<UUID> spectators;
    private ArrayList<UUID> allPlayers;
    private Timer timer;
    private BukkitTask task;
    private Player pOne;
    private Player pTwo;


    public Event(Practice plugin, String host) {
        this.plugin = plugin;
        this.host = host;
        members = new ArrayList<>();
        spectators = new ArrayList<>();
        allPlayers = new ArrayList<>();
        timer = new Timer(this.plugin, 61, true);
        timer.start();
        task = new BukkitRunnable() {
            public void run() {
                if (plugin.getManagerHandler().getEventManager().currentEvent()) {
                    start();
                }
            }
        }.runTaskLaterAsynchronously(this.plugin, 1200L);
    }

    @Override
    public void start() {
        started = true;
        timer = new Timer(this.plugin, 0, false);
        timer.start();
        for (UUID uuid : allPlayers) {
            Player all = this.plugin.getServer().getPlayer(uuid);
            all.sendMessage(ChatColor.GOLD + "[Event] " + ChatColor.YELLOW + "The event has started!");
        }
        pickFight();
    }

    @Override
    public void stop(String winnerName) {
        timer.stop();
        String winner = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------------------------\n" + ChatColor.GOLD + winnerName + ChatColor.YELLOW + " has won the tournament.\n" + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------------------------";
        for (Player all : this.plugin.getServer().getOnlinePlayers()) {
            all.sendMessage(winner);
        }
        for (UUID uuid : allPlayers) {
            Player all = this.plugin.getServer().getPlayer(uuid);
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(all);
            this.plugin.getManagerHandler().getPlayerManager().giveItems(all, false);
        }
        this.plugin.getManagerHandler().getEventManager().setCurrentEvent(null);
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public ArrayList<UUID> getMembers() {
        return members;
    }

    @Override
    public ArrayList<UUID> getSpectators() {
        return spectators;
    }

    @Override
    public ArrayList<UUID> getAllPlayers() {
        return allPlayers;
    }

    @Override
    public void setStarted(boolean started) {
        this.started = started;
    }

    public void addPlayer(Player player) {
        members.add(player.getUniqueId());
        for (UUID uuid : members) {
            Player member = this.plugin.getServer().getPlayer(uuid);
            if (member != null) {
                member.showPlayer(player);
                player.showPlayer(member);
            }
            this.plugin.getManagerHandler().getPlayerManager().giveEventItem(member);
            member.sendMessage(ChatColor.GOLD + "[Event] " + player.getName() + ChatColor.YELLOW + " has joined the event " + ChatColor.GOLD + "(" + members.size() + "/100)");
        }
        allPlayers.add(player.getUniqueId());
    }

    public void removePlayer(Player loser) {
        for (UUID uuid : allPlayers) {
            Player member = this.plugin.getServer().getPlayer(uuid);
            member.sendMessage(ChatColor.GOLD + "[Event] " + loser.getName() + ChatColor.YELLOW + " has been eliminated.");
            this.plugin.getManagerHandler().getPlayerManager().giveEventItem(member);
        }
        members.remove(loser.getUniqueId());
        spectators.add(loser.getUniqueId());
        this.plugin.getManagerHandler().getPlayerManager().teleportLocation(pOne, "event-spawn");
        this.plugin.getManagerHandler().getPlayerManager().teleportLocation(pTwo, "event-spawn");
        pickFight();
    }

    private void pickFight() {
        if (members.size() == 1) {
            for (UUID uuid : members) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                stop(member.getName());
            }
        } else if (members.size() == 0) {
            stop("No one");
        } else {
            Random random = new Random();
            UUID one = members.get(random.nextInt(members.size()));
            ArrayList<UUID> freeMembers = new ArrayList();
            for (UUID uuid : members) {
                if (uuid != one) {
                    freeMembers.add(uuid);
                }
            }
            new BukkitRunnable() {
                int i = 5;

                public void run() {
                    if (!plugin.getManagerHandler().getEventManager().currentEvent()) {
                        this.cancel();
                    }
                    for (UUID uuid : allPlayers) {
                        Player all = plugin.getServer().getPlayer(uuid);
                        all.sendMessage(ChatColor.GOLD + "[Event] " + ChatColor.YELLOW + "Next fight starting in " + ChatColor.GOLD + i + "...");
                    }
                    i--;
                    if (i == 0) {
                        this.cancel();
                        UUID two = freeMembers.get(random.nextInt(freeMembers.size()));
                        Player playerOne = plugin.getServer().getPlayer(one);
                        Player playerTwo = plugin.getServer().getPlayer(two);
                        Util.resetPlayer(playerOne, playerTwo);
                        playerOne.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
                        playerTwo.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
                        plugin.getManagerHandler().getPlayerManager().teleportLocation(playerOne, "event-point-1");
                        plugin.getManagerHandler().getPlayerManager().teleportLocation(playerTwo, "event-point-2");
                        pOne = playerOne;
                        pTwo = playerTwo;
                        for (UUID uuid : allPlayers) {
                            Player all = plugin.getServer().getPlayer(uuid);
                            all.sendMessage(ChatColor.GOLD + "[Event] " + ChatColor.YELLOW + "Starting match: " + ChatColor.GOLD + playerOne.getName() + " vs " + playerTwo.getName());
                        }
                    }
                }
            }.runTaskTimer(this.plugin, 0L, 20L);
        }
    }

    public Timer getTimer() {
        return timer;
    }
}
