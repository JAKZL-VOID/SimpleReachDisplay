package com.jakzl.simplereachdisplay;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "simplereachdisplay")
public class ModConfig implements ConfigData {

    // General
    public boolean enabled = true;

    public enum TargetFilter {
        ANY_ENTITY,
        PLAYERS_ONLY,
        HOSTILE_MOBS_ONLY,
        PLAYERS_AND_HOSTILE,
        PASSIVE_MOBS_ONLY
    }

    public TargetFilter targetFilter = TargetFilter.ANY_ENTITY;

    public float displayDuration = 1.5f; // seconds
    public boolean fadeOut = true;

    // Appearance
    public enum TextSize {
        SMALL, MEDIUM, LARGE
    }

    public TextSize textSize = TextSize.MEDIUM;
    public boolean bold = false;
    public boolean italic = false;
    public boolean showBackground = false;
    public float backgroundOpacity = 0.5f;
    public int backgroundPadding = 3;
    public boolean showUnit = false; // show "blocks" suffix

    // Colors
    public boolean dynamicColor = true;

    // Dynamic color thresholds (reach distance)
    public float greenMinDistance = 2.75f;
    public float greenMaxDistance = 3.0f;
    public float orangeMinDistance = 2.0f;
    public float orangeMaxDistance = 2.75f;
    // below orangeMinDistance = red

    public int greenColor = 0x55FF55;
    public int orangeColor = 0xFFAA00;
    public int redColor = 0xFF5555;
    public int staticColor = 0xFFFFFF;

    // HUD Position
    public float hudX = -1f; // -1 means "default under crosshair"
    public float hudY = -1f;
    public boolean useDefaultPosition = true; // centered under crosshair

    // Decimal places
    public int decimalPlaces = 2;
}
