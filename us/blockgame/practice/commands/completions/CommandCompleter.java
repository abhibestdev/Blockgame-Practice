package us.blockgame.practice.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import us.blockgame.practice.Practice;

import java.util.ArrayList;
import java.util.List;

public class CommandCompleter implements TabCompleter {

    private Practice plugin;

    public CommandCompleter(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        ArrayList<String> completer = new ArrayList<>();
        if (args[args.length - 1] == null) {
            for (Player all : this.plugin.getServer().getOnlinePlayers()) {
                if (!completer.contains(all.getName())) {
                    completer.add(all.getName());
                }
            }
        } else {
            for (Player all : this.plugin.getServer().getOnlinePlayers()) {
                if (all.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()) && !completer.contains(all.getName())) {
                    completer.add(all.getName());
                }
            }
        }
        return completer;
    }
}
