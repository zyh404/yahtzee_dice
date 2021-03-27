package server;

import game.Game;
import game.KeepListing;
import game.PlayingInfo;
import game.ScoreCard;
import util.Message;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class SaveServer extends JFrame {

    private static final int PORT = 9999;
    private JTextArea wordsBox;
    private DataBase db;

    public SaveServer() {
        db = new DataBase();
        createMainPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        new Thread(new AcceptListener()).start();
        setVisible(true);
    }

    public void createMainPanel() {
        wordsBox = new JTextArea(35, 10);

        JScrollPane listScroller = new JScrollPane(wordsBox);
        this.add(listScroller, BorderLayout.CENTER);
        listScroller.setPreferredSize(new Dimension(250, 80));
    }

    public static void main(String[] main) {
        SaveServer saveServer = new SaveServer();
    }

    private class AcceptListener implements Runnable {
        private ServerSocket serverSocket;

        public AcceptListener() {
            try {
                serverSocket = new ServerSocket(PORT);
                wordsBox.append("Ready to Accept Connections\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            while (true) {
                Socket socket = null;
                ObjectInputStream oin = null;
                ObjectOutputStream out = null;
                try {
                    socket = serverSocket.accept();
                    wordsBox.append("\nAccept connection:" + socket.getInetAddress().toString() + ":" + socket.getPort());
                    oin = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    Message msg = (Message) oin.readObject();
                    if (Message.Type.LoadGame == msg.getType()) {
                        wordsBox.append("\nReceive Game Listing Request");
                        List<PlayingInfo> allPlaying = db.findAllPlaying();
                        Message resp = new Message(Message.Type.GameListingResponse, allPlaying);
                        Utils.sendMsg(resp, out);

                        msg = (Message) oin.readObject();
                        Object data = msg.getData();
                        int gameId = Integer.parseInt(data.toString());

                        PlayingInfo playingInfo = db.findPlayingByID(gameId);
                        List<KeepListing> keepListings = db.getKeepListings(gameId);
                        List<ScoreCard> scoreCards = db.getScoreCards(gameId);
                        Game game = new Game(playingInfo, keepListings, scoreCards);

                        wordsBox.append("\nReceive Game Loading Request, playerName:" + playingInfo.getPlayerName() + ", date:" + Utils.formatDate(playingInfo.getCreatedAt()));
                        resp = new Message(Message.Type.GameInformationResponse, game);
                        Utils.sendMsg(resp, out);
                        wordsBox.append("\nGame loading finished");
                    } else if (Message.Type.SaveGame == msg.getType()) {
                        wordsBox.append("\nReceive Game Saving Request");
                        Game game = (Game) msg.getData();
                        int gameId = db.insert(game.getPlayingInfo());
                        List<KeepListing> keepListing = game.getKeepListing();
                        keepListing.forEach(item -> {
                            item.setGameId(gameId);
                            db.insert(item);
                        });
                        List<ScoreCard> scoreCards = game.getScoreCards();
                        scoreCards.forEach(card -> {
                            card.setGameId(gameId);
                            db.insert(card);
                        });
                        wordsBox.append("\nGame Saved, playerName:" + game.getPlayingInfo().getPlayerName() + ", date:" + Utils.formatDate(new Date()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.closeQuietly(oin, out, socket);
                }
            }
        }
    }
}
