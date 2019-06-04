package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.data.PlayerData;

public class BuildingCommand implements CommandExecutor {

    private final Practice plugin;

    public BuildingCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        if (!sender.hasPermission("practice.command.building")) {
            sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
            return true;
        }
        Player player = (Player) sender;
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.hasBuildMode()) {
            playerData.setBuildMode(false);
            sender.sendMessage(ChatColor.GOLD + "[Building] " + ChatColor.YELLOW + "Building mode " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
        } else {
            playerData.setBuildMode(true);
            sender.sendMessage(ChatColor.GOLD + "[Building] " + ChatColor.YELLOW + "Building mode " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
            return true;
        }
        return true;
    }
}
