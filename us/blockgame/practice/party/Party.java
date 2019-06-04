package us.blockgame.practice.party;

import us.blockgame.practice.match.PartyMatch;

import java.util.ArrayList;
import java.util.UUID;

public class Party implements IParty {

    private UUID leader;
    private ArrayList<UUID> members;
    private boolean open;
    private PartyState partyState;
    private PartyMatch partyMatch;

    public Party(UUID leader) {
        this.leader = leader;
        members = new ArrayList<>();
        members.add(leader);
        partyState = PartyState.LOBBY;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public ArrayList<UUID> getMembers() {
        return members;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public PartyState getPartyState() {
        return partyState;
    }

    @Override
    public PartyMatch getPartyMatch() {
        return partyMatch;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void setPartyState(PartyState partyState) {
        this.partyState = partyState;
    }

    @Override
    public void setPartyMatch(PartyMatch partyMatch) {
        this.partyMatch = partyMatch;
    }
}
