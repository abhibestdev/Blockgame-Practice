package us.blockgame.practice.managers;

import org.bukkit.Location;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.util.PlayersFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SettingsManager extends Manager {

    private HashMap<String, Location> locationsMap;
    private PlayersFile playersFile;
    private List<String> players;

    public SettingsManager(ManagerHandler managerHandler) {
        super(managerHandler);
        locationsMap = new HashMap<>();
        this.playersFile = PlayersFile.getInstance();
        players = playersFile.getData().getStringList("players");
        load();
    }

    private void load() {
        for (String uuids : players) {
            UUID uuid = UUID.fromString(uuids);
            managerHandler.getPlayerDataManager().addPlayer(uuid);
        }
        try {
            String world = managerHandler.getPlugin().getConfig().getString("spawn.world");
            int x = managerHandler.getPlugin().getConfig().getInt("spawn.x");
            int y = managerHandler.getPlugin().getConfig().getInt("spawn.y");
            int z = managerHandler.getPlugin().getConfig().getInt("spawn.z");
            float yaw = (float) managerHandler.getPlugin().getConfig().getInt("spawn.yaw");
            float pitch = (float) managerHandler.getPlugin().getConfig().getInt("spawn.pitch");
            Location spawn = new Location(managerHandler.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            locationsMap.put("spawn", spawn);
            System.out.print("Loaded location spawn.");
        } catch (Exception ex) {
            System.out.print("Error loading location spawn.");
        }
        try {
            String world = managerHandler.getPlugin().getConfig().getString("editor.world");
            int x = managerHandler.getPlugin().getConfig().getInt("editor.x");
            int y = managerHandler.getPlugin().getConfig().getInt("editor.y");
            int z = managerHandler.getPlugin().getConfig().getInt("editor.z");
            float yaw = (float) managerHandler.getPlugin().getConfig().getInt("editor.yaw");
            float pitch = (float) managerHandler.getPlugin().getConfig().getInt("editor.pitch");
            Location editor = new Location(managerHandler.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            locationsMap.put("editor", editor);
            System.out.print("Loaded location editor.");
        } catch (Exception ex) {
            System.out.print("Error loading location editor.");
        }
        try {
            String world = managerHandler.getPlugin().getConfig().getString("event.spawn.world");
            int x = managerHandler.getPlugin().getConfig().getInt("event.spawn.x");
            int y = managerHandler.getPlugin().getConfig().getInt("event.spawn.y");
            int z = managerHandler.getPlugin().getConfig().getInt("event.spawn.z");
            float yaw = (float) managerHandler.getPlugin().getConfig().getInt("event.spawn.yaw");
            float pitch = (float) managerHandler.getPlugin().getConfig().getInt("event.spawn.pitch");
            Location eventSpawn = new Location(managerHandler.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            locationsMap.put("event-spawn", eventSpawn);
            System.out.print("Loaded location event spawn.");
        } catch (Exception ex) {
            System.out.print("Error loading location event spawn.");
        }
        try {
            String world = managerHandler.getPlugin().getConfig().getString("event.point1.world");
            int x = managerHandler.getPlugin().getConfig().getInt("event.point1.x");
            int y = managerHandler.getPlugin().getConfig().getInt("event.point1.y");
            int z = managerHandler.getPlugin().getConfig().getInt("event.point1.z");
            float yaw = (float) managerHandler.getPlugin().getConfig().getInt("event.point1.yaw");
            float pitch = (float) managerHandler.getPlugin().getConfig().getInt("event.point1.pitch");
            Location eventSpawn1 = new Location(managerHandler.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            locationsMap.put("event-point-1", eventSpawn1);
            System.out.print("Loaded location event point 1.");
        } catch (Exception ex) {
            System.out.print("Error loading location event point 1.");
        }
        try {
            String world = managerHandler.getPlugin().getConfig().getString("event.point2.world");
            int x = managerHandler.getPlugin().getConfig().getInt("event.point2.x");
            int y = managerHandler.getPlugin().getConfig().getInt("event.point2.y");
            int z = managerHandler.getPlugin().getConfig().getInt("event.point2.z");
            float yaw = (float) managerHandler.getPlugin().getConfig().getInt("event.point2.yaw");
            float pitch = (float) managerHandler.getPlugin().getConfig().getInt("event.point2.pitch");
            Location eventSpawn2 = new Location(managerHandler.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
            locationsMap.put("event-point-2", eventSpawn2);
            System.out.print("Loaded location event point 2.");
        } catch (Exception ex) {
            System.out.print("Error loading location event point 2.");
        }
    }

    public HashMap<String, Location> getLocationsMap() {
        return locationsMap;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void save() {
        playersFile.getData().set("players", players);
        playersFile.saveData();
    }
}
