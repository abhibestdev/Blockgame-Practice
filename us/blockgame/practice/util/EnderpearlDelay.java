package us.blockgame.practice.util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.blockgame.practice.Practice;

import java.util.HashMap;
import java.util.UUID;

public class EnderpearlDelay {

    private Practice plugin;
    private int startTime;
    private int millis = 0;
    private Player player;
    private BukkitTask task;

    public static HashMap<UUID, EnderpearlDelay> cp = new HashMap<>();

    public EnderpearlDelay(Practice plugin, Player player, int startTime) {
        this.startTime = startTime;
        this.player = player;
        cp.put(player.getUniqueId(), this);
        this.plugin = plugin;
    }

    public void start() {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                millis--;
                if (millis == -1) {
                    millis = 9;
                    startTime--;
                    player.setLevel(startTime + 1);
                }

            }
        }.runTaskTimerAsynchronously(this.plugin, 0, 2);
        this.task = task;

    }

    public void stop() {
        player.setLevel(0);
        task.cancel();
        cp.remove(player.getUniqueId());
    }

    public String getTime() {
        return startTime + "." + millis;
    }

    public static EnderpearlDelay getByPlayer(Player player) {
        return cp.get(player.getUniqueId());
    }

}
