package server;

import game.KeepListing;
import game.PlayingInfo;
import game.ScoreCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String DB_FILE = "yahtzee.db";

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    public static Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
    }

    public DataBase() {
        init();
    }

    public int insert(PlayingInfo playingInfo) {
        try {
            connection = connect();
            String sql = "insert into playing_info values(?,?,?,?,?,?,?);";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setNull(1, Types.INTEGER);
            statement.setString(2, playingInfo.getPlayerName());
            statement.setInt(3, playingInfo.getRound());
            statement.setInt(4, playingInfo.getRoll());
            setInt(statement, 5, playingInfo.getUpperBonus());
            setInt(statement, 6, playingInfo.getLowerBonus());
            statement.setDate(7, new Date(new java.util.Date().getTime()));
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                return id;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public void insert(KeepListing keepListing) {
        try {
            connection = connect();
            String sql = "insert into keep_listing values(?,?,?,?);";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, keepListing.getId());
            statement.setInt(2, keepListing.getGameId());
            statement.setInt(3, keepListing.getDice());
            statement.setBoolean(4, keepListing.getKeep());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public void insert(ScoreCard card) {
        try {
            connection = connect();
            String sql = "insert into score_card values(?,?,?);";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, card.getGameId());
            statement.setString(2, card.getCategory());
            setInt(statement, 3, card.getScore());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public List<PlayingInfo> findAllPlaying() {
        List<PlayingInfo> playingInfoList = new ArrayList<>();
        try {
            connection = connect();
            statement = connection.prepareStatement("select * from playing_info;");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                playingInfoList.add(getPlayingInfo(resultSet));
            }
            return playingInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public PlayingInfo findPlayingByID(int id) {
        try {
            connection = connect();
            statement = connection.prepareStatement("select * from playing_info where id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return getPlayingInfo(resultSet);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public List<KeepListing> getKeepListings(int gameId) {
        List<KeepListing> keepListings = new ArrayList<>();
        try {
            connection = connect();
            statement = connection.prepareStatement("select * from keep_listing where game_id=?");
            statement.setInt(1, gameId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                keepListings.add(getKeepListing(resultSet));
            }
            return keepListings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    public List<ScoreCard> getScoreCards(int gameId) {
        List<ScoreCard> scoreCards = new ArrayList<>();
        try {
            connection = connect();
            statement = connection.prepareStatement("select * from score_card where game_id=?");
            statement.setInt(1, gameId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                scoreCards.add(getScoreCard(resultSet));
            }
            return scoreCards;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    private PlayingInfo getPlayingInfo(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String playerName = rs.getString("player_name");
        int round = rs.getInt("round");
        int roll = rs.getInt("roll");
        Integer upperBonus = (Integer) rs.getObject("upper_bonus");
        Integer lowerBonus = (Integer) rs.getObject("lower_bonus");
        Date createdAt = rs.getDate("created_at");
        return new PlayingInfo(id, playerName, round, roll, upperBonus, lowerBonus, createdAt);
    }

    private KeepListing getKeepListing(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int gameId = rs.getInt("game_id");
        int dice = rs.getInt("dice");
        boolean isKeep = rs.getBoolean("is_keep");
        return new KeepListing(id, gameId, dice, isKeep);
    }

    private ScoreCard getScoreCard(ResultSet rs) throws SQLException {
        int gameId = rs.getInt("game_id");
        String category = rs.getString("category");
        Integer score = rs.getInt("score");
        return new ScoreCard(gameId, category, score);
    }

    private void init() {
        try {
            connection = connect();
            if (!tableExists("playing_info")) {
                String sql = "CREATE TABLE `playing_info` (\n" +
                        "  `id` INTEGER PRIMARY KEY,\n" +
                        "  `player_name` varchar(50) NOT NULL,\n" +
                        "  `round` int(2) NOT NULL DEFAULT 0,\n" +
                        "  `roll` int(2) NOT NULL DEFAULT 0,\n" +
                        "  `upper_bonus` int(5) DEFAULT NULL,\n" +
                        "  `lower_bonus` int(5) DEFAULT NULL,\n" +
                        "  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                        ");";
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Table: playing_info is created.");
            }

            if (!tableExists("keep_listing")) {
                String sql = "CREATE TABLE `keep_listing` (\n" +
                        "  `id` int(2) NOT NULL,\n" +
                        "  `game_id` int(20) NOT NULL,\n" +
                        "  `dice` int(2) NOT NULL,\n" +
                        "  `is_keep` tinyint(1) NOT NULL DEFAULT false,\n" +
                        "   UNIQUE (`id`,`game_id`)\n" +
                        ");";
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Table: keep_listing is created.");
            }

            if (!tableExists("score_card")) {
                String sql = "CREATE TABLE `score_card` (\n" +
                        "  `game_id` int(20) NOT NULL,\n" +
                        "  `category` varchar(20) NOT NULL,\n" +
                        "  `score` int(2) DEFAULT NULL,\n" +
                        "   UNIQUE (`game_id`,`category`)\n" +
                        ");";
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Table: score_card is created.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    private boolean tableExists(String tableName) {
        try {
            statement = connection.prepareStatement("select count(*) from sqlite_master where type =? and name =?;");
            statement.setString(1, "table");
            statement.setString(2, tableName);
            resultSet = statement.executeQuery();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setInt(PreparedStatement s, int index, Integer value) throws SQLException {
        if (null == value) {
            s.setNull(index, Types.INTEGER);
        } else {
            s.setInt(index, value);
        }
    }

    private void release() {
        try {
            if (null != connection) {
                connection.close();
                connection = null;
            }
            if (null != statement) {
                statement.close();
                statement = null;
            }
            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
