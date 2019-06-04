package us.blockgame.practice.managers;

import org.bukkit.entity.Player;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager extends Manager {

    private HashMap<UUID, PlayerData> playerDataMap;

    public PlayerDataManager(ManagerHandler managerHandler) {
        super(managerHandler);
        playerDataMap = new HashMap<>();
    }

    public void addPlayer(Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData(this.managerHandler.getPlugin(), player.getUniqueId()));
    }

    public void addPlayer(UUID uuid) {
        playerDataMap.put(uuid, new PlayerData(this.managerHandler.getPlugin(), uuid));
    }

    public void removePlayer(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return playerDataMap.containsKey(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }

    public HashMap<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }
}
