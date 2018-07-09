package view;

import com.sun.istack.internal.Nullable;
import controller.ccontroller;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.Card;
import model.enums.color;

import java.util.HashMap;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class cardView extends VBox implements Card {

    /**
     * color of this card
     */
    private color col;

    /**
     * number of this card
     */
    private int number;

    /**
     * controller of the game that this card is in.
     */
    private ccontroller cont;

    /**
     * this is used in place of the 9 button in the UI
     */
    private Label placeholder;

    /**
     * the buttons representing the colors of this card
     */
    private HashMap<color, Button> colorButton = new HashMap<>();

    /**
     * the buttons representing the possible numbers of this card
     */
    private Button[][] numberbuttons = new Button[3][3];

    /**
     * box below the card used to indicate when this card is suggested to be a part of a play.
     */
    private Label indicator = new Label();

    /**
     * default button size
     */
    private int button = 45;

    /**
     * make a new cardView
     *
     * @param cont controller of the game that this cardView is a part of.
     */
    cardView(ccontroller cont) {
        this.cont = cont;
        this.setPadding(new Insets(6));
        reset();
    }

    /**
     * reset this card
     */
    public void reset() {
        getChildren().clear();
        setupColor();
        setupNumber();
        setupIndicator();
        setSize(button);
    }

    /**
     * set up the visual color elements, i.e. the color buttons.
     */
    private void setupColor() {
        HBox colorbox = new HBox();

        for (int i = 0; i < 3; i++) {
            Button b = new Button(color.values()[i].toString().substring(0, 1));
            b.setStyle(getStyle(color.values()[i]));
            b.setFont(new Font("Arial", 18));
            int tmp = i;
            b.setOnAction(o -> {
                {
                    setCol(color.values()[tmp]);
                    for (color c : colorButton.keySet()) {
                        if (!col.equals(c)) {
                            colorButton.get(c).setStyle(getStyle(null));
                            colorButton.get(c).setDisable(true);
                        }
                    }
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {
                            if ((x != 2) || (y != 2)) {
                                numberbuttons[x][y].setDisable(false);
                            }
                        }
                    }
                }
            });
            colorButton.put(color.values()[i], b);
            colorbox.getChildren().add(b);
        }
        getChildren().add(colorbox);
    }

    /**
     * set up the visual number elements, i.e. the number buttons.
     */
    private void setupNumber() {
        GridPane numberpane = new GridPane();
        int count = 1;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if ((x != 2) || (y != 2)) {
                    Button b = new Button("" + count);
                    b.setFont(new Font("Arial", 18));
                    b.setDisable(true);
                    int tmp = count;
                    b.setOnAction(o -> cardAdded(tmp));
                    numberpane.add(b, x, y);
                    numberbuttons[x][y] = b;
                    count++;
                }
            }
        }
        placeholder = new Label();
        placeholder.setPrefSize(button, button);
        numberpane.add(placeholder, 2, 2);
        getChildren().add(numberpane);
    }

    /**
     * set up the indicator
     */
    private void setupIndicator() {
        indicator = new Label();
        indicator.setStyle("-fx-background-color: #000000; -fx-border-color: #FFFFFF; -fx-border-width: 2px");
        getChildren().add(indicator);
    }

    /**
     * get the CSS color code for a button given a model.enums.color
     *
     * @param col color to set the button
     * @return the CSS string for that style
     */
    private String getStyle(@Nullable color col) {
        String hexcode;
        if (col == null) {
            hexcode = "#606060";
        } else if (col.equals(color.BLUE)) {
            hexcode = "#0000FF";
        } else if (col.equals(color.RED_)) {
            hexcode = "#FF0000";
        } else if (col.equals(color.GOLD)) {
            hexcode = "#FFD700";
        } else {
            hexcode = "#606060";
        } return "-fx-background-color: " + hexcode;
    }

    /**
     * @return the color of this card
     */
    public color color() {
        return col;
    }

    /**
     * @return the number of this card
     */
    public int number() {
        return number;
    }

    /**
     * set this card's color
     * @param col color to set this card
     */
    private void setCol(color col) {
        this.col = col;
    }

    /**
     * used to update the controller that a card was added to the board, meaning that the color and number of this
     * card is now defined. also updates the buttons to make sure that they aren't accidentally clicked.
     *
     * @param number the number of this card.
     */
    private void cardAdded(int number) {
        this.number = number;
        cont.cardAdded(this);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i != 2) || (j != 2)) {
                    if (Integer.parseInt(numberbuttons[i][j].getText()) != number) {
                        numberbuttons[i][j].setStyle(getStyle(null));
                        numberbuttons[i][j].setDisable(true);
                    }
                }
            }
        }
        for (Button but : colorButton.values())
            but.setOnAction(ignored -> {});
    }

    /**
     * suggest that this card is removed from the board by turning the indicator box red
     */
    public void suggestRemove() {
        indicator.setStyle("-fx-background-color: #FF0000; -fx-border-color: #FFFFFF; -fx-border-width: 2px");
    }

    /**
     * suggest that this card is used in a play by turning the indicator box green
     */
    public void suggestPlay() {
        indicator.setStyle("-fx-background-color: #00FF00; -fx-border-color: #FFFFFF; -fx-border-width: 2px");
    }

    /**
     * reset the size of this card, saving the new size so that it isn't reset to the old size
     *
     * @param buttonsize size to reset the buttons to
     */
    void setSize(int buttonsize) {
        button = buttonsize;
        for (Button b : colorButton.values())
            b.setPrefSize(buttonsize, buttonsize);
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                if ((x != 2) || (y != 2))
                    numberbuttons[x][y].setPrefSize(buttonsize, buttonsize);
        indicator.setPrefSize(buttonsize * 3, 30);
        placeholder.setPrefSize(button, button);
    }
}
