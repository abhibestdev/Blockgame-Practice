package us.blockgame.practice.match;

import org.bukkit.entity.Player;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.party.Party;

import java.util.ArrayList;
import java.util.UUID;

public interface IPartyMatch {

    Party getParty();
    Arena getArena();
    PartyMatchType getPartyMatchType();
    void start();
    void stop();
    void addDeath(Player player, MatchEnd matchEnd);
    ArrayList<UUID> getTeamOne();
    ArrayList<UUID> getTeamTwo();
    MatchState getMatchState();
    Ladder getLadder();
}
