package model;

import model.enums.*;

import java.util.*;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class Deck {

    /**
     * HashMap containing the ArrayLists of cards for each color. That is, there is a list of red cards, a list of
     * blue cards, etc...
     */
    private HashMap<color, ArrayList<cardModel>> deck;

    /**
     * make a new deck. initialize all cards.
     */
    public Deck() {
        deck = new HashMap<>();
        for (color c : color.values()) {
            ArrayList<cardModel> l = new ArrayList<cardModel>() {{
                for (int i=1; i < 9; i++)
                    add(new cardModel(i, c));
            }};
            deck.put(c, l);
        }
    }

    /**
     * get a specific card from the deck. since this uses the generic Card, it can be a cardView or a cardModel from
     * another deck
     *
     * @param c card to retrieve
     * @return the desired card from the deck
     */
    cardModel getCard(Card c) {
        return deck.get(c.color()).get(c.number()-1);
    }

    /**
     * @return the deck
     */
    ArrayList<cardModel> getDeck() {
        ArrayList<cardModel> remaining = new ArrayList<>();
        for (ArrayList<cardModel> col : deck.values())
            remaining.addAll(col);
        return remaining;
    }

    /**
     * @return all undealt cards in the deck
     */
    public ArrayList<cardModel> getUndealt() {
        ArrayList<cardModel> remaining = new ArrayList<>();
        for (ArrayList<cardModel> col : deck.values())
            for (cardModel cm : col)
                if (cm.getLoc().equals(location.NOTDEALT))
                    remaining.add(cm);
        return remaining;
    }

    /**
     * @param c card in all plays
     * @return a HashSet of all possible plays that include card c
     */
    HashSet<Play> getPlays(cardModel c) {
        HashSet<Play> allp = new HashSet<>();
        ArrayList<cardModel> deck = getDeck();

        for (cardModel dc : getDeck())
            if (!dc.getLoc().equals(location.DISCARDED))
                deck.add(dc);

        for (cardModel c1 : deck) {
            for (cardModel c2 : deck) {
                Play pm = new Play(c, c1, c2);
                if ((pm.score() != 0) && (pm.score() != -1))
                    allp.add(pm);
            }
        } return allp;
    }

    /**
     * @return a deep copy of this deck, including copies of all the cards and their states.
     */
    Deck copy() {
        Deck nDeck = new Deck();
        for (cardModel cm : getDeck())
            if (cm.getLoc().equals(location.ONBOARD))
                nDeck.getCard(cm).deal(null);
            else if (cm.getLoc().equals(location.DISCARDED))
                nDeck.getCard(cm).discard();
        return nDeck;
    }
}
