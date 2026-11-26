package com.example.javafx_test;

/**
 * One row of Champion_Patch_Stats for the Champion Stats table.
 * Plain class with getters so PropertyValueFactory("championName") etc. work,
 * the same way Champion.java is used by the Champions tab.
 */
public class ChampionStats {
    private final int code;
    private final int championCode;
    private final String championName;
    private final String patchNumber;
    private final String lane;
    private final int gamesPlayed;
    private final int wins;
    private final int bans;

    public ChampionStats(int code, int championCode, String championName, String patchNumber,
                         String lane, int gamesPlayed, int wins, int bans) {
        this.code = code;
        this.championCode = championCode;
        this.championName = championName;
        this.patchNumber = patchNumber;
        this.lane = lane;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.bans = bans;
    }

    public int getCode() {
        return code;
    }

    public int getChampionCode() {
        return championCode;
    }

    public String getChampionName() {
        return championName;
    }

    public String getPatchNumber() {
        return patchNumber;
    }

    public String getLane() {
        return lane;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getBans() {
        return bans;
    }

    /** Win rate shown in the table, e.g. "52.3%" ("-" when no games were played). */
    public String getWinRate() {
        return gamesPlayed > 0 ? String.format("%.1f%%", 100.0 * wins / gamesPlayed) : "-";
    }
}
