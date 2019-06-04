package us.blockgame.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;
import us.blockgame.practice.arena.ArenaType;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.util.PlayerState;

public class AcceptCommand implements CommandExecutor {

    private final Practice plugin;

    public AcceptCommand(Practice plugin) {
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
        if (target == null) {
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
            sender.sendMessage(ChatColor.RED + "Player is busy!");
            return true;
        }
        boolean hasDuel = false;
        Duel duel = null;
        for (Duel duels : playerData.getDuels()) {
            if (duels.getRequester() == target.getUniqueId()) {
                hasDuel = true;
                duel = duels;
            }
        }
        if (hasDuel) {
            Match match = new Match(this.plugin, player, target, duel.getLadder(), ArenaType.valueOf(duel.getLadder().getLadderType().toString()), false);
            match.start();
            playerData.getDuels().remove(duel);
        } else {
            sender.sendMessage(ChatColor.RED + "That duel request doesn't exist!");
        }
        return true;
    }
}
