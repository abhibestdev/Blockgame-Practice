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
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyInvite;
import us.blockgame.practice.util.PlayerState;

import java.util.ArrayList;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    private final Practice plugin;

    public PartyCommand(Practice plugin) {
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
        if (args.length < 1) {
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "/party create" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Create a party.");
            sender.sendMessage(ChatColor.GOLD + "/party join <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Join a party.");
            sender.sendMessage(ChatColor.GOLD + "/party leave" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Leave party.");
            sender.sendMessage(ChatColor.GOLD + "/party kick <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Kick player from party.");
            sender.sendMessage(ChatColor.GOLD + "/party disband" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Disband your party.");
            sender.sendMessage(ChatColor.GOLD + "/party info" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "View party info.");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "@" + ChatColor.YELLOW + " to talk in party chat.");
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (playerData.getPlayerState() != PlayerState.LOBBY) {
                sender.sendMessage(ChatColor.RED + "You cannot do this in your current state.");
                return true;
            }
            playerData.setParty(new Party(player.getUniqueId()));
            this.plugin.getManagerHandler().getPlayerManager().givePartyItems(player, true);
            playerData.setPlayerState(PlayerState.PARTY);
            sender.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "You created a party.");
            return true;
        }
        if (args[0].equalsIgnoreCase("disband")) {
            if (playerData.getPlayerState() != PlayerState.PARTY) {
                sender.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            Party party = playerData.getParty();
            if (party.getLeader() != player.getUniqueId()) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader!");
                return true;
            }
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                PlayerData memberData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(members);
                this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(members);
                this.plugin.getManagerHandler().getPlayerManager().giveItems(members, false);
                members.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Your party was disbanded.");
                memberData.setParty(null);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (playerData.getPlayerState() != PlayerState.PARTY) {
                sender.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            Party party = playerData.getParty();
            ArrayList<String> memberNames = new ArrayList<>();
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                if (uuid != party.getLeader()) {
                    memberNames.add(members.getName());
                }
            }
            String status = null;
            if (party.isOpen()) {
                status = ChatColor.GREEN + "Open";
            } else if (!party.isOpen()) {
                status = ChatColor.RED + "Closed";
            }
            Player leader = this.plugin.getServer().getPlayer(party.getLeader());
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.YELLOW + leader.getName());
            sender.sendMessage(ChatColor.GOLD + "Members: " + ChatColor.YELLOW + memberNames.toString().replace("[", "").replace("]", ""));
            sender.sendMessage(ChatColor.GOLD + "Status: " + status);
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
            return true;
        }
        if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " invite <player>");
                return true;
            }
            if (playerData.getPlayerState() != PlayerState.PARTY) {
                sender.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            Party party = playerData.getParty();
            if (party.getLeader() != player.getUniqueId()) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader!");
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null || target == player) {
                sender.sendMessage(ChatColor.RED + "Could not find player.");
                return true;
            }
            if (party.getMembers().contains(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "That player is already in the party!");
                return true;
            }
            PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
            boolean hasInvite = false;
            for (PartyInvite partyInvite : targetData.getPartyInvites()) {
                if (partyInvite.getParty() == party) {
                    hasInvite = true;
                }
            }
            if (hasInvite) {
                sender.sendMessage(ChatColor.RED + "That player already has an invite from this party.");
                return true;
            }
            PartyInvite partyInvite = new PartyInvite(party);
            TextComponent partyMessage = new TextComponent(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has invited you to a party ");
            TextComponent acceptMessage = new TextComponent(ChatColor.GREEN + "[Click to accept]");
            acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept invite.").create()));
            acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + sender.getName()));
            partyMessage.addExtra(acceptMessage);
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has been invited to the party.");
            }
            target.spigot().sendMessage(partyMessage);
            targetData.getPartyInvites().add(partyInvite);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (targetData.getPartyInvites().contains(partyInvite)) {
                        targetData.getPartyInvites().remove(partyInvite);
                    }
                }
            }.runTaskLaterAsynchronously(this.plugin, 600L);
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " accept <player>");
                return true;
            }
            if (playerData.getPlayerState() != PlayerState.LOBBY) {
                sender.sendMessage(ChatColor.RED + "You cannot do this in your current state!");
                return true;
            }
            if (playerData.getParty() != null) {
                sender.sendMessage(ChatColor.RED + "You are already in a party!");
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null || target == player) {
                sender.sendMessage(ChatColor.RED + "Could not find player.");
                return true;
            }
            PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
            if (targetData.getParty() == null) {
                sender.sendMessage(ChatColor.RED + "That player is not in a party.");
                return true;
            }
            Party party = targetData.getParty();
            boolean hasInvite = false;
            PartyInvite invite = null;
            for (PartyInvite partyInvite : playerData.getPartyInvites()) {
                if (partyInvite.getParty() == party) {
                    hasInvite = true;
                    invite = partyInvite;
                }
            }
            if (hasInvite) {
                sender.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "You have joined the party!");
                party.getMembers().add(player.getUniqueId());
                this.plugin.getManagerHandler().getPlayerManager().givePartyItems(player, false);
                for (UUID uuid : party.getMembers()) {
                    Player members = this.plugin.getServer().getPlayer(uuid);
                    playerData.setPlayerState(PlayerState.PARTY);
                    playerData.setParty(party);
                    members.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has joined the party.");
                    playerData.getPartyInvites().remove(invite);
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "You do not have an invite from that party.");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " kick <player>");
                return true;
            }
            if (playerData.getParty() == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (playerData.getParty().getLeader() != player.getUniqueId()) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader!");
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null || target == player) {
                sender.sendMessage(ChatColor.RED + "Could not find player.");
                return true;
            }
            PlayerData targetData = this.plugin.getManagerHandler().getPlayerDataManager().getPlayerData(target);
            if (targetData.getParty() == null || targetData.getParty() != playerData.getParty()) {
                sender.sendMessage(ChatColor.RED + "That player is not in your a party.");
                return true;
            }
            Party party = playerData.getParty();
            party.getMembers().remove(target.getUniqueId());
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has been kicked from the party.");
            }
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(target);
            this.plugin.getManagerHandler().getPlayerManager().giveItems(target, false);
            target.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "You were kicked from the party.");
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (playerData.getParty() == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            Party party = playerData.getParty();
            if (party.getLeader() == player.getUniqueId()) {
                player.chat("/party disband");
                return true;
            }
            for (UUID uuid : party.getMembers()) {
                Player members = this.plugin.getServer().getPlayer(uuid);
                members.sendMessage(ChatColor.GOLD + "[Party] " + ChatColor.YELLOW + "Player " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has left the party.");
            }
            party.getMembers().remove(player.getUniqueId());
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
            this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
            return true;
        }
        return true;
    }
}
