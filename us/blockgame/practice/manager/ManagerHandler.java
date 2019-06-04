package us.blockgame.practice.manager;

import us.blockgame.practice.Practice;
import us.blockgame.practice.managers.*;

public class ManagerHandler {

    private final Practice plugin;
 //   private SQLManager sqlManager;
    private SettingsManager settingsManager;
    private PlayerManager playerManager;
    private ScoreboardManager scoreboardManager;
    private InventoryManager inventoryManager;
    private LadderManager ladderManager;
    private ArenaManager arenaManager;
    private PlayerDataManager playerDataManager;
    private EventManager eventManager;

    public ManagerHandler(Practice plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
      //  sqlManager = new SQLManager(this);
        playerManager = new PlayerManager(this);
        scoreboardManager = new ScoreboardManager(this);
        inventoryManager = new InventoryManager(this);
        ladderManager = new LadderManager(this);
        arenaManager = new ArenaManager(this);
        playerDataManager = new PlayerDataManager(this);
        settingsManager = new SettingsManager(this);
        eventManager = new EventManager(this);
    }

    public Practice getPlugin() {
        return plugin;
    }

  //  public SQLManager getSQLManager() {
    //    return sqlManager;
    //}

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ScoreboardManager getScoreboardManager() {
         return scoreboardManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public LadderManager getLadderManager() {
        return ladderManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
