package game;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Game implements Serializable {
    private static final long serialVersionUID = -5242544140710191120L;

    private static final int MIN_DICE = 1;
    private static final int MAX_DICE = 6;
    private static final int NUM_DICE = 5;
    private static final int MAX_ROUND = 13;
    private static final int ROLLS_EACH_ROUND = 3;
    private static final int UPPER_BONUS_LIMIT = 63;
    private static final Map<Category, Integer> CATEGORY_DICE_MAPPING;

    static {
        CATEGORY_DICE_MAPPING = new HashMap<>();
        CATEGORY_DICE_MAPPING.put(Category.ACES, 1);
        CATEGORY_DICE_MAPPING.put(Category.TWOS, 2);
        CATEGORY_DICE_MAPPING.put(Category.THREES, 3);
        CATEGORY_DICE_MAPPING.put(Category.FOURS, 4);
        CATEGORY_DICE_MAPPING.put(Category.FIVES, 5);
        CATEGORY_DICE_MAPPING.put(Category.SIXES, 6);
    }

    private Random randomGenerator;
    private PlayingInfo playingInfo;
    private List<KeepListing> keepListing;
    private List<ScoreCard> scoreCards;
    private Map<Category, ScoreCard> cardMap;

    public Game() {
        this.randomGenerator = new Random(System.currentTimeMillis());
        this.playingInfo = new PlayingInfo();
        this.keepListing = new ArrayList<>(NUM_DICE);
        this.scoreCards = new ArrayList<>(MAX_ROUND);
        this.cardMap = new HashMap<>(MAX_ROUND);

        reset();
    }

    public Game(PlayingInfo playingInfo, List<KeepListing> keepListing, List<ScoreCard> cards) {
        this.randomGenerator = new Random(System.currentTimeMillis());
        this.playingInfo = playingInfo;
        this.keepListing = keepListing;
        this.scoreCards = cards;
        this.cardMap = scoreCards.stream().collect(Collectors.toMap(s -> Category.valueOf(s.getCategory()), Function.identity()));
    }

    public PlayingInfo getPlayingInfo() {
        return playingInfo;
    }

    public void setPlayingInfo(PlayingInfo playingInfo) {
        this.playingInfo = playingInfo;
    }

    public List<KeepListing> getKeepListing() {
        return keepListing;
    }

    public void setKeepListing(List<KeepListing> keepListing) {
        this.keepListing = keepListing;
    }

    public List<ScoreCard> getScoreCards() {
        return scoreCards;
    }

    public void setScoreCards(List<ScoreCard> scoreCards) {
        this.scoreCards = scoreCards;
    }

    public Map<Category, ScoreCard> getCards() {
        return cardMap;
    }


    public void play() {
        if (isOver()) {
            throw new IllegalStateException("Game is over!");
        }
        if (playingInfo.getRoll() >= ROLLS_EACH_ROUND) {
            throw new IllegalStateException("Please select one category and finish the current round!");
        }
        roll();
    }

    public void roll() {
        if (keepListing.isEmpty()) {
            for (int i = 0; i < NUM_DICE; i++) {
                KeepListing keepItem = new KeepListing(i, playingInfo.getId(), rollDice(), false);
                keepListing.add(keepItem);
            }
        } else {
            for (KeepListing keepItem : keepListing) {
                if (!keepItem.getKeep()) {
                    keepItem.setDice(rollDice());
                }
            }
        }
        playingInfo.setRoll(playingInfo.getRoll() + 1);
    }

    public boolean isOver() {
        return playingInfo.getRound() >= MAX_ROUND && playingInfo.getRoll() >= ROLLS_EACH_ROUND;
    }

    public void keep(int diceId, boolean isKeep) {
        KeepListing keepListing = this.keepListing.stream().filter(keepItem -> keepItem.getId() == diceId).findFirst().orElse(null);
        keepListing.setKeep(isKeep);
    }

    public void nextRound(Category selectedCategory) {
        checkCategory(selectedCategory);
        int score = computeScoreIfYahtzee();
        if (score == 50 && cardMap.containsKey(Category.YAHTZEE)) {
            //apply the special yahtzee rule
            if (cardMap.get(Category.YAHTZEE).getScore() == 50) {
                playingInfo.setLowerBonus(100);
            } else {
                Integer dice = keepListing.get(0).getDice();
                Category category = CATEGORY_DICE_MAPPING.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(dice);
                if (cardMap.containsKey(category)) {
                    //the corresponding box in the Upper Section has been used already, choose one unused box in Lower Section
                    Optional<Category> unusedLowerBox = Arrays.stream(Category.values()).filter(c -> !c.isUpperSection())
                            .filter(c -> !cardMap.containsKey(c)).findFirst();
                    if (!selectedCategory.isUpperSection() && unusedLowerBox.isPresent()) {
                        throw new IllegalStateException("You must select one unused box in Lower Section!");
                    } else {
                        //no available box can be used
                        score = 0;
                    }
                } else {
                    //the corresponding box in the Upper Section has not been used
                    if (selectedCategory != category) {
                        throw new IllegalStateException("You must select corresponding unused box in Upper Section!");
                    }
                }
            }
        } else {
            score = computeScore(selectedCategory);
        }
        if (cardMap.containsKey(selectedCategory)) {
            cardMap.get(selectedCategory).setScore(score);
        } else {
            ScoreCard scoreCard = new ScoreCard(selectedCategory.name(), score);
            scoreCards.add(scoreCard);
            cardMap.put(selectedCategory, scoreCard);
        }
        if (playingInfo.getUpperBonus() == null && getUpperScore() >= UPPER_BONUS_LIMIT) {
            playingInfo.setUpperBonus(35);
        }
        if (playingInfo.getUpperBonus() == null && Category.FOUR_OF_A_KIND == selectedCategory && score > 0) {
            playingInfo.setUpperBonus(35);
        }

        playingInfo.setRound(playingInfo.getRound() + 1);
        playingInfo.setRoll(0);
        reset();
    }

    public void reset() {
        keepListing.clear();
        for (int i = 0; i < NUM_DICE; i++) {
            keepListing.add(new KeepListing(i, 1));
        }
    }

    public List<Category> getUsedCategories() {
        ArrayList<Category> categories = new ArrayList<>(cardMap.keySet());
        categories.remove(Category.YAHTZEE);
        return categories;
    }

    public int getUpperScore() {
        return cardMap.entrySet().stream().filter(e -> e.getKey().isUpperSection())
                .mapToInt(e -> e.getValue().getScore()).sum();
    }

    public int getLowerScore() {
        return cardMap.entrySet().stream().filter(e -> !e.getKey().isUpperSection())
                .mapToInt(e -> e.getValue().getScore()).sum();
    }

    public int getTotalUpperScore() {
        int upperScore = getUpperScore();
        if (null != playingInfo.getUpperBonus()) {
            upperScore += playingInfo.getUpperBonus();
        }
        return upperScore;
    }

    public int getTotalLowerScore() {
        int lowerScore = getLowerScore();
        if (null != playingInfo.getLowerBonus()) {
            lowerScore += playingInfo.getLowerBonus();
        }
        return lowerScore;
    }

    private int computeScore(Category selectedCategory) {
        switch (selectedCategory) {
            //The sum of the dice with the number 1
            case ACES:
                //The sum of the dice with the number 2
            case TWOS:
                //The sum of the dice with the number 3
            case THREES:
                //The sum of the dice with the number 4
            case FOURS:
                //The sum of the dice with the number 5
            case FIVES:
                //The sum of the dice with the number 6
            case SIXES:
                return computeScoreByCountingSameDice(CATEGORY_DICE_MAPPING.get(selectedCategory));
            case THREE_OF_A_KIND:
                //At least three dice of the same
                return computeScoreByLeastSameDice(3);
            case FOUR_OF_A_KIND:
                //At least four dice of the same
                return computeScoreByLeastSameDice(4);
            case FULL_HOUSE:
                //Three of one number and two of another
                return computeScoreIfFullHouse();
            case SMALL_STRAIGHT:
                //Four sequential dice
                //(1-2-3-4, 2-3-4-5, or 3-4-5-6)
                return computeScoreIfStraight(4);
            case LARGE_STRAIGHT:
                //Five sequential dice
                //(1-2-3-4-5, 2-3-4-5-6)
                return computeScoreIfStraight(5);
            case YAHTZEE:
                //All five dice the same
                return computeScoreIfYahtzee();
            case CHANCE:
                //Any combination
                return computeChanceScore();
            default:
                throw new IllegalArgumentException("unsupported category:" + selectedCategory);
        }
    }

    private int computeScoreByCountingSameDice(int sameDice) {
        return keepListing.stream().filter(dice -> dice.getDice() == sameDice).mapToInt(KeepListing::getDice).sum();
    }

    private int computeScoreByLeastSameDice(int repeat) {
        for (int i = MIN_DICE; i <= MAX_DICE; i++) {
            if (getRepeatedDice(i) >= repeat) {
                return keepListing.stream().mapToInt(KeepListing::getDice).sum();
            }
        }
        return 0;
    }

    private int computeScoreIfFullHouse() {
        for (int i = MIN_DICE; i <= MAX_DICE; i++) {
            int repeatedOne = getRepeatedDice(i);
            for (int j = MIN_DICE; j < MAX_DICE; j++) {
                if (j != i) {
                    int repeatedTwo = getRepeatedDice(j);
                    if ((repeatedOne == 2 && repeatedTwo == 3) || (repeatedOne == 3 && repeatedTwo == 2)) {
                        return 25;
                    }
                }
            }
        }
        return 0;
    }

    private int computeScoreIfStraight(int repeat) {
        List<Integer> dices = keepListing.stream().map(KeepListing::getDice).distinct().sorted().collect(Collectors.toList());
        if (4 == repeat) {
            if (dices.containsAll(Arrays.asList(1, 2, 3, 4))
                    || dices.containsAll(Arrays.asList(2, 3, 4, 5))
                    || dices.containsAll(Arrays.asList(3, 4, 5, 6))
            ) {
                return 30;
            }
        } else if (repeat == 5) {
            if (dices.containsAll(Arrays.asList(1, 2, 3, 4, 5))
                    || dices.containsAll(Arrays.asList(2, 3, 4, 5, 6))
            ) {
                return 40;
            }
        }
        return 0;
    }

    private int computeScoreIfYahtzee() {
        List<Integer> dices = keepListing.stream().map(KeepListing::getDice).distinct().sorted().collect(Collectors.toList());
        return dices.size() == 1 ? 50 : 0;
    }

    private int computeChanceScore() {
        return keepListing.stream().mapToInt(KeepListing::getDice).sum();
    }

    private int getRepeatedDice(int sameDice) {
        return (int) keepListing.stream().filter(dice -> dice.getDice() == sameDice).count();
    }

    private int rollDice() {
        return randomGenerator.nextInt(6) + 1;
    }

    private void checkCategory(Category selectedCategory) {
        if (playingInfo.getRoll() < ROLLS_EACH_ROUND) {
            throw new IllegalStateException("You must finish your 3 rolls in the turn before selecting on category!");
        }
        if (selectedCategory != Category.YAHTZEE && cardMap.containsKey(selectedCategory)) {
            throw new IllegalStateException("The category: " + selectedCategory + " has been used!");
        }
    }
}
