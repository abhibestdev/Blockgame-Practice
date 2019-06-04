package us.blockgame.practice.util;

import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import us.blockgame.practice.ladder.Ladder;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class Util {

    public static ArrayList<String> newList(String... strings) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return list;
    }

    public static int getFilledSlots(Inventory inventory) {
        int slots = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                slots += 1;
            }
        }
        return slots;
    }

    public static ItemStack createQueueItem(Ladder ladder, ItemStack item, boolean ranked) {
        ItemStack queueItem;
        if (ranked) {
            queueItem = new ItemBuilder(new ItemStack(item.getType(), ladder.getRankedMatch().size(), (short) item.getDurability())).setName(item.getItemMeta().getDisplayName()).setLore("&eIn queue: &a" + ladder.getRankedQueue().size(), "&eIn match: &a" + ladder.getRankedMatch().size()).buildItemStack();
        } else {
            queueItem = new ItemBuilder(new ItemStack(item.getType(), ladder.getUnrankedMatch().size(), (short) item.getDurability())).setName(item.getItemMeta().getDisplayName()).setLore("&eIn queue: &a" + ladder.getUnrankedQueue().size(), "&eIn match: &a" + ladder.getUnrankedMatch().size()).buildItemStack();
        }
        return queueItem;
    }

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void playSound(Sound sound, Player... players) {
        for (Player player : players) {
            player.playSound(player.getLocation(), sound, 20L, 20L);
        }
    }

    public static void loadChunks(Location... locations) {
        for (Location location : locations) {
            Chunk chunk = location.getWorld().getChunkAt(location);
            location.getWorld().loadChunk(chunk);
        }
    }


    public static void unloadChunks(Location... locations) {
        for (Location location : locations) {
            Chunk chunk = location.getWorld().getChunkAt(location);
            location.getWorld().unloadChunk(chunk);
        }
    }

    public static String getFirst10Letters(String name) {
        return name.substring(0, Math.min(name.length(), 10));
    }

    public static void resetPlayer(Player... players) {
        for (Player player : players) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }
    }

    public static void saveInventory(Player... players) {
        for (Player player : players) {
            new InventorySnapshot(player);
        }
    }

       private static double[] getEstimations(double rankingA, double rankingB) {
        double[] ret = new double[2];
        double estA = 1.0 / (1.0 + Math.pow(10.0, (rankingB - rankingA) / 400.0));
        double estB = 1.0 / (1.0 + Math.pow(10.0, (rankingA - rankingB) / 400.0));
        ret[0] = estA;
        ret[1] = estB;
        return ret;
    }

    private static int getConstant(int ranking) {
        if (ranking < 1400) {
            return 32;
        }
        if (ranking >= 1400 && ranking < 1800) {
            return 24;
        }
        if (ranking >= 1800 && ranking < 2400) {
            return 16;
        }
        return 0;
    }

    public static int[] getNewRankings(int rankingA, int rankingB, boolean victoryA) {
        int[] elo = new int[2];
        double[] estimates = getEstimations(rankingA, rankingB);
        int newRankA = (int) (rankingA + getConstant(rankingA) * ((victoryA ? 1 : 0) - estimates[0]));
        elo[0] = Math.round(newRankA);
        elo[1] = Math.round(rankingB - (newRankA - rankingA));
        return elo;
    }

    public static void showAllPlayers(ArrayList<UUID> list) {
        for (UUID uuid : list) {
            Player player = Bukkit.getPlayer(uuid);
            for (UUID uuid2 : list) {
                Player player2 = Bukkit.getPlayer(uuid2);
                if (player != player2) {
                    player.showPlayer(player2);
                    player2.showPlayer(player);
                }
            }
        }
    }

    public static String fetchName(UUID uuid) throws Exception {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + uuid.toString());
        String name = (String) ((JSONObject) new JSONParser().parse(new InputStreamReader(url.openStream()))).get("name");
        return name;
    }

    public static void resetName(Player p) {
        GameProfile gp = ((CraftPlayer) p).getProfile();
        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);

            nameField.set(gp, fetchName(p.getUniqueId()));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void changeName(Player p, String newName) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl == p)
                continue;
            // REMOVES THE PLAYER
            // CHANGES THE PLAYER'S GAME PROFILE
            GameProfile gp = ((CraftPlayer) p).getProfile();
            PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) p).getHandle());
            try {
                Field nameField = GameProfile.class.getDeclaredField("name");
                nameField.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);

                nameField.set(gp, newName);
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                throw new IllegalStateException(ex);
            }
            // ADDS THE PLAYER
            PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer) p).getHandle());
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getEntityId()));
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()));
        }
    }
}