package game;

import java.io.Serializable;

public class ScoreCard implements Serializable {
    private static final long serialVersionUID = 5603164654576349379L;

    private Integer gameId;
    private String category;
    private Integer score;

    public ScoreCard() {
    }

    public ScoreCard(String category, Integer score) {
        this.category = category;
        this.score = score;
    }

    public ScoreCard(Integer gameId, String category, Integer score) {
        this.gameId = gameId;
        this.category = category;
        this.score = score;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
