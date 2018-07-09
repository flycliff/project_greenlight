package model;

import model.enums.location;

import java.util.*;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class Play {

    /**
     * store all the cards in an arraylist
     */
    private ArrayList<cardModel> cards = new ArrayList<>();

    /**
     * the score value of this cards. If it's not yet set, it will be -1. If it's an invalid cards, it will be 0.
     */
    private int score = -1;

    /**
     * is this cards a removal or a hand?
     */
    private boolean isRemoval = false;

    /**
     * make a new hand
     * @param all cards in the cards.
     */
    Play(cardModel... all) {
        if (all.length == 1) {
            cards.add(all[0]);
            this.isRemoval = true;
        } else if (all.length == 3) {
            cards.addAll(Arrays.asList(all));
            cards.sort(Comparator.comparingInt(cardModel::number));
        }
    }

    /**
     * @return is this a removal or a hand?
     */
    public boolean isRemoval() {
        return this.isRemoval;
    }

    /**
     * get the cards of this play.
     *
     * @return the arraylist of cards in this play.
     */
    public ArrayList<cardModel> cards() {
        return cards;
    }

    /**
     1-2-3 same color = 50 points
     2-3-4 same color = 60 points
     3-4-5 same color = 70 points
     5-6-7 same color = 90 points
     6-7-8 same color = 100 points

     1-1-1 diff color = 20 points
     2-2-2 diff color = 30 points
     3-3-3 diff color = 40 points
     4-4-4 diff color = 50 points
     5-5-5 diff color = 60 points
     6-6-6 diff color = 70 points
     7-7-7 diff color = 80 points
     8-8-8 diff color = 90 points

     1-2-3 any  color = 10 points
     2-3-4 any  color = 20 points
     3-4-5 any  color = 30 points
     4-5-6 any  color = 40 points
     5-6-7 any  color = 50 points
     6-7-8 any  color = 60 points
     */
    public int score() {
        if (isRemoval) {
            return 0;
        } else if (score == -1) {
            if (allSameCol() && isRun())
                score = 40 + (cards.get(0).number()) * 10;
            else if (allSameNum())
                score =  10 + (cards.get(0).number()) * 10;
            else if (isRun())
                score = (cards.get(0).number()) * 10;
            else
                score = 0;
        } return score;
    }

    /**
     * a 'run' is defined as three or more cards that are in ascending or descending order by number. This check is
     * possible because the cards are sorted by number in the constructor
     *
     * @return true iff this play is a run
     */
    private boolean isRun() {
        return ((cards.get(0).number() + 1) == cards.get(1).number() &&
                (cards.get(0).number() + 2) == cards.get(2).number());
    }

    /**
     * @return true iff all the cards in this play are the same number
     */
    private boolean allSameNum() {
        return (allDiffCol() &&
                cards.get(0).number() == cards.get(1).number() &&
                cards.get(1).number() == cards.get(2).number());
    }

    /**
     * @return true iff all cards in this play are the same color.
     */
    private boolean allSameCol() {
        return (cards.get(0).color() == cards.get(1).color()) &&
                cards.get(1).color() == cards.get(2).color();
    }

    /**
     * @return true iff all cards in this play are different colors.
     */
    private boolean allDiffCol() {
        return  (cards.get(0).color() != cards.get(1).color()) &&
                (cards.get(1).color() != cards.get(2).color()) &&
                (cards.get(0).color() != cards.get(2).color());
    }

    /**
     * @return 'cost' of this play, i.e. the sum of the rank of the cards
     */
    private int getCost() {
        int tot = 0;
        for (cardModel cm : cards()) {
            tot += cm.getRank();

        } return tot;
    }

    /**
     * @return true iff none of the cards are discarded
     */
    boolean isValid() {
        for (cardModel c : cards)
            if (c.getLoc().equals(location.DISCARDED))
                return false;
        return score() >= getCost();
    }

    /**
     * @return String representation of this play, i.e. each card separated by a colon
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (cardModel c : cards)
            sb.append(c).append(" : ");
        return sb.toString();
    }

    /**
     * @param obj another play to compare to this one.
     * @return true iff the other play contains the same cards as this one
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Play) {
            for (cardModel cm : ( (Play) obj ).cards())
                if (!cards().contains(cm))
                    return false;
            return true;
        } return false;
    }

    /**
     * @return the unique hashcode of this play
     */
    @Override
    public int hashCode() {
        int code = 0;
        for (cardModel c : cards) {
            code *= c.hashCode();
        } return code;
    }
}
