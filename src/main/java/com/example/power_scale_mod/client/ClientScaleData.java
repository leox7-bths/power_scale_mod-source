package com.example.power_scale_mod.client;

import java.util.List;

public class ClientScaleData {
    private static int scaleLevel = 5;
    private static List<String> trackedAttributeIds = List.of();

    public static int getScaleLevel() {
        return scaleLevel;
    }

    public static void setScaleLevel(int level) {
        scaleLevel = level;
    }

    public static List<String> getTrackedAttributeIds() {
        return trackedAttributeIds;
    }

    public static void setTrackedAttributeIds(List<String> ids) {
        trackedAttributeIds = ids;
    }
}
