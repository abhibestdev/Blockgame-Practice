package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.util.PlayerState;

public class DuelCommand implements CommandExecutor {

    private Practice plugin;

    public DuelCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
        if (target == null || target == player) {
            sender.sendMessage(ChatColor.RED + "Could not find player.");
            return true;
        }
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.LOBBY) {
            sender.sendMessage(ChatColor.RED + "You cannot do this in your current state!");
            return true;
        }
        PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
        if (targetData.getPlayerState() != PlayerState.LOBBY) {
            sender.sendMessage(ChatColor.RED + "That player is not in the lobby!");
            return true;
        }
        boolean hasInvite = false;
        for (Duel duel : targetData.getDuels()) {
            if (duel.getRequester() == player.getUniqueId()) {
            }
            hasInvite = true;
        }
        if (hasInvite) {
            sender.sendMessage(ChatColor.RED + "You already sent a duel request to this player! Please wait for it to expire.");
            return true;
        }
        playerData.setDueled(target.getUniqueId());
        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getDuelInventory());
        return true;
    }
}
