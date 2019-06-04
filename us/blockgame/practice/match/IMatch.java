package us.blockgame.practice.match;

import org.bukkit.entity.Player;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.util.Timer;

import java.util.ArrayList;
import java.util.UUID;

public interface IMatch {

    Arena getArena();
    void start();
    void stop(Player loser, MatchEnd matchEnd);
    MatchState getMatchState();
    Player getPlayerOne();
    Player getPlayerTwo();
    Timer getTimer();
    Ladder getLadder();
    boolean isRanked();
    ArrayList<UUID> getSpectators();
}
