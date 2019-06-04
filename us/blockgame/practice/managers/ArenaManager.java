package us.blockgame.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.ArenaType;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager extends Manager {

    private ArrayList<Arena> arenas;
    private List<String> rawArenas;

    public ArenaManager(ManagerHandler managerHandler) {
        super(managerHandler);
        arenas = new ArrayList<>();
        rawArenas = managerHandler.getPlugin().getConfig().getStringList("arenas");
        load();
    }

    private void load() {
        for (String rawArena : rawArenas) {
            try {
                String displayName = ChatColor.translateAlternateColorCodes('&', managerHandler.getPlugin().getConfig().getString("arena." + rawArena + ".displayname"));
                ArenaType arenaType = ArenaType.valueOf(managerHandler.getPlugin().getConfig().getString("arena." + rawArena + ".type"));
                String world1 = managerHandler.getPlugin().getConfig().getString("arena." + rawArena + ".world1");
                int x1 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".x1");
                int y1 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".y1");
                int z1 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".z1");
                float yaw1 = (float) managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".yaw1");
                float pitch1 = (float) managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".pitch1");
                String world2 = managerHandler.getPlugin().getConfig().getString("arena." + rawArena + ".world2");
                int x2 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".x2");
                int y2 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".y2");
                int z2 = managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".z2");
                float yaw2 = (float) managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".yaw2");
                float pitch2 = (float) managerHandler.getPlugin().getConfig().getInt("arena." + rawArena + ".pitch2");
                Location spawnOne = new Location(managerHandler.getPlugin().getServer().getWorld(world1), x1, y1, z1, yaw1, pitch1);
                Location spawnTwo = new Location(managerHandler.getPlugin().getServer().getWorld(world2), x2, y2, z2, yaw2, pitch2);
                Arena arena = new Arena(displayName, spawnOne, spawnTwo, arenaType);
                arenas.add(arena);
                System.out.print("Loaded arena " + rawArena + ".");
            } catch (Exception ex) {
                System.out.print("Error loading arena " + rawArena + ".");
            }
        }
    }

    public ArrayList<Arena> getArenas() {
        return arenas;
    }

    public Arena getRandomArena(ArenaType arenaType) {
        Random random = new Random();
        ArrayList<Arena> choices = new ArrayList<>();
        for (Arena arena : arenas) {
            if (arena.getAreanaType() == arenaType) {
                choices.add(arena);
            }
        }
        return choices.get(random.nextInt(choices.size()));
    }

    public boolean arenaExists(String name) {
        return managerHandler.getPlugin().getConfig().getStringList("arenas").contains(name);
    }

    public void createArena(String name, String displayName) {
        rawArenas.add(name);
        managerHandler.getPlugin().getConfig().set("arenas", rawArenas);
        managerHandler.getPlugin().getConfig().set("arena." + name + ".displayname", displayName);
        managerHandler.getPlugin().getConfig().set("arena." + name + ".type", ArenaType.NORMAL.toString());
        managerHandler.getPlugin().saveConfig();
    }

    public void deleteArena(String name) {
        rawArenas.remove(name);
        managerHandler.getPlugin().getConfig().set("arenas", rawArenas);
        managerHandler.getPlugin().getConfig().set("arena." + name, null);
        managerHandler.getPlugin().saveConfig();
    }

    public void setSpawn(String name, Location location, int num) {
        managerHandler.getPlugin().getConfig().set("arena." + name + ".world" + num, location.getWorld().getName());
        managerHandler.getPlugin().getConfig().set("arena." + name + ".x" + num, location.getX());
        managerHandler.getPlugin().getConfig().set("arena." + name + ".y" + num, location.getY());
        managerHandler.getPlugin().getConfig().set("arena." + name + ".z" + num, location.getZ());
        managerHandler.getPlugin().getConfig().set("arena." + name + ".yaw" + num, location.getYaw());
        managerHandler.getPlugin().getConfig().set("arena." + name + ".pitch" + num, location.getPitch());
        managerHandler.getPlugin().saveConfig();
    }

    public void setType(String name, ArenaType arenaType) {
        managerHandler.getPlugin().getConfig().set("arena." + name + ".type", arenaType.toString());
        managerHandler.getPlugin().saveConfig();
    }
}
