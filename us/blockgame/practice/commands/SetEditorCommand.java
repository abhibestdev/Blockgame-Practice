package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;

public class SetEditorCommand implements CommandExecutor {

    private Practice plugin;

    public SetEditorCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are the console!");
            return true;
        }
        if (!sender.hasPermission("practice.command.seteditor")) {
            sender.sendMessage(ChatColor.RED + "You do not have the required rank to perform this action.");
            return true;
        }
        Player player = (Player) sender;
        this.plugin.getConfig().set("editor.world", player.getWorld().getName());
        this.plugin.getConfig().set("editor.x", player.getLocation().getX());
        this.plugin.getConfig().set("editor.y", player.getLocation().getY());
        this.plugin.getConfig().set("editor.z", player.getLocation().getZ());
        this.plugin.getConfig().set("editor.yaw", player.getLocation().getYaw());
        this.plugin.getConfig().set("editor.pitch", player.getLocation().getPitch());
        this.plugin.saveConfig();
        sender.sendMessage(ChatColor.YELLOW + "Editor has been set!");
        return true;
    }
}
