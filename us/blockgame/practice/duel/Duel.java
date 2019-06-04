package us.blockgame.practice.duel;

import us.blockgame.practice.ladder.Ladder;

import java.util.UUID;

public class Duel implements IDuel {

    private UUID requester;
    private Ladder ladder;

    public Duel(UUID requester, Ladder ladder) {
        this.requester = requester;
        this.ladder = ladder;
    }

    @Override
    public UUID getRequester() {
        return requester;
    }

    @Override
    public Ladder getLadder() {
        return ladder;
    }
}
