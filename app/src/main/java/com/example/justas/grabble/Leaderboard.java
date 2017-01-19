package com.example.justas.grabble;

class Leaderboard {
    public static final int TODAY = 0;
    public static final int ALL_TIME = 1;

    public static final String PATH_TODAY = "today";
    public static final String PATH_ALL_TIME = "";

    public static int count() {
        return 2;
    }

    public static String titleOf(int index) {
        if (index == Leaderboard.TODAY) {
            return "Today";
        } else if (index == Leaderboard.ALL_TIME) {
            return "All-time";
        }
        return null;
    }

    public static String getPathParameter(int index) {
        if (index == Leaderboard.TODAY) {
            return PATH_TODAY;
        } else if (index == Leaderboard.ALL_TIME) {
            return PATH_ALL_TIME;
        }
        return "";
    }
}