package me.gonkas.assassin;

import me.gonkas.assassin.game.Game;
import me.gonkas.assassin.game.Gamestate;
import me.gonkas.assassin.game.Gamestates;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.SPECTATOR);                      // set player to spectator if they die
        Game.TEAMPARTICIPANTS.removePlayer(player);                  // remove player from participants

        // if the assassin failed to kill someone, the death message is changed
        if (event.getDamageSource() == Gamestate.getCustomDamageSource(player)) {event.setDeathMessage("§4" + player.name() + " has failed to kill someone.");}

        Player killer = player.getKiller();
        if (killer == null) {return;}             // check if it was friendly fire or by natural causes

        // check if the assassin killed someone, or if someone killed the assassin
        if (killer == Assassins.ASSASSIN || player == Assassins.ASSASSIN) {Gamestate.huntingToGrace(false); return;}

        killer.damage(Double.MAX_VALUE, player);
        killer.setGameMode(GameMode.SPECTATOR);
        Game.TEAMPARTICIPANTS.removePlayer(killer);                  // remove killer from participants
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        boolean isHoldingTracker = player.getInventory().getItemInMainHand() == Game.TRACKER || player.getInventory().getItemInOffHand() == Game.TRACKER;
        if (event.getAction() != Action.RIGHT_CLICK_AIR || !isHoldingTracker) {return;}
        if (player != Assassins.ASSASSIN || Assassins.GAMESTATE == Gamestates.DUELING) {return;}

        Assassins.TARGET++;
        player.sendMessage("§eTracking §9" + Assassins.getTarget() + "§e.");
    }
}
