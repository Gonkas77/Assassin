package me.gonkas.assassin.timer;

public enum Timers {
    GRACE(300, "§eThe Assassin is out! Survive for 15 minutes."),
    HUNT(900, "§eYou have 5 minutes of Grace. Don't waste them!");

    final int time;
    final String msg;

    Timers(int i, String s) {time = i; msg = s;}
}