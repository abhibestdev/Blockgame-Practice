package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.ladder.LadderType;

public class LadderCommand implements CommandExecutor {

    private final Practice plugin;

    public LadderCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        if (!sender.hasPermission("practice.command.ladder")) {
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
            String displayName = args[2];
            if (this.plugin.getManagerHandler().getLadderManager().ladderExists(name)) {
                sender.sendMessage(ChatColor.RED + "That ladder already exists.");
                return true;
            }
            this.plugin.getManagerHandler().getLadderManager().createLadder(name, displayName);
            sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + " (" + ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.GOLD + ") " + ChatColor.YELLOW + " has been created.");
            return true;
        }
        if (args[0].equals("delete")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " delete <name>");
                return true;
            }
            String name = args[1];
            if (!this.plugin.getManagerHandler().getLadderManager().ladderExists(name)) {
                sender.sendMessage(ChatColor.RED + "That ladder doesn't exist.");
                return true;
            }
            this.plugin.getManagerHandler().getLadderManager().deleteLadder(name);
            sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been deleted.");
            return true;
        }
        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " edit <name> <sumo:normal:builduhc:editable:setdisplay:setkit>");
                return true;
            }
            String name = args[1];
            if (!this.plugin.getManagerHandler().getLadderManager().ladderExists(name)) {
                sender.sendMessage(ChatColor.RED + "That ladder doesn't exist.");
                return true;
            }
            if (args[2].equalsIgnoreCase("sumo")) {
                this.plugin.getManagerHandler().getLadderManager().setType(name, LadderType.SUMO);
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("builduhc")) {
                this.plugin.getManagerHandler().getLadderManager().setType(name, LadderType.BUILDUHC);
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("normal")) {
                this.plugin.getManagerHandler().getLadderManager().setType(name, LadderType.NORMAL);
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("hcf")) {
                this.plugin.getManagerHandler().getLadderManager().setType(name, LadderType.HCF);
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("editable")) {
                this.plugin.getManagerHandler().getLadderManager().setEditable(name);
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("setdisplay")) {
                if (player.getItemInHand() == null) {
                    sender.sendMessage(ChatColor.RED + "There is no item in your hand.");
                    return true;
                }
                this.plugin.getManagerHandler().getLadderManager().setDisplayItem(name, player.getItemInHand());
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
            if (args[2].equalsIgnoreCase("setkit")) {
                this.plugin.getManagerHandler().getLadderManager().setInventory(name, player.getInventory(), player.getInventory().getArmorContents());
                sender.sendMessage(ChatColor.YELLOW + "Ladder " + ChatColor.GOLD + name + ChatColor.YELLOW + " has been edited.");
                return true;
            }
        }
        return true;
    }
}
