package us.blockgame.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.event.Event;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.match.MatchState;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.scoreboard.ScoreHelper;
import us.blockgame.practice.util.PlayerState;

public class ScoreboardManager extends Manager {

    public ScoreboardManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public void update(Player player) {
        PlayerData playerData = this.managerHandler.getPlayerDataManager().getPlayerData(player);
        if (ScoreHelper.getByPlayer(player) != null) {
            ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);
            scoreHelper.setTitle("&bBlockgame &8┃ &bPractice");
            if (playerData.getPlayerState() == PlayerState.LOBBY) {
                for (int i = 6; i <= 15; i++) {
                    scoreHelper.removeSlot(i);
                }
                scoreHelper.setSlot(5, "&8&m--------------------");
                scoreHelper.setSlot(4, "&bOnline&7: &f" + getOnlinePlayers()); // separate method to get online players instead of .size() because IntelliJ doesn't allow it.
                scoreHelper.setSlot(3, "&bQueueing&7: &f" + getInQueue());
                scoreHelper.setSlot(2, "&bFighting&7: &f" + getFighting());
                scoreHelper.setSlot(1, "&8&m--------------------");
            } else if (playerData.getPlayerState() == PlayerState.QUEUE) {
                if (playerData.isQueuedRanked()) {
                    for (int i = 5; i <= 15; i++) {
                        scoreHelper.removeSlot(i);
                    }
                    scoreHelper.setSlot(5, "&8&m--------------------");
                    scoreHelper.setSlot(4, "&aSearching for match...");
                    scoreHelper.setSlot(3, "&7» &3" + ChatColor.stripColor(playerData.getQueuedLadder().getDisplayItem().getItemMeta().getDisplayName()));
                    scoreHelper.setSlot(2, "&7» &b[" + playerData.getMinElo() + " -> " + playerData.getMaxElo() + "]");
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else if (!playerData.isQueuedRanked()) {
                    for (int i = 6; i <= 15; i++) {
                        scoreHelper.removeSlot(i);
                    }
                    scoreHelper.setSlot(5, "&8&m--------------------");
                    scoreHelper.setSlot(4, "&aSearching for match...");
                    scoreHelper.setSlot(3, "&7» &3" + ChatColor.stripColor(playerData.getQueuedLadder().getDisplayItem().getItemMeta().getDisplayName()));
                    scoreHelper.setSlot(2, "&7» &bUnranked");
                    scoreHelper.setSlot(1, "&8&m--------------------");
                }
            } else if (playerData.getPlayerState() == PlayerState.GAME) {
                Player opponent = null;
                Match match = playerData.getMatch();
                if (match.getPlayerOne() == player) {
                    opponent = match.getPlayerTwo();
                } else {
                    opponent = match.getPlayerOne();
                }
                for (int i = 5; i <= 15; i++) {
                    scoreHelper.removeSlot(i);
                }
                if (match.getMatchState() == MatchState.STARTING) {
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&6Opponent: &f" + opponent.getName());
                    scoreHelper.setSlot(2, "&6Starting: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else if (match.getMatchState() == MatchState.GAME) {
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&6Opponent: &f" + opponent.getName());
                    scoreHelper.setSlot(2, "&6Time: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else if (match.getMatchState() == MatchState.ENDING) {
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&6Winner: &f" + match.getWinner().getName());
                    scoreHelper.setSlot(2, "&6Match Length: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                }
            } else if (playerData.getPlayerState() == PlayerState.PARTY) {
                for (int i = 5; i <= 15; i++) {
                    scoreHelper.removeSlot(i);
                }
                Party party = playerData.getParty();
                Player leader = managerHandler.getPlugin().getServer().getPlayer(party.getLeader());
                scoreHelper.setSlot(4, "&8&m--------------------");
                scoreHelper.setSlot(3, "&bLeader&7: &f" + leader.getName());
                scoreHelper.setSlot(2, "&bMembers&7: &f" + party.getMembers().size());
                scoreHelper.setSlot(1, "&8&m--------------------");
            } else if (playerData.getPlayerState() == PlayerState.EVENT) {
                for (int i = 5; i <= 15; i++) {
                    scoreHelper.removeSlot(i);
                }
                Event event = this.managerHandler.getEventManager().getCurrentEvent();
                if (!event.hasStarted()) {
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&bPlayers&7: &f" + event.getMembers().size() + "/100");
                    scoreHelper.setSlot(2, "&bStarting&7: &f" + event.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else {
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&bPlayers&7: &f" + event.getMembers().size() + "/100");
                    scoreHelper.setSlot(2, "&bDuration&7: &f" + event.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                }
            } else if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                Match match = playerData.getSpectating();
                if (match.getMatchState() == MatchState.STARTING) {
                    scoreHelper.setSlot(5, "&8&m--------------------");
                    scoreHelper.setSlot(4, "&6Player One: &f" + match.getPlayerOne().getName());
                    scoreHelper.setSlot(3, "&6Player Two: &f" + match.getPlayerTwo().getName());
                    scoreHelper.setSlot(2, "&6Starting: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else if (match.getMatchState() == MatchState.GAME) {
                    scoreHelper.setSlot(5, "&8&m--------------------");
                    scoreHelper.setSlot(4, "&6Player One: &f" + match.getPlayerOne().getName());
                    scoreHelper.setSlot(3, "&6Player Two: &f" + match.getPlayerTwo().getName());
                    scoreHelper.setSlot(2, "&6Time: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                } else if (match.getMatchState() == MatchState.ENDING) {
                    for (int i = 5; i <= 15; i++) {
                        scoreHelper.removeSlot(i);
                    }
                    scoreHelper.setSlot(4, "&8&m--------------------");
                    scoreHelper.setSlot(3, "&6Winner: &f" + match.getWinner().getName());
                    scoreHelper.setSlot(2, "&6Match Length: &f" + match.getTimer().getTime());
                    scoreHelper.setSlot(1, "&8&m--------------------");
                }
            }
        }
    }

    private int getOnlinePlayers() {
        int online = 0;
        for (Player all : this.managerHandler.getPlugin().getServer().getOnlinePlayers()) {
            online += 1;
        }
        return online;
    }

    private int getFighting() {
        int fighting = 0;
        for (Player all : this.managerHandler.getPlugin().getServer().getOnlinePlayers()) {
            PlayerData allData = this.managerHandler.getPlayerDataManager().getPlayerData(all);
            if (allData.getPlayerState() == PlayerState.GAME) {
                fighting += 1;
            }
        }
        return fighting;
    }

    private int getInQueue() {
        int inQueue = 0;
        for (Ladder ladder : this.managerHandler.getLadderManager().getLadders()) {
            inQueue += ladder.getUnrankedQueue().size() + ladder.getRankedQueue().size();
        }
        return inQueue;
    }
}
