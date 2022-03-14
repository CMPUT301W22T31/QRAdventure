package com.example.qradventure;


/**
 * Holds data relevant for a player preview
 * Helps custom views display player data
 * TODO: easily extensible if needed!
 */
public class PlayerPreview {
    private String username;
    private String totalScore;

    /**
     * Constructor
     * @param name
     *      username of the player
     * @param score
     *      total score of the player
     */
    public PlayerPreview(String name, String score) {
        this.username = name;
        this.totalScore = score;
    }

    public String getScore() {
        return this.totalScore;
    }

    public String getUsername() {
        return this.username;
    }
}
