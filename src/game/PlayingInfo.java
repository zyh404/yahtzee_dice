package game;

import java.io.Serializable;
import java.util.Date;

public class PlayingInfo implements Serializable {
    private static final long serialVersionUID = 151198298423356127L;
    private Integer id;
    private String playerName;
    private Integer round;
    private Integer roll;
    private Integer upperBonus;
    private Integer lowerBonus;
    private Date createdAt;

    public PlayingInfo() {
        this.round = 0;
        this.roll = 0;
    }

    public PlayingInfo(String playerName) {
        this();
        this.playerName = playerName;
    }

    public PlayingInfo(Integer id, String playerName, Integer round, Integer roll, Integer upperBonus, Integer lowerBonus, Date createdAt) {
        this.id = id;
        this.playerName = playerName;
        this.round = round;
        this.roll = roll;
        this.upperBonus = upperBonus;
        this.lowerBonus = lowerBonus;
        this.createdAt = createdAt;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getRoll() {
        return roll;
    }

    public void setRoll(Integer roll) {
        this.roll = roll;
    }

    public Integer getUpperBonus() {
        return upperBonus;
    }

    public void setUpperBonus(Integer upperBonus) {
        this.upperBonus = upperBonus;
    }

    public Integer getLowerBonus() {
        return lowerBonus;
    }

    public void setLowerBonus(Integer lowerBonus) {
        this.lowerBonus = lowerBonus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
