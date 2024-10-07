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

public class SetParticipantCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {sender.sendMessage("§cInvalid arguments. This command requires syntax \"/setparticipant <player>\""); return true;}

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {sender.sendMessage("§cPlayer not found."); return true;}

        if (Assassins.PARTICIPANTS.contains(target) && Assassins.ASSASSIN != target) {sender.sendMessage("§cPlayer is already a participant!"); return true;}

        // if the player is an assassin, must already be a participant, simply update assassin | else add to participants
        if (Assassins.ASSASSIN == target) {
            boolean updateAssassin = Assassins.GAMESTATE != Gamestates.GRACE;
            Game.rollNewAssassin(updateAssassin);
        } else {
            Assassins.PARTICIPANTS.add(target);}

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {return List.of();}

        // returns a list containing names of the Assassin and all non-participants
        return Bukkit.getOnlinePlayers().stream().filter(p -> p == Assassins.ASSASSIN || !Assassins.PARTICIPANTS.contains(p)).map(Player::getName).toList();
    }
}
