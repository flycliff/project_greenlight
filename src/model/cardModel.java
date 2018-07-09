package model;

import model.enums.*;
import view.*;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class cardModel implements Card {

    /**
     * color of this card
     */
    private color color;

    /**
     * the number of this card
     */
    private int num;

    /**
     * location of this card (as defined in model.enums.location)
     */
    private location loc;

    /**
     * used for the under-the-hood card ranking system. only has a value if the card is on the board.
     */
    private float rank;

    /**
     * cardView associated with this card.
     */
    private cardView cv;

    /**
     * Create a new instance of a card model. by default, cards are not dealt when created.
     * @param num number of this card
     * @param color color of this card
     */
    cardModel(int num, color color) {
        this.num = num;
        this.color = color;
        this.loc = location.NOTDEALT;
    }

    //          GETTERS          //
    public color color() {
        return color;
    }
    public int number() {
        return num;
    }
    location getLoc() {
        return loc;
    }
    float getRank() {
        return rank;
    }

    //          SETTERS          //
    void setRank(float rank) {
        this.rank = rank;
    }
    void deal(cardView cv) {
        this.loc = location.ONBOARD;
        this.cv = cv;
    }
    public void discard() {
        this.loc = location.DISCARDED;
        if (cv != null)
            cv.reset();
    }

    /**
     * suggest a play on this card
     */
    public void suggestPlay() {
        if (cv != null)
            cv.suggestPlay();
    }

    /**
     * suggest a removal on this card.
     */
    public void suggestRemove() {
        if (cv != null)
            cv.suggestRemove();
    }

    /**
     * @param obj card to compare to this.
     * @return true iff obj's color and number are the same as this
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof cardModel) {
            cardModel cv = (cardModel) obj;
            return (cv.color() == this.color()) && (this.number() == cv.number());
        } return false;
    }

    /**
     * @return a string representing a cardModel, i.e. COLOR NUM
     */
    @Override
    public String toString() {
        return color + " " + number();
    }

    /**
     * the hashcode of a cardModel is the "number" of its color times 10, plus its number. this gives a unique code
     * for each possible card in a deck.
     *
     * @return unique hashcode for this card
     */
    @Override
    public int hashCode() {
        int col = 0;
        for (int i = 0; i < model.enums.color.values().length ; i++)
            if (model.enums.color.values()[i].equals(color))
                col = i;
        return col * 10 + num;
    }
}
