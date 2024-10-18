package me.gonkas.assassin;

import me.gonkas.assassin.game.Game;
import me.gonkas.assassin.game.Gamestate;
import me.gonkas.assassin.game.Gamestates;
import org.bukkit.GameMode;
import org.bukkit.damage.DamageSource;
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

        // check if the reason this event was called was an Assassin "getting killed" for failing to kill someone.
        if (deathWasAssassinFail(player, event.getDamageSource())) {event.setDeathMessage("§4" + player.name() + " has failed to kill someone."); return;}

        registerPlayerDeath(player);

        // prevent killer getting punished if there are only 2 players left alive
        if (Assassins.GAMESTATE == Gamestates.DUELING) {return;}

        Player killer = player.getKiller();
        if (killer == null) {return;}             // check if it was friendly fire or by natural causes

        // check if the assassin killed or was killed by someone
        if (assassinKillOrDeath(player, killer)) {Gamestate.huntingToGrace(false); return;}

        // kill the killer if neither were the assassin
        punishKiller(killer);    // automatically registers player death
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // prevents target changing when there are only 2 players alive
        if (Assassins.GAMESTATE != Gamestates.DUELING) {return;}

        // ignores if the player is not holding the tracker or the action was not a right click
        if (!actionWasRightClick(event.getAction()) || !playerIsHoldingTracker(player)) {return;}

        Assassins.TARGET++;    // navigates through the participants
        player.sendMessage("§eTracking §9" + Assassins.getTarget().getName() + "§e.");
    }

    private static boolean deathWasAssassinFail(Player player, DamageSource damageSource) {
        return damageSource == Gamestate.getCustomDamageSource(player);
    }

    private static boolean assassinKillOrDeath(Player killed, Player killer) {
        return killed == Assassins.ASSASSIN || killer == Assassins.ASSASSIN;
    }

    private static boolean playerIsHoldingTracker(Player player) {
        return Game.isTracker(player.getInventory().getItemInMainHand()) || Game.isTracker(player.getInventory().getItemInOffHand());
    }

    private static boolean actionWasRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    private static void registerPlayerDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);                      // set player to spectator if they die
        Assassins.PARTICIPANTS.remove(player);                       // remove player from participants list
        Assassins.TEAMPARTICIPANTS.removePlayer(player);             // remove player from the team made of participants
    }

    private static void punishKiller(Player killer) {
        killer.setHealth(0.0);
        killer.damage(1000, killer);
        registerPlayerDeath(killer);
    }
}
