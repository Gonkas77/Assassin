package me.gonkas.assassin.timer;

import me.gonkas.assassin.Assassins;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class Timer {

    private final String name;
    private int time;
    private final String msg;

    public Timer(Timers timer) {
        this.name = timer.name();
        this.time = timer.time;
        this.msg = timer.msg;
    }

    public void decrementTimer(boolean announceNewTime) {
        this.time--;
        if (announceNewTime) {announceTime();}
    }

    public void decrementTimer(int time) {
        this.time -= time;
        announceTime();
        if (this.time < 0) {endTimer();}
    }

    public void spawnVictoryFireworks(Player player) {
        switch (this.time) {
            case 0,10,20,30,40,50,60,70,80,90 -> spawnFirework(player);
        }
    }

    private void announceTime() {
        switch (this.time) {
            case 1,2,3,4,5,10,30,60 -> {
                Assassins.sendServerChat("§eThe §l" + this.name + "§r§e Period ends in §f" + this.time + "§e seconds.");
                Assassins.playServerSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 0.7f);
            } case 0 -> endTimer();
        }
    }

    private void endTimer() {
        Assassins.sendServerChat("§cThe §l" + this.name + "§r§c Period has ended! " + this.msg);
        Assassins.playServerSound(Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 0.7f);
    }

    private void spawnFirework(Player player) {
        World world = player.getWorld();
        world.spawn()
    }

    private void getRandomFireworkLocation(Player player) {
        Location origin = player.getLocation();
        Location[] possibleLocations = new Location[8];

        for (int i=0; i < 8; i++) {
            double yaw = origin.getDirection().getX();
        }

        Random random = new Random();
    }

    private int getCardinalDirection(double yaw) {
        int[] directions = {0, 90, 180, -90};
        double[] distances = new double[4];

        for (int i=0; i < 4; i++) {distances[i] = Math.abs(yaw - directions[i]);}

        int indiceMenorValor = 0;
        for (int i=0; i < 4; i++) {
            if (distances[i] < distances[indiceMenorValor]) {}
        }
    }

    public String getName() {return name;}
    public int getTime() {return time;}

    @Override
    public String toString() {return this.name + ": " + this.time;}
}
