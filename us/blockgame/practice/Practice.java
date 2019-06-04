package us.blockgame.practice;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockgame.practice.commands.*;
import us.blockgame.practice.commands.completions.CommandCompleter;
import us.blockgame.practice.customkit.CustomKit;
import us.blockgame.practice.data.PlayerData;
import us.blockgame.practice.ladder.Ladder;
import us.blockgame.practice.listeners.PlayerListener;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.util.EntityHider;
import us.blockgame.practice.util.PlayerDataFile;
import us.blockgame.practice.util.PlayersFile;
import us.blockgame.practice.util.runnables.InventoryRunnable;
import us.blockgame.practice.util.runnables.ScoreboardRunnable;

import java.util.UUID;

public class Practice extends JavaPlugin {

    private static Practice instance;
    private ManagerHandler managerHandler;
    private EntityHider entityHider;
    private PlayersFile playersFile = PlayersFile.getInstance();
    private PlayerDataFile playerDataFile = PlayerDataFile.getInstance();

    public void onEnable() {
        instance = this;
        createFiles();
        registerManagers();
        registerCommands();
        registerListeners();
        loadPlayerData();
        registerRunnables();
        for (Chunk chunk : this.getServer().getWorld("world").getLoadedChunks()) {
            chunk.unload(true, false);
        }
    }

    public void onDisable() {
        this.playersFile.getData().set("players", this.managerHandler.getSettingsManager().getPlayers());
        this.playersFile.saveData();
        savePlayerData();
        for (Entity entity : this.getServer().getWorld("world").getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }
        for (Chunk chunk : this.getServer().getWorld("world").getLoadedChunks()) {
            chunk.unload(true, false);
        }
        for (Player all : getServer().getOnlinePlayers()) {
            all.kickPlayer("Restarting.");
        }
    }

    private void createFiles() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        playersFile.setup(this);
        playerDataFile.setup(this);
    }

    private void registerManagers() {
        managerHandler = new ManagerHandler(this);
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
    }

    private void registerCommands() {
        getCommand("ladder").setExecutor(new LadderCommand(this));
        getCommand("ladder").setTabCompleter(new CommandCompleter(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("arena").setTabCompleter(new CommandCompleter(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("seteditor").setExecutor(new SetEditorCommand(this));
        getCommand("inventory").setExecutor(new InventoryCommand(this));
        getCommand("inventory").setTabCompleter(new CommandCompleter(this));
        getCommand("building").setExecutor(new BuildingCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("party").setTabCompleter(new CommandCompleter(this));
        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("duel").setTabCompleter(new CommandCompleter(this));
        getCommand("accept").setExecutor(new AcceptCommand(this));
        getCommand("accept").setTabCompleter(new CommandCompleter(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("spectate").setTabCompleter(new CommandCompleter(this));
        getCommand("event").setExecutor(new EventCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityHider(this, EntityHider.Policy.BLACKLIST), this);
    }

    private void loadPlayerData() {
        for (String uuids : playersFile.getData().getStringList("players")) {
            UUID uuid = UUID.fromString(uuids);
            managerHandler.getPlayerDataManager().addPlayer(uuid);
            PlayerData playerData = managerHandler.getPlayerDataManager().getPlayerDataMap().get(uuid);
            try {
                for (Ladder ladder : managerHandler.getLadderManager().getLadders()) {
                    if (playerDataFile.getData().getConfigurationSection(uuids + "." + ladder.getRawName()) != null) {
                        playerData.setElo(ladder, playerDataFile.getData().getInt(uuids + "." + ladder.getRawName() + ".elo"));
                        if (!playerDataFile.getData().getString(uuids + "." + ladder.getRawName() + ".inventory").equals("none") && !playerDataFile.getData().getString(uuids + "." + ladder.getRawName() + ".armor").equals("none")) {
                            CustomKit customKit = new CustomKit(playerDataFile.getData().getString(uuids + "." + ladder.getRawName() + ".inventory"), playerDataFile.getData().getString(uuids + "." + ladder.getRawName() + ".armor"));
                            playerData.getCustomKitMap().put(ladder, customKit);
                        }
                    }
                }
                System.out.print("Loaded data for " + uuids + ".");
            } catch (Exception ex) {
                System.out.print("Error loading data for " + uuids + ".");
            }
        }
    }

    public void savePlayerData() {
        for (String uuids : playersFile.getData().getStringList("players")) {
            UUID uuid = UUID.fromString(uuids);
            try {
                PlayerData playerData = managerHandler.getPlayerDataManager().getPlayerDataMap().get(uuid);
                if (playerData.isUpdate()) {
                    for (Ladder ladder : managerHandler.getLadderManager().getLadders()) {
                        playerDataFile.getData().set(uuids + "." + ladder.getRawName() + ".elo", playerData.getElo(ladder));
                        if (playerData.getCustomKitMap().containsKey(ladder)) {
                            playerDataFile.getData().set(uuids + "." + ladder.getRawName() + ".inventory", playerData.getCustomKitMap().get(ladder).getRawInventory());
                            playerDataFile.getData().set(uuids + "." + ladder.getRawName() + ".armor", playerData.getCustomKitMap().get(ladder).getRawArmor());
                        } else {
                            playerDataFile.getData().set(uuids + "." + ladder.getRawName() + ".inventory", "none");
                            playerDataFile.getData().set(uuids + "." + ladder.getRawName() + ".armor", "none");
                        }
                        playerDataFile.saveData();
                    }
                    System.out.print("Saved data for " + uuids + ".");
                }
            } catch (Exception ex) {
                System.out.print("Error saving data for " + uuids + ".");
            }
        }
    }

    private void registerRunnables() {
        new ScoreboardRunnable(this).runTaskTimerAsynchronously(this, 0L, 0L);
        new InventoryRunnable(this).runTaskTimerAsynchronously(this, 0L, 0L);
    }

    public static Practice getInstance() {
        return instance;
    }

    public ManagerHandler getManagerHandler() {
        return managerHandler;
    }

    public EntityHider getEntityHider() {
        return entityHider;
    }
}
