package game;

import java.util.Arrays;
import java.util.List;

public enum Category {

    ACES(1, "Aces"),
    TWOS(2, "Twos"),
    THREES(3, "Threes"),
    FOURS(4, "Fours"),
    FIVES(5, "Fives"),
    SIXES(6, "Sixes"),
    THREE_OF_A_KIND(7, "Three of a Kind"),
    FOUR_OF_A_KIND(8, "Four of a Kind"),
    FULL_HOUSE(9, "Full House"),
    SMALL_STRAIGHT(10, "Small Straight"),
    LARGE_STRAIGHT(11, "Large Straight"),
    YAHTZEE(12, "Yahtzee"),
    CHANCE(13, "Chance"),
    ;

    private final int id;
    private final String desc;

    Category(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public List<Category> getUpperCategories() {
        return Arrays.asList(ACES, TWOS, THREES, FOURS, FIVES, SIXES);
    }

    public boolean isUpperSection() {
        return getUpperCategories().contains(this);
    }

    public static Category valueOf(int id) {
        for (Category category : Category.values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

}
