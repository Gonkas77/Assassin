package me.gonkas.assassin;

import me.gonkas.assassin.commands.*;
import me.gonkas.assassin.game.Game;
import me.gonkas.assassin.game.Gamestate;
import me.gonkas.assassin.game.Gamestates;
import me.gonkas.assassin.timer.Timer;
import me.gonkas.assassin.timer.Timers;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;

public final class Assassins extends JavaPlugin {

    public static ConsoleCommandSender CONSOLE;

    public static Gamestates GAMESTATE = Gamestates.NOTSTARTED;
    public static Gamestates STATEBEFOREPAUSE = Gamestates.NOTSTARTED;

    public static ArrayList<World> WORLDS;
    public static ScoreboardManager SCOREBOARDMANAGER;
    public static Scoreboard SCOREBOARD;

    public static ArrayList<Player> PARTICIPANTS = new ArrayList<>();
    public static Player ASSASSIN = null;
    public static int TARGET = 0; // targets are obtained from "Participants" using an index

    public static Timer TIMER = new Timer(Timers.GRACE);
    public static ItemStack TRACKER = Game.createTracker();

    public static ArrayList<Player> PRINTTIMERFOR = new ArrayList<>();

    public static Team TEAMPARTICIPANTS;
    public static HashMap<Player, Location> PARTICIPANTLOCATIONS = new HashMap<>();
    public static HashMap<GameRule<Boolean>, Boolean> PAUSEGAMERULES = Game.getPausedRules();

    @Override
    public void onEnable() {

        CONSOLE = Bukkit.getConsoleSender();

        WORLDS = (ArrayList<World>) Bukkit.getWorlds();
        SCOREBOARDMANAGER = Bukkit.getScoreboardManager();
        SCOREBOARD = SCOREBOARDMANAGER.getNewScoreboard();

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        getCommand("startgame").setExecutor(new StartGameCommand());
        getCommand("stopgame").setExecutor(new StopGameCommand());
        getCommand("pausegame").setExecutor(new PauseGameCommand());
        getCommand("resumegame").setExecutor(new ResumeGameCommand());

        getCommand("setassassin").setExecutor(new SetAssassinCommand());
        getCommand("setparticipant").setExecutor(new SetParticipantCommand());
        getCommand("removeparticipant").setExecutor(new RemoveParticipantCommand());

        getCommand("getassassin").setExecutor(new GetAssassinCommand());
        getCommand("getparticipants").setExecutor(new ParticipantListCommand());

        getCommand("printtimers").setExecutor(new PrintTimersCommand());
        getCommand("decrementtimer").setExecutor(new DecrementTimerCommand());


        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            switch (GAMESTATE) {

                case GRACE -> {
                    if (TIMER.getTime() > 0) {TIMER.decrementTimer(true);}
                    else {
                        if (PARTICIPANTS.size() == 2) {Gamestate.graceToDueling();}
                        else if (PARTICIPANTS.size() == 1) {Gamestate.graceToVictory();}
                        else {Gamestate.graceToHunting();}
                    }
                }

                case HUNTING -> {
                    if (TIMER.getTime() > 0) {TIMER.decrementTimer(true);}
                    else {Gamestate.huntingToGrace(true);}
                }

                case DUELING -> {
                    if (PARTICIPANTS.size() == 1) {Gamestate.duelingToVictory();}
                }

                case PAUSED, NOTSTARTED, VICTORY -> {}

            }

            PRINTTIMERFOR.forEach(p -> p.sendActionBar(TIMER.toString()));

        }, 0, 20);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            switch (GAMESTATE) {

                case HUNTING -> ASSASSIN.setCompassTarget(getTarget().getLocation());

                case DUELING -> {
                    Player duelist1 = PARTICIPANTS.get(0);
                    Player duelist2 = PARTICIPANTS.get(1);

                    duelist1.setCompassTarget(duelist2.getLocation());
                    duelist2.setCompassTarget(duelist1.getLocation());
                }

                case PAUSED -> {
                    PARTICIPANTLOCATIONS.forEach(Player::teleport);
                    PARTICIPANTS.forEach(p -> p.addPotionEffects(Game.getPauseEffects()));
                }

                case VICTORY -> {
                    if (TIMER.getTime() > 0) {    // timer runs on ticks, not seconds
                        TIMER.decrementTimer(false);
                        TIMER.spawnVictoryFireworks(PARTICIPANTS.getFirst());
                    } else {Game.stopGame();}
                }

                case GRACE, NOTSTARTED -> {}
            }

        }, 0, 1);
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void sendServerChat(String msg) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(msg));
    }

    public static void playServerSound(Sound sound, SoundCategory category, float volume, float pitch) {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p, sound, category, volume, pitch));
    }

    public static ArrayList<Player> getParticipants() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeIf(p -> p.getGameMode() != GameMode.SURVIVAL);
        return players;
    }

    public static Player getTarget() {
        if (TARGET >= PARTICIPANTS.size()) {TARGET = 0;}
        if (Assassins.PARTICIPANTS.get(Assassins.TARGET) == Assassins.ASSASSIN) {Assassins.TARGET++;}
        return PARTICIPANTS.get(TARGET);
    }
}
