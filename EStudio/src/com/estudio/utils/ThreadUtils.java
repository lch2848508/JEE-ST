package com.estudio.utils;

public class ThreadUtils {

    public static void sleep(int num) {
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
        }
    }

    public static void sleepSecond(int num) {
        try {
            Thread.sleep(num * 1000);
        } catch (InterruptedException e) {
        }
    }

    public static void sleepMinute(int num) {
        try {
            Thread.sleep(num * 1000 * 60);
        } catch (InterruptedException e) {
        }
    }

    public static void sleepHour(int num) {
        try {
            Thread.sleep(num * 1000 * 60 * 60);
        } catch (InterruptedException e) {
        }
    }
}
