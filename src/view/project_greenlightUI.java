package view;

import controller.ccontroller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Main UI of Project Greenlight.
 *
 * @author Will Clifford (GitHub: wtc8754)
 */
public class project_greenlightUI extends Application implements ScoreObserver {

    /**
     * the main scene of the program.
     */
    private Scene mainscene;

    /**
     * reset button. this is not reset when the reset button is pressed for obvious reasons.
     */
    private Button reset;

    /**
     * a list of all cardViews on the board. used to update their size
     */
    private ArrayList<cardView> cvs = new ArrayList<>();

    /**
     * primary stage of this application. This is stored here to reset the scene.
     */
    private Stage primaryStage;

    /**
     * where the score will be displayed
     */
    private Label scoreboard;

    /**
     * the controller that communicated between this UI and the model
     */
    private ccontroller cont;

    /**
     * used to check if the reset button has been set or not. this prevents making a new reset button.
     */
    private boolean FirstSetup = true;

    /**
     * used for printing out the score.
     */
    private final static String SPACER = "\t\t\t\t\t\t\t\t\t\t";

    /**
     * reset the whole scene, prepare for a clean game. Since this replaces the controller, it also resets the model
     */
    private void reset() {
        cont = new ccontroller();
        cont.registerForUpdates(this);
        menuView mv = new menuView(this);
        VBox mainBox = new VBox() {{
            setStyle("-fx-background-color: #000000");
            getChildren().addAll(mv, setupCards(), setupControl());
        }};
        mainscene = new Scene(mainBox);
        primaryStage.setScene(mainscene);
    }

    /**
     * set up the control buttons (i.e. reset and next) and the scoreboard
     *
     * @return an HBox containing the control buttons and the scoreboard
     */
    private HBox setupControl() {
        scoreboard = new Label();
        scoreboard.setFont(new Font("Arial", 14));
        scoreboard.setPrefSize(603, 30);
        scoreboard.setPadding(new Insets(0, 10, 0, 10));
        updateScore(0, -1);

        Button next = new Button("Next");
        next.setOnAction(o -> cont.play());
        next.setPrefSize(60, 30);

        if (FirstSetup) {
            reset = new Button("RESET");
            reset.setOnAction(o -> reset());
            reset.setPrefSize(60, 30);
            FirstSetup = false;
        }

        HBox control = new HBox();
        control.getChildren().addAll(reset, scoreboard, next);
        control.setPadding(new Insets(3, 6, 6, 6));
        return control;
    }

    /**
     * set up all the cardViews
     *
     * @return an HBox containing all the cardViews
     */
    private HBox setupCards() {
        HBox cards = new HBox();
        cvs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cardView cv = new cardView(cont);
            cards.getChildren().add(cv);
            cvs.add(cv);
        } return cards;
    }

    /**
     * update the size of the UI
     *
     * @param buttsize the dimension of a button
     */
    void updateSize(int buttsize) {
        for (cardView cv : cvs)
            cv.setSize(buttsize);
        scoreboard.setPrefSize((buttsize * 15) - 72, 30);
        primaryStage.close();
        primaryStage.setScene(mainscene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    /**
     * display the score on the scoreboard.
     *
     * @param score current score
     * @param maxscore the highest possible score, if computable. -1 if not.
     */
    @Override
    public void updateScore(int score, int maxscore) {
        scoreboard.setTextFill(new Color(1, 1, 1, 1));

        String spcr;
        if (score - 99 > 0)
            spcr = SPACER;
        else if (score - 9 > 0)
            spcr = SPACER;
        else
            spcr = SPACER + "\t";

        if (maxscore == -1) {
            scoreboard.setText("Score : " + score + spcr + "Highest achievable : unknown");
        } else if (maxscore == -100) {
            scoreboard.setText("Score : " + score + spcr + "Highest achievable : calculating...");
        } else if (maxscore < 300) {
            scoreboard.setTextFill(new Color(1, 0, 0, 1));
            scoreboard.setText("Score : " + score + spcr + "Highest achievable : " + maxscore);
        } else {
            scoreboard.setText("Score : " + score + spcr + "Highest achievable : " + maxscore);
        }
    }

    /**
     * Initialize the stage/scene, show the stage
     *
     * @param primaryStage main stage of the program.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        reset();
        primaryStage.setScene(mainscene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
