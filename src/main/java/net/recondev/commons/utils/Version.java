package net.recondev.commons.utils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public enum Version {
    TOO_OLD(-1),
    v1_7_R1(171),
    v1_7_R2(172),
    v1_7_R3(173),
    v1_7_R4(174),
    v1_8_R1(181),
    v1_8_R2(182),
    v1_8_R3(183),
    v1_9_R1(191),
    v1_9_R2(192),
    v1_10_R1(1101),
    v1_11_R1(1111),
    v1_12_R1(1121),
    v1_13_R2(1132),
    v1_14_R1(1141),
    v1_15_R1(1151),
    v1_16_R1(1161),
    v1_16_R2(1162),
    v1_16_R3(1163),
    v1_17_R1(1171),
    v1_18_R1(1181),
    v1_18_R2(1182),
    v1_19_R1(1191),
    v1_19_R2(1192),
    v1_19_R3(1193),
    v1_19_R4(1194),
    v1_20_R1(1201),
    v1_20_R3(1203),


    TOO_NEW(-2);

    private static Version currentVersion;

    private static Version latest;

    private static final Map<Integer, Version> versionMap;

    private int versionInteger;

    static {
        versionMap = new HashMap<>();
        for (Version version : values())
            versionMap.put(Integer.valueOf(version.getVersionInteger()), version);
    }

    Version(int versionInteger) {
        this.versionInteger = versionInteger;
    }

    public static Version getCurrentVersion() {
        if (currentVersion == null) {
            String ver = Bukkit.getServer().getClass().getPackage().getName();
            int v = Integer.parseInt(ver.substring(ver.lastIndexOf('.') + 1).replace("_", "").replace("R", "").replace("v", ""));
            currentVersion = versionMap.getOrDefault(Integer.valueOf(v), TOO_NEW);
            if (v > getLatestVersion().getVersionInteger())
                currentVersion = getLatestVersion();
        }
        return currentVersion;
    }

    public static Version getLatestVersion() {
        if (latest == null) {
            Version v = TOO_OLD;
            for (Version version : values()) {
                if (version.comparedTo(v) == ComparisonResult.NEWER)
                    v = version;
            }
            latest = v;
        }
        return latest;
    }

    public int getVersionInteger() {
        return this.versionInteger;
    }

    public ComparisonResult comparedTo(Version version) {
        int current = getVersionInteger();
        int check = version.getVersionInteger();
        if (current > check || check == -2)
            return ComparisonResult.NEWER;
        if (current == check)
            return ComparisonResult.SAME;
        return ComparisonResult.OLDER;
    }

    public static boolean isNewer(Version version) {
        if (currentVersion == null)
            getCurrentVersion();
        return (currentVersion.versionInteger > version.versionInteger || currentVersion.versionInteger == -2);
    }

    public static boolean isSame(Version version) {
        if (currentVersion == null)
            getCurrentVersion();
        return (currentVersion.versionInteger == version.versionInteger);
    }

    public static boolean isOlder(Version version) {
        if (currentVersion == null)
            getCurrentVersion();
        return (currentVersion.versionInteger < version.versionInteger || currentVersion.versionInteger == -1);
    }

    public enum ComparisonResult {
        NEWER, SAME, OLDER;
    }
}

