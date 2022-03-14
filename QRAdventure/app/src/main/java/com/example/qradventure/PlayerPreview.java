package com.example.qradventure;


/**
 * Holds data relevant for a player preview
 * Helps custom views display player data
 * TODO: easily extensible if needed!
 */
public class PlayerPreview {
    private String username;
    private Long totalScore;

    public PlayerPreview(String name, Long score) {
        this.username = name;
        this.totalScore = score;
    }

    public Long getScore() {
        return this.totalScore;
    }

    public String getUsername() {
        return this.username;
    }
}
