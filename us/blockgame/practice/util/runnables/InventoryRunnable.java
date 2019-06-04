package us.blockgame.practice.util.runnables;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.Practice;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.managers.PlayerDataManager;
import us.blockgame.practice.util.PlayersFile;
import us.blockgame.practice.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryRunnable extends BukkitRunnable {

    private final Practice plugin;
    private final PlayersFile playersFile = PlayersFile.getInstance();

    public InventoryRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    public void run() {
        unranked();
        ranked();
        try {
            leaderboards();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void unranked() {
        for (Ladder ladder : this.plugin.getManagerHandler().getLadderManager().getLadders()) {
            this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory().setItem(ladder.getId(), Util.createQueueItem(ladder, ladder.getDisplayItem(), false));
        }
    }

    private void ranked() {
        for (Ladder ladder : this.plugin.getManagerHandler().getLadderManager().getLadders()) {
            this.plugin.getManagerHandler().getInventoryManager().getRankedInventory().setItem(ladder.getId(), Util.createQueueItem(ladder, ladder.getDisplayItem(), true));
        }
    }


    private void leaderboards() throws IOException {
        ManagerHandler handler = this.plugin.getManagerHandler();
        for (Ladder ladder : handler.getLadderManager().getLadders()) {
            List<String> lore = new ArrayList<>();
            PlayerDataManager dataManager = handler.getPlayerDataManager();
            List<EloEntry> entries = handler.getSettingsManager().getPlayers().stream().map(stringUUID -> {
                UUID uuid = UUID.fromString(stringUUID);
                PlayerData playerData = dataManager.getPlayerDataMap().get(uuid);
                return new EloEntry(uuid, playerData.getElo(ladder));
            }).sorted(EloEntry.comparator).collect(Collectors.toList());
            Collections.reverse(entries);
            int
                    entriesSize = entries.size(),
                    extras = entriesSize >= 10 ? 0 : 10 - entriesSize;

            lore.add(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------");
            for (int i = 1; i <= 10; i++) {
                if (i <= 10 - extras) {
                    EloEntry entry = entries.get(i - 1);
                    OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(entry.uuid);
                    if (player == null) continue;

                    lore.add(ChatColor.YELLOW + "#" + i + ". " + ChatColor.DARK_GREEN + player.getName() + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + entry.elo);
                } else {
                    lore.add(ChatColor.YELLOW + "#" + String.valueOf(i) + ". " + ChatColor.GRAY + "~");
                }
            }
            lore.add(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------");

            ItemStack item = ladder.getDisplayItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            handler.getInventoryManager().getLeaderboardsInventory().setItem(ladder.getId(), item);
        }
    }

    private static class EloEntry {
        private static final Comparator<EloEntry> comparator = Comparator.comparingInt(entry -> entry.elo);
        private final UUID uuid;
        private final int elo;

        public EloEntry(UUID uuid, int elo) {
            this.uuid = uuid;
            this.elo = elo;
        }
    }
}

