package com.example.qradventure.model;


/**
 * Holds data relevant for a player preview
 * Helps custom views display player data
 * TODO: easily extensible if needed!
 */
public class PlayerPreview {
    private String username;
    private String totalScore;
    private int rank;

    /**
     * Constructor
     * @param name
     *      username of the player
     * @param score
     *      total score of the player
     */
    public PlayerPreview(String name, String score, int rank) {
        this.username = name;
        this.totalScore = score;
        this.rank = rank;
    }

    public String getScore() {
        return this.totalScore;
    }

    public String getUsername() {
        return this.username;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int newRank) {
        this.rank = newRank;
    }
}
