package us.blockgame.practice.arena;

import org.bukkit.Location;

public class Arena implements IArena {

    private final String displayName;
    private final Location spawnOne;
    private final Location spawnTwo;
    private final ArenaType arenaType;

    public Arena(String displayName, Location spawnOne, Location spawnTwo, ArenaType arenaType) {
        this.displayName = displayName;
        this.spawnOne = spawnOne;
        this.spawnTwo = spawnTwo;
        this.arenaType = arenaType;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Location getSpawnOne() {
        return spawnOne;
    }

    @Override
    public Location getSpawnTwo() {
        return spawnTwo;
    }

    @Override
    public ArenaType getAreanaType() {
        return arenaType;
    }
}
