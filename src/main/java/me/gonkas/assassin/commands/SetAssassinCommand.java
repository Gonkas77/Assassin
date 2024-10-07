package me.gonkas.assassin.commands;

import me.gonkas.assassin.Assassins;
import me.gonkas.assassin.game.Game;
import me.gonkas.assassin.game.Gamestates;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetAssassinCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {sender.sendMessage("§cInvalid arguments. This command requires syntax \"/setassassin <player>\""); return true;}

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {sender.sendMessage("§cPlayer not found."); return true;}

        if (Assassins.ASSASSIN == target) {sender.sendMessage("§cPlayer is already the Assassin!"); return true;}

        // check if the player was not a participant
        if (!Assassins.PARTICIPANTS.contains(target)) {
            Assassins.PARTICIPANTS.add(target);}

        boolean updateAssassin = Assassins.GAMESTATE != Gamestates.GRACE;
        Game.rollNewAssassin(target, updateAssassin);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {return List.of();}

        // returns a list with all participants except the assassin
        return Bukkit.getOnlinePlayers().stream().filter(p -> Assassins.PARTICIPANTS.contains(p) && Assassins.ASSASSIN != p).map(Player::getName).toList();
    }
}
