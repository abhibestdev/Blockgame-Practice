package us.blockgame.practice.arena;

import org.bukkit.Location;

public interface IArena {

    String getDisplayName();
    Location getSpawnOne();
    Location getSpawnTwo();
    ArenaType getAreanaType();
}
