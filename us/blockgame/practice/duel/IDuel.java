package us.blockgame.practice.duel;

        import us.blockgame.practice.ladder.Ladder;

        import java.util.UUID;

public interface IDuel {

    UUID getRequester();
    Ladder getLadder();
}
