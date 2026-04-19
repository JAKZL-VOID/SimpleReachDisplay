package com.jakzl.simplereachdisplay;

public class ReachDisplayState {
    public static double lastHitDistance = 0.0;
    public static long lastHitTime = 0L; // System.currentTimeMillis()

    public static void recordHit(double distance) {
        lastHitDistance = distance;
        lastHitTime = System.currentTimeMillis();
    }

    public static boolean isActive(ModConfig config) {
        if (!config.enabled) return false;
        if (lastHitTime == 0L) return false;
        long elapsed = System.currentTimeMillis() - lastHitTime;
        return elapsed < (long)(config.displayDuration * 1000);
    }

    /**
     * Returns 0.0 - 1.0 opacity based on fade, or 1.0 if fade disabled.
     */
    public static float getOpacity(ModConfig config) {
        if (!config.fadeOut) return 1.0f;
        long elapsed = System.currentTimeMillis() - lastHitTime;
        long total = (long)(config.displayDuration * 1000);
        long fadeStart = (long)(total * 0.5); // start fading at 50% of duration
        if (elapsed < fadeStart) return 1.0f;
        float fadeProgress = (float)(elapsed - fadeStart) / (float)(total - fadeStart);
        return Math.max(0f, 1.0f - fadeProgress);
    }
}
