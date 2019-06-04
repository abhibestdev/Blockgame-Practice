package us.blockgame.practice.data;

import us.blockgame.practice.Practice;
import us.blockgame.practice.customkit.CustomKit;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyInvite;
import us.blockgame.practice.util.PlayerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private final Practice plugin;
    private final UUID uuid;
    private final HashMap<Ladder, Integer> eloMap;
    private int minElo;
    private int maxElo;
    private Ladder queuedLadder;
    private boolean queuedRanked;
    private PlayerState playerState;
    private Match match;
    private String lastOpponent;
    private final HashMap<Ladder, CustomKit> customKitMap;
    private Ladder editing;
    private boolean buildMode;
    private boolean premiumMatches;
    private Party party;
    private ArrayList<PartyInvite> partyInvites;
    private ArrayList<Duel> duels;
    private UUID dueled;
    private Match spectating;
    private boolean update;

    public PlayerData(Practice plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        eloMap = new HashMap<>();
        playerState = PlayerState.LOBBY;
        customKitMap = new HashMap<>();
        partyInvites = new ArrayList<>();
        duels = new ArrayList<>();
    }

    public int getElo(Ladder ladder) {
        return eloMap.getOrDefault(ladder, 1000);
    }

    public int getMinElo() {
        return minElo;
    }

    public int getMaxElo() {
        return maxElo;
    }

    public Ladder getQueuedLadder() {
        return queuedLadder;
    }

    public boolean isQueuedRanked() {
        return queuedRanked;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public Match getMatch() {
        return match;
    }

    public String getLastOpponent() {
        return lastOpponent;
    }

    public UUID getUUID() {
        return uuid;
    }

    public HashMap<Ladder, CustomKit> getCustomKitMap() {
        return customKitMap;
    }

    public Ladder getEditing() {
        return editing;
    }

    public boolean hasBuildMode() {
        return buildMode;
    }

    public boolean gotPremiumMatches() {
        return premiumMatches;
    }

    public Party getParty() {
        return party;
    }

    public ArrayList<PartyInvite> getPartyInvites() {
        return partyInvites;
    }

    public ArrayList<Duel> getDuels() {
        return duels;
    }

    public UUID getDueled() {
        return dueled;
    }

    public Match getSpectating() {
        return spectating;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setElo(Ladder ladder, int newElo) {
        eloMap.put(ladder, newElo);
    }

    public void setMinElo(int minElo) {
        this.minElo = minElo;
    }

    public void setMaxElo(int maxElo) {
        this.maxElo = maxElo;
    }

    public void setQueuedLadder(Ladder queuedLadder) {
        this.queuedLadder = queuedLadder;
    }

    public void setQueuedRanked(boolean queuedRanked) {
        this.queuedRanked = queuedRanked;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setLastOpponent(String lastOpponent) {
        this.lastOpponent = lastOpponent;
    }

    public void setEditing(Ladder editing) {
        this.editing = editing;
    }

    public void setBuildMode(boolean buildMode) {
        this.buildMode = buildMode;
    }

    public void setGotPremiumMatches(boolean premiumMatches) {
        this.premiumMatches = premiumMatches;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setDueled(UUID dueled) {
        this.dueled = dueled;
    }

    public void setSpectating(Match spectating) {
        this.spectating = spectating;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
