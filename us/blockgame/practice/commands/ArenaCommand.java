package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.arena.ArenaType;

public class ArenaCommand implements CommandExecutor {

    private final Practice plugin;

    public ArenaCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        if (!sender.hasPermission("practice.command.arena")) {
            sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " <create:delete:edit> <name> <possible argument>");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " create <name> <displayName>");
                return true;
            }
            String name = args[1];
            if (this.plugin.getManagerHandler().getArenaManager().arenaExists(name)) {
                sender.sendMessage(ChatColor.RED + "That arena already exists!");
                return true;
            }
            String displayName = args[2];
            this.plugin.getManagerHandler().getArenaManager().createArena(name, displayName);
            sender.sendMessage(ChatColor.YELLOW  + "Arena " + ChatColor.GOLD + name + " (" + ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.GOLD + ") " + ChatColor.YELLOW + " has been created.");
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " delete <name>");
                return true;
            }
            String name = args[1];
            if (!this.plugin.getManagerHandler().getArenaManager().arenaExists(name)) {
                sender.sendMessage(ChatColor.RED + "That arena doesn't exist!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().deleteArena(name);
            sender.sendMessage(ChatColor.YELLOW  + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been deleted.");
            return true;
        }
        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " edit <name> <sumo:normal:builduhc:setspawn1:setspawn2>");
                return true;
            }
            String name = args[1];
            if (!this.plugin.getManagerHandler().getArenaManager().arenaExists(name)) {
                sender.sendMessage(ChatColor.RED + "That arena doesn't exist!");
                return true;
            }
            if (args[2].equalsIgnoreCase("sumo")) {
                this.plugin.getManagerHandler().getArenaManager().setType(name, ArenaType.SUMO);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("builduhc")) {
                this.plugin.getManagerHandler().getArenaManager().setType(name, ArenaType.BUILDUHC);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("normal")) {
                this.plugin.getManagerHandler().getArenaManager().setType(name, ArenaType.NORMAL);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("hcf")) {
                this.plugin.getManagerHandler().getArenaManager().setType(name, ArenaType.HCF);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("setspawn1")) {
                this.plugin.getManagerHandler().getArenaManager().setSpawn(name, player.getLocation(), 1);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("setspawn2")) {
                this.plugin.getManagerHandler().getArenaManager().setSpawn(name, player.getLocation(), 2);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            return true;
        }
        return true;
    }
}
