package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.util.PlayerState;
import us.blockgame.practice.util.Util;

import java.util.UUID;

public class SpectateCommand implements CommandExecutor {

    private Practice plugin;

    public SpectateCommand(Practice plugin) {
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
            sender.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
            return true;
        }
        PlayerData playerData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.LOBBY) {
            sender.sendMessage(ChatColor.RED + "You cannot do this in your current state!");
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null || target == player) {
            sender.sendMessage(ChatColor.RED + "Could not find player.");
            return true;
        }
        PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
        if (targetData.getPlayerState() != PlayerState.GAME) {
            sender.sendMessage(ChatColor.RED + "Player is not in a fight!");
            return true;
        }
        Match match = targetData.getMatch();
        match.getSpectators().add(player.getUniqueId());
        playerData.setSpectating(match);
        playerData.setPlayerState(PlayerState.SPECTATING);
        this.plugin.getManagerHandler().getPlayerManager().giveSpectatorItems(player);
        if (!player.hasPermission("practice.staff")) {
           for (UUID uuid : match.getSpectators()) {
               Player spectators = this.plugin.getServer().getPlayer(uuid);
               spectators.sendMessage(ChatColor.GOLD + "[Spectate] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " is now spectating.");
           }
           Util.sendMessage(ChatColor.GOLD + "[Spectate] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " is now spectating.", match.getPlayerOne(), match.getPlayerTwo());
        }
        player.teleport(target);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.showPlayer(match.getPlayerOne());
        player.showPlayer(match.getPlayerTwo());
        return true;
    }
}
