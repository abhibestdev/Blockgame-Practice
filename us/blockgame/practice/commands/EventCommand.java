package us.blockgame.practice.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.event.Event;
import us.blockgame.practice.util.PlayerState;

import java.util.UUID;

public class EventCommand implements CommandExecutor {

    private Practice plugin;

    public EventCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        Player player = (Player) sender;
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " <start:stop:setspawn:setpoint1:setpoint2:>");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!sender.hasPermission("practice.command.event")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
                return true;
            }
            if (this.plugin.getManagerHandler().getEventManager().currentEvent()) {
                sender.sendMessage(ChatColor.RED + "There is already an event started.");
                return true;
            }
            Event event = new Event(this.plugin, sender.getName());
            this.plugin.getManagerHandler().getEventManager().setCurrentEvent(event);
            new BukkitRunnable() {
                public void run() {
                    if (plugin.getManagerHandler().getEventManager().currentEvent()) {
                        if (plugin.getManagerHandler().getEventManager().getCurrentEvent().hasStarted()) {
                            this.cancel();
                        } else {
                            for (Player all : plugin.getManagerHandler().getPlugin().getServer().getOnlinePlayers()) {
                                TextComponent joinMessage = new TextComponent(ChatColor.GOLD + "[Event] " + sender.getName() + ChatColor.YELLOW + " is hosting a sumo event ");
                                TextComponent acceptMessage = new TextComponent(ChatColor.GREEN + "[Join]");
                                acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join event.").create()));
                                acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event join"));
                                joinMessage.addExtra(acceptMessage);
                                all.spigot().sendMessage(joinMessage);
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(this.plugin, 0L, 200L);
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (playerData.getPlayerState() != PlayerState.LOBBY) {
                sender.sendMessage(ChatColor.RED + "You cannot do this in your current state!");
                return true;
            }
            if (!this.plugin.getManagerHandler().getEventManager().currentEvent()) {
                sender.sendMessage(ChatColor.RED + "There is no event going on.");
                return true;
            }
            Event event = this.plugin.getManagerHandler().getEventManager().getCurrentEvent();
            if (event.getMembers().size() >= 100) {
                sender.sendMessage(ChatColor.RED + "The event is full.");
                return true;
            }
            if (event.hasStarted()) {
                sender.sendMessage(ChatColor.RED + "The event has already started.");
                return true;
            }
            playerData.setPlayerState(PlayerState.EVENT);
            event.addPlayer(player);
            this.plugin.getManagerHandler().getPlayerManager().teleportLocation(player, "event-spawn");
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (playerData.getPlayerState() != PlayerState.EVENT) {
                sender.sendMessage(ChatColor.RED + "You cannot do this in your current state!");
                return true;
            }
            Event event = this.plugin.getManagerHandler().getEventManager().getCurrentEvent();
            if (event.getMembers().contains(player.getUniqueId())) {
                event.getMembers().remove(player.getUniqueId());
            }
            if (event.getSpectators().contains(player.getUniqueId())) {
                event.getSpectators().remove(player.getUniqueId());
            }
            for (UUID uuid : event.getAllPlayers()) {
                Player all = this.plugin.getServer().getPlayer(uuid);
                all.sendMessage(ChatColor.GOLD + "[Event] " + sender.getName() + ChatColor.YELLOW + " has left the event.");
            }
            event.getAllPlayers().remove(player.getUniqueId());
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
            this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("practice.event.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
                return true;
            }
            this.plugin.getConfig().set("event.spawn.world", player.getWorld().getName());
            this.plugin.getConfig().set("event.spawn.x", player.getLocation().getX());
            this.plugin.getConfig().set("event.spawn.y", player.getLocation().getY());
            this.plugin.getConfig().set("event.spawn.z", player.getLocation().getZ());
            this.plugin.getConfig().set("event.spawn.yaw", player.getLocation().getYaw());
            this.plugin.getConfig().set("event.spawn.pitch", player.getLocation().getPitch());
            this.plugin.saveConfig();
            sender.sendMessage(ChatColor.YELLOW + "Event has been set!");
            return true;
        }
        if (args[0].equalsIgnoreCase("setpoint1")) {
            if (!sender.hasPermission("practice.event.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
                return true;
            }
            this.plugin.getConfig().set("event.point1.world", player.getWorld().getName());
            this.plugin.getConfig().set("event.point1.x", player.getLocation().getX());
            this.plugin.getConfig().set("event.point1.y", player.getLocation().getY());
            this.plugin.getConfig().set("event.point1.z", player.getLocation().getZ());
            this.plugin.getConfig().set("event.point1.yaw", player.getLocation().getYaw());
            this.plugin.getConfig().set("event.point1.pitch", player.getLocation().getPitch());
            this.plugin.saveConfig();
            sender.sendMessage(ChatColor.YELLOW + "Event point 1 has been set!");
            return true;
        }
        if (args[0].equalsIgnoreCase("setpoint2")) {
            if (!sender.hasPermission("practice.event.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
                return true;
            }
            this.plugin.getConfig().set("event.point2.world", player.getWorld().getName());
            this.plugin.getConfig().set("event.point2.x", player.getLocation().getX());
            this.plugin.getConfig().set("event.point2.y", player.getLocation().getY());
            this.plugin.getConfig().set("event.point2.z", player.getLocation().getZ());
            this.plugin.getConfig().set("event.point2.yaw", player.getLocation().getYaw());
            this.plugin.getConfig().set("event.point2.pitch", player.getLocation().getPitch());
            this.plugin.saveConfig();
            sender.sendMessage(ChatColor.YELLOW + "Event point 2 has been set!");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("practice.event.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
                return true;
            }
            if (!this.plugin.getManagerHandler().getEventManager().currentEvent()) {
                sender.sendMessage(ChatColor.RED + "There is no event started.");
                return true;
            }
            this.plugin.getManagerHandler().getEventManager().getCurrentEvent().stop("No one");
            return true;
        }
        return true;

    }
}
