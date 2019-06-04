package us.blockgame.practice.util.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;

public class ScoreboardRunnable extends BukkitRunnable {

    private final Practice plugin;

    public ScoreboardRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player all : this.plugin.getServer().getOnlinePlayers()) {
            this.plugin.getManagerHandler().getScoreboardManager().update(all);
        }
    }
}
