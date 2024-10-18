package me.gonkas.assassin.timer;

import me.gonkas.assassin.Assassins;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

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
        player.getWorld().spawn(
                getRandomFireworkLocation(player),
                Firework.class,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                (it) -> it.setFireworkMeta(getRandomFireworkMeta(it.getFireworkMeta()))
                );
    }

    private Location getRandomFireworkLocation(Player player) {
        int offset = (new Random()).nextInt(8);
        return getNextPosition(player.getLocation(), offset);
    }

    private Location getNextPosition(Location origin, int offset) {
        int[] directions = {0, 90, 180, -90};
        double[] distances = new double[4];

        double yaw = origin.getDirection().getX();
        yaw += 45*offset;

        for (int i=0; i < 4; i++) {distances[i] = yaw - Math.abs(directions[i]);}

        int indiceMenorValor = 0;
        for (int i=0; i < 4; i++) {if (Math.abs(distances[i]) < Math.abs(distances[indiceMenorValor])) {indiceMenorValor = i;}}

        double angle = distances[indiceMenorValor];
        double adjacentSide = Math.cos(angle)*3;
        double oppositeSide = Math.sin(angle)*3;

        Vector vector = new Vector();

        // Z axis else X axis
        if (directions[indiceMenorValor] == 0 || directions[indiceMenorValor] == 180) {
            vector.setZ(adjacentSide);
            vector.setX(oppositeSide);
        } else {
            vector.setX(adjacentSide);
            vector.setZ(oppositeSide);
        }

        return origin.add(vector);
    }

    private FireworkMeta getRandomFireworkMeta(FireworkMeta meta) {
        meta.addEffect(getRandomFireworkEffect());
        meta.setPower((new Random()).nextInt(10, 30));
        return meta;
    }

    private FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.with(getRandomFireworkType());
        builder.withColor(getRandomFireworkColors());
        builder.withFade(getRandomFireworkColors());

        Random random = new Random();
        if (random.nextBoolean()) {builder.withFlicker();}
        if (random.nextBoolean()) {builder.withTrail();}

        return builder.build();
    }

    private FireworkEffect.Type getRandomFireworkType() {
        return FireworkEffect.Type.values()[(new Random()).nextInt(4)];
    }

    private Color[] getRandomFireworkColors() {
        Random random = new Random();
        int numberOfColors = (int) random.nextGaussian(2, 1.5);

        Color[] colors = new Color[numberOfColors];
        for (int i=0; i < numberOfColors; i++) {
            switch (random.nextInt(numberOfColors)) {
                case 0 -> colors[i] = DyeColor.BLACK.getColor();
                case 1 -> colors[i] = DyeColor.BLUE.getColor();
                case 2 -> colors[i] = DyeColor.BROWN.getColor();
                case 3 -> colors[i] = DyeColor.CYAN.getColor();
                case 4 -> colors[i] = DyeColor.GRAY.getColor();
                case 5 -> colors[i] = DyeColor.GREEN.getColor();
                case 6 -> colors[i] = DyeColor.LIGHT_BLUE.getColor();
                case 7 -> colors[i] = DyeColor.LIGHT_GRAY.getColor();
                case 8 -> colors[i] = DyeColor.LIME.getColor();
                case 9 -> colors[i] = DyeColor.MAGENTA.getColor();
                case 10 -> colors[i] = DyeColor.ORANGE.getColor();
                case 11 -> colors[i] = DyeColor.PINK.getColor();
                case 12 -> colors[i] = DyeColor.PURPLE.getColor();
                case 13 -> colors[i] = DyeColor.RED.getColor();
                case 14 -> colors[i] = DyeColor.YELLOW.getColor();
                case 15 -> colors[i] = DyeColor.WHITE.getColor();
            }
        } return colors;
    }

    public String getName() {return name;}
    public int getTime() {return time;}

    @Override
    public String toString() {return this.name + ": " + this.time;}
}
