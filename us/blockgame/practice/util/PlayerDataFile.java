package us.blockgame.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;

public class PlayerDataFile {

    private PlayerDataFile() {
    }

    static PlayerDataFile instance = new PlayerDataFile();

    public static PlayerDataFile getInstance() {
        return instance;
    }

    Plugin p;
    FileConfiguration data;
    File dfile;

    public void setup(Plugin p) {
        dfile = new File(p.getDataFolder(), "playerdata.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create playerdata.yml!");
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getData() {
        return data;
    }

    public void saveData() {
        try {
            data.save(dfile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save playerdata.yml!");
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public PluginDescriptionFile getDesc() {
        return p.getDescription();

    }
}