package me.gonkas.assassin.game;

import me.gonkas.assassin.Assassins;
import me.gonkas.assassin.timer.Timer;
import me.gonkas.assassin.timer.Timers;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

import static me.gonkas.assassin.game.Game.*;

public class Gamestate {

    public static void graceToHunting() {

        updateAssassin();

        Assassins.TIMER = new Timer(Timers.HUNT);

        Assassins.GAMESTATE = Gamestates.HUNTING;
    }

    public static void graceToDueling() {

        Assassins.sendServerChat("§cThere are now only 2 players left! Who will be the last player standing?");
        Assassins.PARTICIPANTS.forEach(p -> {
                    p.sendMessage("§4Kill your final opponent.");
                    p.playSound(
                            p,
                            Sound.ITEM_TRIDENT_THUNDER,
                            SoundCategory.MASTER,
                            0.8f,
                            1.2f
                    );
                    p.getInventory().addItem(Assassins.TRACKER);
        });

        Assassins.GAMESTATE = Gamestates.DUELING;
    }

    public static void huntingToGrace(boolean killAssassin) {
        if (killAssassin) {Assassins.ASSASSIN.damage(1000, getCustomDamageSource(Assassins.ASSASSIN));}
        else {Assassins.TIMER.endTimer();} // only forcibly end the timer if the assassin killed someone

        Assassins.sendServerChat("§cThis round the Assassin was §4" + Assassins.ASSASSIN.getName() + "§c.");

        Assassins.TIMER = new Timer(Timers.GRACE);
        Game.rollNewAssassin(false);

        Assassins.GAMESTATE = Gamestates.GRACE;
    }

    public static void graceToVictory() {

        Assassins.sendServerChat("§aPlayer §2" + Assassins.PARTICIPANTS.getFirst().getName() + "§a is the last one standing!");

        Assassins.TIMER = new Timer(Timers.VICTORY);
        Assassins.GAMESTATE = Gamestates.VICTORY;
    }

    public static void duelingToVictory() {
        graceToVictory();
    }

    public static DamageSource getCustomDamageSource(Player player) {
        return DamageSource.builder(DamageType.THORNS).withCausingEntity(player).withDirectEntity(player).build();
    }
}
