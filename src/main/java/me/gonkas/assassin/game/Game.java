package me.gonkas.assassin.game;

import me.gonkas.assassin.Assassins;
import me.gonkas.assassin.timer.Timer;
import me.gonkas.assassin.timer.Timers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game {

    public static HashMap<World, HashMap<GameRule<Boolean>, Boolean>> BOOLEANRULES = new HashMap<>();
    public static HashMap<World, HashMap<GameRule<Integer>, Integer>> INTEGERRULES = new HashMap<>();

    public static void startGame() {

        Assassins.TEAMPARTICIPANTS = Assassins.SCOREBOARD.registerNewTeam("Participants");
        Assassins.PARTICIPANTS = Assassins.getParticipants();

        Assassins.PARTICIPANTS.forEach(p -> Assassins.TEAMPARTICIPANTS.addPlayer(p)); // put all participants on a team
        Assassins.TEAMPARTICIPANTS.setAllowFriendlyFire(false);

        Game.rollNewAssassin(false);
        Assassins.sendServerChat("§eThe Assassins game has started. You have 5 minutes of Grace until an Assassin is selected.");
        Assassins.GAMESTATE = Gamestates.GRACE;
    }

    public static void stopGame() {

        Assassins.GAMESTATE = Gamestates.NOTSTARTED;

        Assassins.PARTICIPANTS = new ArrayList<>();
        Assassins.ASSASSIN = null;
        Assassins.TARGET = 0;

        Assassins.TIMER = new Timer(Timers.GRACE);

        Assassins.WORLDS = (ArrayList<World>) Bukkit.getWorlds();
        Assassins.SCOREBOARDMANAGER = Bukkit.getScoreboardManager();
        Assassins.SCOREBOARD = Assassins.SCOREBOARDMANAGER.getNewScoreboard();

        Assassins.sendServerChat("§cStopped Assassins.");
    }

    public static void pauseGame() {

        Assassins.WORLDS.forEach(w -> {
            BOOLEANRULES.put(w, getBooleanGameRules(w));
            INTEGERRULES.put(w, getIntegerGameRules(w));

            Assassins.PAUSEGAMERULES.forEach((w::setGameRule));
            w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        });

        Assassins.PARTICIPANTS.forEach(p -> Assassins.PARTICIPANTLOCATIONS.put(p, p.getLocation()));
        Assassins.TEAMPARTICIPANTS.setAllowFriendlyFire(false);

        Assassins.sendServerChat("§aThe game has been paused! Please wait while the Hosts handle the situation.");
        Assassins.STATEBEFOREPAUSE = Assassins.GAMESTATE;
        Assassins.GAMESTATE = Gamestates.PAUSED;
    }

    public static void resumeGame() {

        BOOLEANRULES.forEach((w, e) -> e.forEach(w::setGameRule));
        INTEGERRULES.forEach((w, e) -> e.forEach(w::setGameRule));

        Assassins.TEAMPARTICIPANTS.setAllowFriendlyFire(true);

        Assassins.sendServerChat("§aThe game has been resumed.");
        Assassins.GAMESTATE = Assassins.STATEBEFOREPAUSE;
    }

    public static void rollNewAssassin(boolean updateAssassin) {
        Player lastAssassin = Assassins.ASSASSIN;

        ArrayList<Player> participants = new ArrayList<>(Assassins.getParticipants());
        if (lastAssassin != null) {
            lastAssassin.getInventory().remove(createTracker());
            participants.remove(lastAssassin);
        }

        Assassins.ASSASSIN = participants.get((new Random()).nextInt(participants.size()));        // choosing new assassin

        // used only when an admin forces an assassin into a participant **AND** if it is not grace period
        if (updateAssassin) {updateAssassin();}
    }

    // forces a player as an assassin
    public static void rollNewAssassin(Player player, boolean updateAssassin) {
        Assassins.ASSASSIN.getInventory().remove(Assassins.TRACKER);
        Assassins.ASSASSIN = player;

        // updates only if it is not grace period
        if (updateAssassin) {updateAssassin();}
    }

    public static void updateAssassin() {
        Assassins.ASSASSIN.sendTitle("§cAssassin!", "Kill someone within 15 minutes.", 10, 30, 10);
        Assassins.ASSASSIN.sendMessage("§cYou are the new Assassin! Kill someone within 15 minutes.");
        Assassins.ASSASSIN.playSound(
                Assassins.ASSASSIN,
                Sound.ENTITY_WITHER_SPAWN,
                SoundCategory.MASTER,
                0.8f,
                1.0f
        );
        Assassins.ASSASSIN.getInventory().addItem(Assassins.TRACKER);    // give tracker to assassin
    }

    public static ItemStack createTracker() {
        ItemStack tracker = ItemStack.of(Material.COMPASS);
        ItemMeta meta = tracker.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.setItemName("Tracker");

        tracker.setItemMeta(meta);
        return tracker;
    }

    public static boolean isTracker(ItemStack item) {
        if (item == null || !item.getItemMeta().hasEnchantmentGlintOverride()) {return false;}
        return item.getType() == Material.COMPASS && item.getItemMeta().getEnchantmentGlintOverride();
    }

    public static List<PotionEffect> getPauseEffects() {
        return List.of(
                new PotionEffect(PotionEffectType.BLINDNESS, 60, 255, true, false),
                new PotionEffect(PotionEffectType.RESISTANCE, 30, 255, true, false),
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30, 255, true, false)
        );
    }

    private static HashMap<GameRule<Boolean>, Boolean> getBooleanGameRules(World world) {
        HashMap<GameRule<Boolean>, Boolean> gameRules = new HashMap<>();
        for (String rule : world.getGameRules()) {
            if (GameRule.getByName(rule).getType() == Boolean.class) {
                gameRules.put((GameRule<Boolean>) GameRule.getByName(rule), (Boolean) world.getGameRuleValue(GameRule.getByName(rule)));
            }
        } return gameRules;
    }

    private static HashMap<GameRule<Integer>, Integer> getIntegerGameRules(World world) {
        HashMap<GameRule<Integer>, Integer> gameRules = new HashMap<>();
        for (String rule : world.getGameRules()) {
            if (GameRule.getByName(rule).getType() == Integer.class) {
                gameRules.put((GameRule<Integer>) GameRule.getByName(rule), (Integer) world.getGameRuleValue(GameRule.getByName(rule)));
            }
        } return gameRules;
    }

    public static HashMap<GameRule<Boolean>, Boolean> getPausedRules() {
        HashMap<GameRule<Boolean>, Boolean> rules = new HashMap<>();
        rules.put(GameRule.DO_DAYLIGHT_CYCLE,              false);
        rules.put(GameRule.DO_FIRE_TICK,                   false);
        rules.put(GameRule.DO_INSOMNIA,                    false);
        rules.put(GameRule.DO_MOB_LOOT,                    false);
        rules.put(GameRule.DO_MOB_SPAWNING,                false);
        rules.put(GameRule.DO_TRADER_SPAWNING,             false);
        rules.put(GameRule.DO_WARDEN_SPAWNING,             false);
        rules.put(GameRule.DO_WEATHER_CYCLE,               false);
        rules.put(GameRule.DROWNING_DAMAGE,                false);
        rules.put(GameRule.FALL_DAMAGE,                    false);
        rules.put(GameRule.FIRE_DAMAGE,                    false);
        rules.put(GameRule.KEEP_INVENTORY,                 true);
        rules.put(GameRule.MOB_GRIEFING,                   false);
        rules.put(GameRule.PROJECTILES_CAN_BREAK_BLOCKS,   false);
        return rules;
    }
}
