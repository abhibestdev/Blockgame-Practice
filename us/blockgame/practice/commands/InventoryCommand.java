package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.util.InventorySnapshot;

public class InventoryCommand implements CommandExecutor {

    private Practice plugin;

    public InventoryCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public  boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " <player>");
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player.");
            return true;
        }
        if (InventorySnapshot.getByPlayer(target) == null) {
            sender.sendMessage(ChatColor.RED + "No inventory found.");
            return true;
        }
        player.openInventory(InventorySnapshot.getByPlayer(target).getInventory());
        return true;
    }
}
