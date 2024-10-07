package me.gonkas.assassin.commands;

import me.gonkas.assassin.Assassins;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrintTimersCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {commandSender.sendMessage("§cOnly player may use this command!"); return true;}

        if (Assassins.PRINTTIMERFOR.contains((Player) commandSender)) {Assassins.PRINTTIMERFOR.remove((Player) commandSender);}
        else {Assassins.PRINTTIMERFOR.add((Player) commandSender);}

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
