package util;

public class Time {
    public static float timeStarted = System.nanoTime(); // Initialize when application starts up

    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
