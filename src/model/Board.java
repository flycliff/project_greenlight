package model;

import java.util.*;

import model.enums.*;
import view.*;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class Board {

    /**
     * list of Cards On the Board
     */
    private ArrayList<cardModel> cob = new ArrayList<>();

    /**
     * list of this boards successors. only end-of-game successors are added to this list.
     */
    private transient ArrayList<Board> succ = new ArrayList<>();

    private transient boolean succSet = false;

    /**
     * the best valid play
     */
    private transient Play hiPlay;

    /**
     * current score of the game, used in the successor system
     */
    private int currentscore = 0;

    /**
     * deck of cards for this game
     */
    private Deck deck;

    /**
     * make a board for a new game
     *
     * @param deck deck for this game
     */
    public Board(Deck deck) {
        this.deck = deck;
    }

    /**
     * used to make a board from a previous board
     *
     * @param last        the board that this board was once.
     * @param isSuccessor whether or not this is used in the successor system. used to ensure that that system is
     *                    non-destructive
     */
    public Board(Board last, int currentscore, boolean isSuccessor) {
        if (isSuccessor) {
            deck = last.getDeck().copy();
            for (cardModel cm : last.getCOB()) {
                if (!last.bestPlay().cards().contains(cm)) {
                    cm.deal(null);
                    cob.add(cm);
                } this.currentscore = last.bestPlay().score() + currentscore;
            }
        } else {
            deck = last.getDeck();
            for (cardModel cm : last.getCOB()) {
                if (!cm.getLoc().equals(location.DISCARDED)) {
                    this.cob.add(cm);
                }
            } this.currentscore = currentscore;
        }
    }

    /**
     * Get all successors for this board. a 'successor' is defined as an END GAME board configuration that has come out
     * of this.
     *
     * WARNING: this algorithm is extremely computationally intensive. This should not be done with a full deck or a
     * nearly full deck. The time and memory used by this algorithm is exponential and can crash the JVM or your PC if
     * you aren't careful.
     *
     * @param cs current score
     * @return an arraylist of the successors of this board
     */
    public ArrayList<Board> getSuccessors(int cs) {
        if (!succSet) {
            if (bestPlay().isRemoval()) {
                for (cardModel cm : getDeck().getUndealt()) {
                    Board nb = new Board(this, cs, true);
                    nb.bestPlay().cards().get(0).discard();
                    nb.deal(cm);
                    if (nb.isEnd()) {
                        succ.add(nb);
                    } else {
                        succ.addAll(nb.getSuccessors(cs + nb.bestPlay().score()));
                    }
                }
            } else {
                ArrayList<cardModel> r1 = getDeck().getUndealt();
                for (int x = 0; x < r1.size(); x++) {
                    for (int y = 0; (y < r1.size()) && (y != x); y++) {
                        for (int z = 0; (z < r1.size()) && (z != y); z++) {
                            Board nb = new Board(this, cs, true);
                            nb.deal(r1.get(x), r1.get(y), r1.get(z));
                            if (nb.isEnd()) {
                                succ.add(nb);
                            } else {
                                succ.addAll(nb.getSuccessors(cs + nb.bestPlay().score()));
                            }
                        }
                    }
                }
            } succSet = true;
        } return succ;
    }

    /**
     * is this a "final" successor? Final is defined as the deck being empty.
     *
     * @return true iff the deck is empty
     */
    private boolean isEnd() {
        return deck.getUndealt().size() == 0;
    }

    /**
     * deal some cards to the board.
     *
     * @param cv cards to be dealt to the board.
     */
    public void deal(Card... cv) {
        for (Card c : cv) {
            cardModel cm = deck.getCard(c);
            if (c instanceof cardView)
                cm.deal((cardView) c);
            else
                cm.deal(null);
            cob.add(cm);
        }
    }

    /**
     * @return true iff there are 5 cards on the board
     */
    public boolean isComplete() {
        return cob.size() == 5;
    }

    /**
     * @return the current score of this game
     */
    public int getCurrentScore() {
        return currentscore;
    }

    /**
     * @return the best play for this board and deck configuration
     */
    public Play bestPlay() {
        if (hiPlay == null) {
            rankCards();
            ArrayList<Play> vp = getValidPlays();
            if (vp.size() == 0) {
                cob.sort((o1, o2) -> Math.round(o1.getRank() * 100 - o2.getRank() * 100));
                hiPlay = new Play(cob.get(0));
            } else {
                vp.sort((o2, o1) -> (o1.score() - o2.score()));
                hiPlay = vp.get(0);
            }
        } return hiPlay;
    }

    /**
     * @return a list of all valid plays
     */
    private ArrayList<Play> getValidPlays() {
        ArrayList<Play> validplays = new ArrayList<>();
        ArrayList<cardModel> r1 = new ArrayList<>(cob);
        for (int x = 0; x < r1.size(); x++) {
            for (int y = 0; y < r1.size(); y++) {
                if (y != x) {
                    for (int z = 0; z < r1.size(); z++) {
                        if (z != y) {
                            cardModel c1 = cob.get(x);
                            cardModel c2 = cob.get(y);
                            cardModel c3 = cob.get(z);
                            Play newp = new Play(c1, c2, c3);
                            if (newp.isValid())
                                validplays.add(newp);
                        }
                    }
                }
            }
        } return validplays;
    }

    /**
     * @return a list of the Cards On the Board
     */
    private List<cardModel> getCOB() {
        return cob;
    }

    /**
     * get the deck that this board is using
     */
    private Deck getDeck() {
        return deck;
    }

    /**
     * rank all cards based on the current deck and board configuration
     */
    private void rankCards() {
        for (cardModel c : cob) {
            float rank = 0.0f;
            HashSet<Play> vp = deck.getPlays(c);
            int numPlays = vp.size();
            for (Play pm : vp) {
                float coeff = (float) numCardsOnBoard(pm) / 3;
                rank += pm.score() * coeff;
            }
            rank /= numPlays * 3;
            c.setRank(rank);
        }
    }

    /**
     * @param pm play to check
     * @return number of cards that are in that play that are also on the current game board
     */
    private int numCardsOnBoard(Play pm) {
        int tot = 0;
        for (cardModel c : pm.cards())
            if (c.getLoc().equals(location.ONBOARD))
                tot += 1;
        return tot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (cardModel c: cob) {
            sb.append(c.toString()).append(" : ");
        } return sb.toString();
    }

    /**
     * @return unique hashcode for this board.
     */
    @Override
    public int hashCode() {
        return deck.getDeck().size() * hiPlay.score();
    }
}
