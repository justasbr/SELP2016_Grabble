package com.example.justas.grabble;

import java.util.List;

public class LeaderboardFeed {
    private List<Player> leaderboard;

    public LeaderboardFeed() {
    }

    public List<Player> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(List<Player> players) {
        this.leaderboard = players;
    }
}
