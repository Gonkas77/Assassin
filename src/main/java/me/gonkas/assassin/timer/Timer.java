package me.gonkas.assassin.timer;

import me.gonkas.assassin.Assassins;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class Timer {

    private final String name;
    private int time;
    private final String msg;

    public Timer(Timers timer) {
        this.name = timer.name();
        this.time = timer.time;
        this.msg = timer.msg;
    }

    public void decrementTimer() {
        this.time--;
        announceTime();
    }

    public void decrementTimer(int time) {
        this.time -= time;
        announceTime();
        if (this.time < 0) {endTimer();}
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

    public String getName() {return name;}
    public int getTime() {return time;}

    @Override
    public String toString() {return this.name + ": " + this.time;}
}
