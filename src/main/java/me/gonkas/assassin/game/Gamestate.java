package me.gonkas.assassin.game;

import me.gonkas.assassin.Assassins;
import me.gonkas.assassin.timer.Timer;
import me.gonkas.assassin.timer.Timers;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

import static me.gonkas.assassin.game.Game.*;

public class Gamestate {

    public static void graceToHunting() {

        updateAssassin();

        Assassins.TIMER = new Timer(Timers.HUNT);

        TEAMPARTICIPANTS.setAllowFriendlyFire(true);
        Assassins.GAMESTATE = Gamestates.HUNTING;
    }

    public static void graceToDueling() {

        Assassins.sendServerChat("§eThere are now only 2 players left! Who will be the last player standing?");
        Assassins.PARTICIPANTS.forEach(p -> {
                    p.sendMessage("§4Kill your final opponent.");
                    p.getInventory().addItem(TRACKER);
        });

        TEAMPARTICIPANTS.setAllowFriendlyFire(true);
        Assassins.GAMESTATE = Gamestates.DUELING;
    }

    public static void huntingToGrace(boolean killAssassin) {
        if (killAssassin) {Assassins.ASSASSIN.damage(Double.MAX_VALUE, getCustomDamageSource(Assassins.ASSASSIN));}

        Assassins.sendServerChat("§cThis round the Assassin was §4" + Assassins.ASSASSIN.name() + "§c.");

        Assassins.TIMER = new Timer(Timers.GRACE);
        Game.rollNewAssassin(false);

        TEAMPARTICIPANTS.setAllowFriendlyFire(false);
        Assassins.GAMESTATE = Gamestates.GRACE;
    }

    public static DamageSource getCustomDamageSource(Player player) {
        return DamageSource.builder(DamageType.THORNS).withCausingEntity(player).build();
    }
}
