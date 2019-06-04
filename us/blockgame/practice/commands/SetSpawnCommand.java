package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;

public class SetSpawnCommand implements CommandExecutor {

    private Practice plugin;

    public SetSpawnCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        if (!sender.hasPermission("practice.command.setspawn")) {
            sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
            return true;
        }
        Player player = (Player) sender;
        this.plugin.getConfig().set("spawn.world", player.getWorld().getName());
        this.plugin.getConfig().set("spawn.x", player.getLocation().getX());
        this.plugin.getConfig().set("spawn.y", player.getLocation().getY());
        this.plugin.getConfig().set("spawn.z", player.getLocation().getZ());
        this.plugin.getConfig().set("spawn.yaw", player.getLocation().getYaw());
        this.plugin.getConfig().set("spawn.pitch", player.getLocation().getPitch());
        this.plugin.saveConfig();
        sender.sendMessage(ChatColor.YELLOW + "Spawn has been set!");
        return true;
    }
}
