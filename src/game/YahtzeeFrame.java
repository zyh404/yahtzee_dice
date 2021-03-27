package game;

import util.Message;
import util.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YahtzeeFrame extends JFrame {

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 9999;


    private Game gameModel;
    private Category selectedCategory;
    private JButton[] categories;
    private JTextField[] scoreFields;
    private ImagePanel[] dices;
    private JCheckBox[] keepBoxes;
    private JTextField playerField;
    private JTextField upperScoreField;
    private JTextField upperBonusField;
    private JTextField upperTotalField;
    private JTextField lowerBonusField;
    private JTextField lowerScoreField;
    private JTextField lowerTotalField;
    private JButton rollBtn;

    public YahtzeeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 1000);
        setResizable(false);
        init();
    }


    public static void main(String args[]) {
        YahtzeeFrame yahtzee = new YahtzeeFrame();
        yahtzee.setVisible(true);
    }

    private void init() {
        gameModel = new Game();
        categories = new JButton[13];
        scoreFields = new JTextField[13];
        dices = new ImagePanel[5];
        keepBoxes = new JCheckBox[5];
        playerField = new JTextField();
        upperScoreField = createUnEditableField();
        upperBonusField = createUnEditableField();
        upperTotalField = createUnEditableField();
        lowerBonusField = createUnEditableField();
        lowerScoreField = createUnEditableField();
        lowerTotalField = createUnEditableField();

        for (Category category : Category.values()) {
            int index = category.getId() - 1;
            categories[index] = new JButton(category.getDesc());
            categories[index].setName(String.valueOf(category.getId()));
        }

        for (int i = 0; i < scoreFields.length; i++) {
            scoreFields[i] = createUnEditableField();
        }
        List<KeepListing> keepListing = gameModel.getKeepListing();
        for (int i = 0; i < keepListing.size(); i++) {
            dices[i] = new ImagePanel(keepListing.get(i).getDice());
        }

        for (int i = 0; i < keepBoxes.length; i++) {
            keepBoxes[i] = new JCheckBox("Keep");
        }

        rollBtn = new JButton("Roll");

        initMenu();
        initComponent();
    }


    private void initMenu() {
        JMenu menu = new JMenu("Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem exitItem = new JMenuItem("Exit");
        menu.add(loadItem);
        menu.add(saveItem);
        menu.add(exitItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        loadItem.addActionListener(e -> {
            loadGame();
        });

        saveItem.addActionListener(e -> {
            saveGame();
        });

        exitItem.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(null, "Are you sure to exit ?", "Alert", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == option) {
                System.exit(0);
            }
        });
    }


    private void initComponent() {
        this.setLayout(null);
        JLabel playerLbl = new JLabel("Player Name:");
        playerLbl.setBounds(20, 5, 100, 20);
        playerField.setBounds(125, 5, 255, 20);
        this.add(playerLbl);
        this.add(playerField);

        JLabel upperLbl = new JLabel("Upper Section");
        upperLbl.setBounds(0, 30, 100, 20);
        this.add(upperLbl);

        int startX = 10, startY = 60;
        for (int i = 0; i < 6; i++) {
            categories[i].setBounds(startX, startY, 100, 20);
            scoreFields[i].setBounds(180, startY, 120, 20);
            this.add(categories[i]);
            this.add(scoreFields[i]);
            startY += 40;
        }

        JLabel upperScoreLbl = new JLabel("Score Subtotal");
        JLabel upperBonusLbl = new JLabel("Bonus");
        JLabel upperTotalLbl = new JLabel("Grand Total");
        upperScoreLbl.setBounds(startX, startY, 100, 20);
        upperScoreField.setBounds(180, startY, 120, 20);
        upperBonusLbl.setBounds(startX, startY += 40, 100, 20);
        upperBonusField.setBounds(180, startY, 120, 20);
        upperTotalLbl.setBounds(startX, startY += 40, 100, 20);
        upperTotalField.setBounds(180, startY, 120, 20);
        this.add(upperScoreLbl);
        this.add(upperScoreField);
        this.add(upperBonusLbl);
        this.add(upperBonusField);
        this.add(upperTotalLbl);
        this.add(upperTotalField);

        JLabel lowerLbl = new JLabel("Lower Section");
        lowerLbl.setBounds(0, startY += 40, 100, 20);
        this.add(lowerLbl);

        for (int i = 6; i < categories.length; i++) {
            startY += 40;
            categories[i].setBounds(startX, startY, 100, 20);
            scoreFields[i].setBounds(180, startY, 120, 20);
            this.add(categories[i]);
            this.add(scoreFields[i]);
        }

        JLabel lowerBonusLbl = new JLabel("Yahtzee Bonus");
        JLabel lowerScoreLbl = new JLabel("Total of lower section");
        JLabel lowerTotalLbl = new JLabel("Grand Total");
        lowerBonusLbl.setBounds(startX, startY += 40, 100, 20);
        lowerBonusField.setBounds(180, startY, 120, 20);
        lowerScoreLbl.setBounds(startX, startY += 40, 150, 20);
        lowerScoreField.setBounds(180, startY, 120, 20);
        lowerTotalLbl.setBounds(startX, startY += 40, 100, 20);
        lowerTotalField.setBounds(180, startY, 120, 20);
        this.add(lowerBonusLbl);
        this.add(lowerBonusField);
        this.add(lowerScoreLbl);
        this.add(lowerScoreField);
        this.add(lowerTotalLbl);
        this.add(lowerTotalField);

        startX = 300;
        startY = 30;
        for (int i = 0; i < dices.length; i++) {
            dices[i].setBounds(startX, startY, 100, 100);
            keepBoxes[i].setBounds(startX + 10, startY + 105, 80, 20);
            this.add(dices[i]);
            this.add(keepBoxes[i]);
            startY += 130;
        }

        rollBtn.setBounds(startX + 5, startY + 10, 80, 30);
        this.add(rollBtn);

        rollBtn.addActionListener(e -> {
            String playerText = playerField.getText();
            if (playerText == null || playerText.trim().length() == 0) {
                showMsg("Please fill your name first!");
                return;
            }
            try {
                gameModel.play();
                updateDice();
            } catch (Exception ex) {
                showMsg(ex.getMessage());
            }
        });

        for (JButton categoryBtn : categories) {
            categoryBtn.addActionListener(e -> {
                int categoryId = Integer.parseInt(categoryBtn.getName());
                selectedCategory = Category.valueOf(categoryId);
                try {
                    gameModel.nextRound(selectedCategory);
                    if (gameModel.isOver()) {
                        rollBtn.setEnabled(false);
                    }
                    updateScoreCard();
                    updateDice();
                } catch (Exception ex) {
                    showMsg(ex.getMessage());
                }
            });
        }

        for (int i = 0; i < keepBoxes.length; i++) {
            final int diceId = i;
            keepBoxes[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        gameModel.keep(diceId, true);
                    } else {
                        gameModel.keep(diceId, false);
                    }
                }
            });
        }
    }


    private void updateDice() {
        List<KeepListing> keepListing = gameModel.getKeepListing();
        for (KeepListing keepItem : keepListing) {
            Integer id = keepItem.getId();
            dices[id].setImage(keepItem.getDice());
            if (keepItem.getKeep()) {
                keepBoxes[id].setSelected(true);
            } else {
                keepBoxes[id].setSelected(false);
            }
        }
    }

    private void updateScoreCard() {
        Map<Category, ScoreCard> cards = gameModel.getCards();
        cards.forEach(((category, scoreCard) -> {
            if (null != scoreCard.getScore()) {
                scoreFields[category.getId() - 1].setText(String.valueOf(scoreCard.getScore()));
            }
        }));

        int upperScore = gameModel.getUpperScore();
        Integer upperBonus = gameModel.getPlayingInfo().getUpperBonus();
        int totalUpperScore = gameModel.getTotalUpperScore();
        if (upperScore > 0) {
            upperScoreField.setText(String.valueOf(upperScore));
        } else {
            upperScoreField.setText("");
        }
        if (null != upperBonus && upperBonus > 0) {
            upperBonusField.setText(String.valueOf(upperBonus));
        } else {
            upperBonusField.setText("");
        }
        if (totalUpperScore > 0) {
            upperTotalField.setText(String.valueOf(totalUpperScore));
        } else {
            upperTotalField.setText("");
        }

        Integer lowerBonus = gameModel.getPlayingInfo().getLowerBonus();
        int lowerScore = gameModel.getLowerScore();
        int totalLowerScore = gameModel.getTotalLowerScore();
        if (null != lowerBonus && lowerBonus > 0) {
            lowerBonusField.setText(String.valueOf(lowerBonus));
        } else {
            lowerBonusField.setText("");
        }
        if (lowerScore > 0) {
            lowerScoreField.setText(String.valueOf(lowerScore));
        } else {
            lowerScoreField.setText("");
        }
        if (totalLowerScore > 0) {
            lowerTotalField.setText(String.valueOf(totalLowerScore));
        } else {
            lowerTotalField.setText("");
        }

        List<Category> usedCategories = gameModel.getUsedCategories();
        for (JButton categoryBtn : categories) {
            if (usedCategories.contains(Category.valueOf(Integer.parseInt(categoryBtn.getName())))) {
                categoryBtn.setEnabled(false);
            } else {
                categoryBtn.setEnabled(true);
            }
        }

        for (Category category : Category.values()) {
            if (!cards.containsKey(category)) {
                scoreFields[category.getId() - 1].setText("");
            }
        }
    }

    private void loadGame() {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream oin = null;
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
            Message message = new Message(Message.Type.LoadGame, "");
            Utils.sendMsg(message, out);

            Message resp = (Message) oin.readObject();
            List<PlayingInfo> playingInfoList = (List<PlayingInfo>) resp.getData();
            if (null == playingInfoList || playingInfoList.isEmpty()) {
                showMsg("No game to be loaded!");
            } else {
                String[] chooses = new String[playingInfoList.size()];
                Map<String, Integer> playingMap = new HashMap<>(playingInfoList.size());
                for (int i = 0; i < playingInfoList.size(); i++) {
                    PlayingInfo p = playingInfoList.get(i);
                    chooses[i] = "PlayerName:" + p.getPlayerName() + ";SaveTime:" + Utils.formatDate(p.getCreatedAt());
                    playingMap.put(chooses[i], p.getId());
                }
                Object selectedItem = JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, chooses, chooses[0]);
                Integer gameId = playingMap.get(selectedItem.toString());
                if (null == gameId) {
                    return;
                }
                System.out.println("selected: " + selectedItem + ", id:" + gameId);
                message = new Message(Message.Type.LoadGame, gameId);
                Utils.sendMsg(message, out);

                resp = (Message) oin.readObject();
                gameModel = (Game) resp.getData();
                updateModel();
                showMsg("Successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeQuietly(out, socket);
        }
    }

    private void updateModel() {
        playerField.setText(gameModel.getPlayingInfo().getPlayerName());
        updateDice();
        updateScoreCard();
    }

    private void saveGame() {
        if (null == gameModel) {
            return;
        }
        String playerName = playerField.getText();
        if (null == playerName || playerName.trim().isEmpty()) {
            showMsg("Please fill your name first!");
            return;
        }
        gameModel.getPlayingInfo().setPlayerName(playerName);
        Socket socket = null;
        ObjectOutputStream out = null;
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            Message message = new Message(Message.Type.SaveGame, gameModel);
            Utils.sendMsg(message, out);
            showMsg("Successfully!");
        } catch (Exception e) {

        } finally {
            Utils.closeQuietly(out, socket);
        }
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(null, msg, "data.Message", JOptionPane.INFORMATION_MESSAGE);
    }


    private JTextField createUnEditableField() {
        JTextField textField = new JTextField();
        textField.setEditable(false);
        return textField;
    }
}
