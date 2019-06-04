package us.blockgame.practice.party;

import us.blockgame.practice.match.PartyMatch;

import java.util.ArrayList;
import java.util.UUID;

public interface IParty {

    UUID getLeader();
    ArrayList<UUID> getMembers();
    boolean isOpen();
    PartyState getPartyState();
    PartyMatch getPartyMatch();
    void setOpen(boolean open);
    void setPartyState(PartyState partyState);
    void setPartyMatch(PartyMatch partyMatch);
}
