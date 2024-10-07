package me.gonkas.assassin.commands;

import me.gonkas.assassin.Assassins;
import me.gonkas.assassin.game.Game;
import me.gonkas.assassin.game.Gamestates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResumeGameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (Assassins.GAMESTATE != Gamestates.PAUSED && Assassins.GAMESTATE != Gamestates.NOTSTARTED) {commandSender.sendMessage("The game is already ongoing!"); return true;}
        Game.resumeGame();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
