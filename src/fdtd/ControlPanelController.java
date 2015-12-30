package fdtd;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ControlPanelController {

    @FXML
    private Parent root;

    @FXML
    private ComboBox<SlideshowPreset> comboSlideshowPreset;

    @FXML
    private ComboBox<Screen> comboMonitor;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonStop;

    private Stage projectionStage;
    private MainController projectionController;

    private final BooleanProperty projectionRunning = new SimpleBooleanProperty(false);

    private final ObjectProperty<SlideshowPreset> slideshowPreset = new SimpleObjectProperty<>();

    private final ObservableList<Screen> screens = Screen.getScreens();
    private final ObjectProperty<Screen> projectionScreen = new SimpleObjectProperty<>();

    public ControlPanelController() {
        // Default to first slideshow preset
        setSlideshowPreset(SlideshowPreset.values()[0]);

        if (screens.size() > 1) {
            // Default to first non-primary screen
            for (Screen screen : Screen.getScreens()) {
                if (!Screen.getPrimary().equals(screen)) {
                    setScreen(screen);
                    break;
                }
            }
        } else {
            // Default to single screen
            setScreen(Screen.getPrimary());
        }

        projectionRunning.addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (newValue) {
                    createProjection();
                    updateScreen(projectionStage, getScreen());
                    projectionStage.show();
                } else {
                    projectionStage.close();
                }
            }
        });

        screenProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (projectionStage != null) {
                    updateScreen(projectionStage, newValue);
                }
            }
        });

        slideshowPresetProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                // TODO slideshow preset
            }
        });
    }

    public void initialize() {
        comboSlideshowPreset.setButtonCell(new SlideshowPresetListCell());
        comboSlideshowPreset.setCellFactory(param -> new SlideshowPresetListCell());
        comboSlideshowPreset.getItems().addAll(SlideshowPreset.values());
        comboSlideshowPreset.valueProperty().bindBidirectional(slideshowPresetProperty());

        comboMonitor.setButtonCell(new ScreenListCell());
        comboMonitor.setCellFactory(param -> new ScreenListCell());
        comboMonitor.itemsProperty().set(screens);
        comboMonitor.valueProperty().bindBidirectional(screenProperty());

        buttonStart.disableProperty().bind(runningProperty());
        buttonStop.disableProperty().bind(runningProperty().not());
    }

    public SlideshowPreset getSlideshowPreset() {
        return slideshowPresetProperty().get();
    }

    public void setSlideshowPreset(SlideshowPreset preset) {
        slideshowPresetProperty().set(preset);
    }

    public ObjectProperty<SlideshowPreset> slideshowPresetProperty() {
        return slideshowPreset;
    }

    public Screen getScreen() {
        return screenProperty().get();
    }

    public void setScreen(Screen screen) {
        screenProperty().set(screen);
    }

    public ObjectProperty<Screen> screenProperty() {
        return projectionScreen;
    }

    public boolean isRunning() {
        return runningProperty().get();
    }

    public BooleanExpression runningProperty() {
        return projectionRunning;
    }

    public void start() {
        projectionRunning.set(true);
    }

    public void stop() {
        projectionRunning.set(false);
    }

    private void createProjection() {
        FXMLLoader fxmlLoader = new FXMLLoader(ControlPanelController.class.getResource("Main.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        projectionStage = new Stage();
        projectionStage.setTitle("From Dusk Till Dawn - Projectie");
        projectionStage.setScene(new Scene(root, 400, 300));

        projectionController = fxmlLoader.getController();
    }

    private static void updateScreen(Stage stage, Screen screen) {
        if (screen == null) {
            // windowed
            stage.setFullScreen(false);
        } else {
            // full screen
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setFullScreen(true);
        }
    }

    private static String getScreenName(Screen screen, int index) {
        if (Screen.getPrimary().equals(screen)) {
            return String.format("%d - Hoofdscherm (%.0f \u00d7 %.0f)",
                    (index + 1), screen.getBounds().getWidth(), screen.getBounds().getHeight());
        } else {
            return String.format("%d - Scherm (%.0f \u00d7 %.0f)",
                    (index + 1), screen.getBounds().getWidth(), screen.getBounds().getHeight());
        }
    }

    private static class SlideshowPresetListCell extends ListCell<SlideshowPreset> {
        @Override
        protected void updateItem(SlideshowPreset item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                textProperty().unbind();
                textProperty().set(null);
            } else {
                textProperty().bind(item.titleProperty());
            }
        }
    }

    private static class ScreenListCell extends ListCell<Screen> {
        @Override
        protected void updateItem(Screen item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
            } else {
                setText(getScreenName(item, getIndex()));
            }
        }
    }

}
