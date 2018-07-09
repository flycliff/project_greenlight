package controller;

import model.*;
import view.*;
import java.util.*;

/**
 * ccontroller manages interaction between the view and the model. To make notifying the scoreboard easier, I've
 * implemented the Observer pattern such that the scoreboard can be registered for changes directly from the controller.
 *
 * @author Will Clifford (GitHub: wtc8754)
 */
public class ccontroller implements ScoreObserver {

    /**
     * ScoreObservers from view stored in an ArrayList
     */
    private ArrayList<ScoreObserver> observs = new ArrayList<>();

    /**
     * deck used for this game.
     */
    private Deck deck = new Deck();

    /**
     * current state of the board. Board states should me remade for each turn.
     */
    private Board config = new Board(deck);

    /**
     * current score of the game. starts at 0.
     */
    private int score = 0;

    /**
     * highest achievable score
     */
    private int bestScore = -1;

    /**
     * the play that is suggested to be played.
     */
    private Play suggested;

    /**
     * contact the correct cardViews, and indicate the correct cards to play.
     *
     * @param pm the play that you'd like to suggest
     */
    private void suggest(Play pm) {
        this.suggested = pm;
        if (pm.isRemoval())
            for (cardModel mc : pm.cards())
                mc.suggestRemove();
        else
            for (cardModel mc : pm.cards())
                mc.suggestPlay();
    }

    /**
     * make the play for the model. This usually follows the cards to be reset from the last turn.
     */
    public void play() {
        if (suggested != null) {
            for (cardModel c : suggested.cards())
                c.discard();
            if (!suggested.isRemoval())
                score += suggested.score();
            config = new Board(config, score, false);
            suggested = null;
            updateScore(score, bestScore);
        }
    }

    /**
     * add a card to the board configuration. If the configuration is complete, then suggest the next play.
     *
     * @param cv cardView to add.
     */
    public void cardAdded(cardView cv) {
        config.deal(cv);
        if (config.isComplete()) {
            checkSuccs();
            updateScore(score, bestScore);
            suggest(config.bestPlay());
        }
    }

    /**
     * retrieve and sort successor boards, and save the highest score.
     */
    private void checkSuccs() {
        if ((deck.getUndealt().size() < 8) && (config.bestPlay().isRemoval()) ||
                ((deck.getUndealt().size() < 7) && (!config.bestPlay().isRemoval()))) {
            updateScore(score, -100);
            ArrayList<Board> succs = config.getSuccessors(score);
            succs.sort((o1, o2) -> o2.getCurrentScore() - o1.getCurrentScore());
            if (succs.size() > 0)
                bestScore = succs.get(0).getCurrentScore();
        }
    }

    /**
     * Register an object for updates to score.
     *
     * @param scoreObserver some ScoreObserver
     */
    public void registerForUpdates(ScoreObserver scoreObserver) {
        this.observs.add(scoreObserver);
    }

    /**
     * Update all observers of a change to score.
     *
     * @param score    the current score
     * @param maxscore the maximum possible score ( see checkSuccs() )
     */
    @Override
    public void updateScore(int score, int maxscore) {
        for (ScoreObserver so : observs)
            so.updateScore(score, maxscore);
    }
}
