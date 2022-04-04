package com.example.qradventure;


/**
 * Holds data relevant for a player preview
 * Helps custom views display player data
 * TODO: easily extensible if needed!
 */
public class PlayerPreview {
    private String username;
    private String totalScore;
    private int profilePicIndex;
    private int rank;

    /**
     * Constructor
     * @param name
     *      username of the player
     * @param score
     *      total score of the player
     * @param rank
     *      ranking in leaderboard
     */
    public PlayerPreview(String name, String score, int rank, int profilePicIndex) {
        this.username = name;
        this.totalScore = score;
        this.rank = rank;
        this.profilePicIndex = profilePicIndex;

    }


     /*
     If no profile picture is provided, give default profile
     index as 0
     */

    /**
     * Constructor2
     * @param name
     *      username of the player
     * @param score
     *      total score of the player
     * @param rank
     *      ranking in leaderboard
     */
    public PlayerPreview(String name, String score, int rank) {
        this.username = name;
        this.totalScore = score;
        this.rank = rank;
        this.profilePicIndex = 0;
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

    public int getProfilePicIndex() {
        return this.profilePicIndex;
    }

    public void setRank(int newRank) {
        this.rank = newRank;
    }
}
