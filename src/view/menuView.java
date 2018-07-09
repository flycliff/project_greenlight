package view;

import javafx.scene.control.*;

/**
 * @author Will Clifford (GitHub: wtc8754)
 */
public class menuView extends MenuBar {

    /**
     * the file menu. This is currently unused, but could be used in the future to save a game perhaps.
     */
    private Menu file;

    /**
     * the settings menu
     */
    private Menu settings;

    /**
     * the UI to control with this menu
     */
    private project_greenlightUI UI;

    /**
     * make a new menu.
     *
     * @param UI the UI to control with this menu
     */
    menuView(project_greenlightUI UI) {
        setMB();
        this.UI = UI;
    }

    /**
     * initialize the menu options.
     */
    private void setMB() {
        file = new Menu("File");
        settings = new Menu("Settings");
        setSettings();
        getMenus().addAll(settings);
    }

    /**
     * Initialize the settings menu. Currently, the only settings are to resize the game.
     */
    private void setSettings() {
        Menu size = new Menu("Size");

        MenuItem med   = new MenuItem("Medium");
        med.setOnAction(o -> UI.updateSize(45));
        MenuItem large = new MenuItem("Large");
        large.setOnAction(o -> UI.updateSize(55));
        MenuItem largest = new MenuItem("Largest");
        largest.setOnAction(o -> UI.updateSize(65));

        size.getItems().addAll(med, large, largest);
        settings.getItems().add(size);
    }
}
