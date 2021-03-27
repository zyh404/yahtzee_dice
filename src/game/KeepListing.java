package game;

import java.io.Serializable;

public class KeepListing implements Serializable {
    private static final long serialVersionUID = 259049061371850588L;

    private Integer id;
    private Integer gameId;
    private Integer dice;
    private Boolean isKeep;

    public KeepListing() {
        this.isKeep = false;
    }

    public KeepListing(Integer id, Integer dice) {
        this.id = id;
        this.dice = dice;
        this.isKeep = false;
    }

    public KeepListing(Integer id, Integer gameId, Integer dice, Boolean isKeep) {
        this.id = id;
        this.gameId = gameId;
        this.dice = dice;
        this.isKeep = isKeep;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getDice() {
        return dice;
    }

    public void setDice(Integer dice) {
        this.dice = dice;
    }

    public Boolean getKeep() {
        return isKeep;
    }

    public void setKeep(Boolean keep) {
        isKeep = keep;
    }
}
