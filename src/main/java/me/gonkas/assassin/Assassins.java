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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;

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

    public static ArrayList<Player> PRINTTIMERFOR = new ArrayList<>();

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
                    if (TIMER.getTime() > 0) {TIMER.decrementTimer();}
                    else {
                        if (getParticipants().size() < 2) {GAMESTATE = Gamestates.PAUSED;}
                        else if (getParticipants().size() == 2) {Gamestate.graceToDueling();}
                        else {Gamestate.graceToHunting();}
                    }
                }

                case HUNTING -> {
                    if (TIMER.getTime() > 0) {
                        TIMER.decrementTimer();
                        ASSASSIN.setCompassTarget(getTarget().getLocation());
                    } else {Gamestate.huntingToGrace(true);}
                }

                case DUELING -> {
                    Player duelist1 = PARTICIPANTS.get(0);
                    Player duelist2 = PARTICIPANTS.get(1);

                    duelist1.setCompassTarget(duelist2.getLocation());
                    duelist2.setCompassTarget(duelist1.getLocation());
                }

                case PAUSED -> {
                    Game.PARTICIPANTLOCATIONS.forEach(Player::teleport);
                    PARTICIPANTS.forEach(p -> p.addPotionEffects(Game.getPauseEffects()));
                }

                case NOTSTARTED -> {}
            }

            PRINTTIMERFOR.forEach(p -> {p.sendActionBar(TIMER.toString());});

        }, 0, 20);
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
        return PARTICIPANTS.get(TARGET);
    }
}
