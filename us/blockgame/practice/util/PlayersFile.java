package us.blockgame.practice.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PlayersFile {

    private PlayersFile() {
    }

    static PlayersFile instance = new PlayersFile();

    public static PlayersFile getInstance() {
        return instance;
    }

    Plugin p;
    FileConfiguration data;
    File dfile;

    public void setup(Plugin p) {
        dfile = new File(p.getDataFolder(), "players.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create players.yml!");
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
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save players.yml!");
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public PluginDescriptionFile getDesc() {
        return p.getDescription();

    }
}