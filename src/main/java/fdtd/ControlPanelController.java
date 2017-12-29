package fdtd;

import fdtd.util.Memoizer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.fxmisc.easybind.EasyBind;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class ControlPanelController {

    @FXML
    private Parent root;

    @FXML
    private ChoiceBox<EditionPreset> choiceEditionPreset;

    @FXML
    private ChoiceBox<SlideshowPreset> choiceSlideshowPreset;

    @FXML
    private ChoiceBox<Screen> choiceMonitor;

    @FXML
    private CheckBox checkFullScreen;

    @FXML
    private Slider sliderSlideDuration;

    @FXML
    private Label labelSlideDuration;

    @FXML
    private DateTimePicker datetimeNewYear;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonStop;

    private Stage projectionStage;
    private MainController projectionController;

    private final BooleanProperty projectionRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty projectionFullscreen = new SimpleBooleanProperty(true);

    private final ObjectProperty<EditionPreset> editionPreset = new SimpleObjectProperty<>();
    private final ListProperty<SlideshowPreset> slideshows = new SimpleListProperty<>();
    private final ListProperty<TimedMessage> messages = new SimpleListProperty<>();

    private final ObjectProperty<SlideshowPreset> slideshowPreset = new SimpleObjectProperty<>();
    private final ObjectProperty<Duration> slideDuration = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>();

    private final ObservableList<Screen> screens = Screen.getScreens();
    private final ObjectProperty<Screen> projectionScreen = new SimpleObjectProperty<>();

    public ControlPanelController() {
        // Bind properties
        slideshows.bind(EasyBind
                .select(editionPresetProperty())
                .selectObject(EditionPreset::slideshowsProperty)
                .orElse(FXCollections.emptyObservableList()));
        messages.bind(EasyBind
                .select(editionPresetProperty())
                .selectObject(EditionPreset::messagesProperty)
                .orElse(FXCollections.emptyObservableList()));

        // Default to first edition preset
        setEditionPreset(EditionPreset.values()[0]);

        // Default to first slideshow preset
        setSlideshowPreset(slideshows.get(0));

        // Default slide duration
        setSlideDuration(Duration.seconds(5));

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

        runningProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (newValue) {
                    createProjection();
                } else {
                    destroyProjection();
                }
            }
        });

        screenProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                updateScreen();
                updateFullScreen();
            }
        });

        fullScreenProperty().addListener((observable1, oldValue, newValue) -> {
            if (oldValue != newValue) {
                updateFullScreen();
            }
        });

        slideshowPresetProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                updateSlides();
            }
        });

        // Default new year
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        if (today.getMonth() != Month.JANUARY) {
            year += 1;
        }
        newYear.set(CountdownModel.getNewYearDate(year));
    }

    public void initialize() {
        choiceEditionPreset.setConverter(new EditionPresetConverter());
        choiceEditionPreset.getItems().addAll(EditionPreset.values());
        choiceEditionPreset.valueProperty().bindBidirectional(editionPresetProperty());

        choiceSlideshowPreset.setConverter(new SlideshowPresetConverter());
        choiceSlideshowPreset.itemsProperty().bind(slideshowsProperty());
        choiceSlideshowPreset.valueProperty().bindBidirectional(slideshowPresetProperty());

        choiceMonitor.setConverter(new ScreenConverter());
        choiceMonitor.itemsProperty().set(screens);
        choiceMonitor.valueProperty().bindBidirectional(screenProperty());

        checkFullScreen.selectedProperty().bindBidirectional(fullScreenProperty());

        sliderSlideDuration.setValue(getSlideDuration().toSeconds());
        labelSlideDuration.textProperty().bind(
                Bindings.format("%.0f seconden", sliderSlideDuration.valueProperty())
        );
        slideDurationProperty().bind(
                EasyBind.map(sliderSlideDuration.valueProperty(), Number::doubleValue)
                        .map(Duration::seconds)
        );

        datetimeNewYear.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        datetimeNewYear.dateTimeValueProperty().bindBidirectional(newYear);

        buttonStart.disableProperty().bind(runningProperty());
        buttonStop.disableProperty().bind(runningProperty().not());
    }

    public EditionPreset getEditionPreset() {
        return editionPresetProperty().get();
    }

    public void setEditionPreset(EditionPreset preset) {
        editionPresetProperty().set(preset);
    }

    public ObjectProperty<EditionPreset> editionPresetProperty() {
        return editionPreset;
    }

    public ObservableList<SlideshowPreset> getSlideshows() {
        return slideshowsProperty().get();
    }

    public ListExpression<SlideshowPreset> slideshowsProperty() {
        return slideshows;
    }

    public ObservableList<TimedMessage> getMessages() {
        return messagesProperty().get();
    }

    public ListExpression<TimedMessage> messagesProperty() {
        return messages;
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

    public Duration getSlideDuration() {
        return slideDurationProperty().get();
    }

    public void setSlideDuration(Duration slideDuration) {
        slideDurationProperty().set(slideDuration);
    }

    public ObjectProperty<Duration> slideDurationProperty() {
        return slideDuration;
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

    public boolean isFullScreen() {
        return fullScreenProperty().get();
    }

    public void setFullScreen(boolean fullScreen) {
        fullScreenProperty().set(fullScreen);
    }

    public BooleanProperty fullScreenProperty() {
        return projectionFullscreen;
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
        projectionStage.setScene(new Scene(root, 480, 270)); // 16:9
        projectionStage.setFullScreenExitHint("");
        projectionStage.fullScreenProperty().addListener(this::onStageFullScreenChanged);
        projectionStage.setOnCloseRequest(event -> destroyProjection());

        projectionController = fxmlLoader.getController();
        projectionController.messagesProperty().bind(messagesProperty());
        projectionController.slideDurationProperty().bind(slideDurationProperty());
        projectionController.newYearProperty().bind(newYear);
        updateSlides();

        projectionStage.show();
        updateScreen();
        updateFullScreen();
    }

    private void destroyProjection() {
        if (projectionController != null) {
            projectionController.dispose();
            projectionController = null;
        }

        if (projectionStage != null) {
            projectionStage.fullScreenProperty().removeListener(this::onStageFullScreenChanged);
            projectionStage.close();
            projectionStage = null;
        }

        projectionRunning.set(false);
    }

    private void updateScreen() {
        Screen screen = getScreen();
        if (screen != null && projectionStage != null) {
            // center in screen
            Rectangle2D bounds = screen.getVisualBounds();
            projectionStage.setX(bounds.getMinX() + (bounds.getWidth() - projectionStage.getWidth()) / 2d);
            projectionStage.setY(bounds.getMinY() + (bounds.getHeight() - projectionStage.getHeight()) / 2d);
        }
    }

    private void updateFullScreen() {
        if (projectionStage != null && isFullScreen() != projectionStage.isFullScreen()) {
            projectionStage.setFullScreen(isFullScreen());
        }
    }

    private void onStageFullScreenChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        setFullScreen(newValue);
    }

    private void updateSlides() {
        if (projectionController != null) {
            projectionController.slidesProperty().set(getPresetSlides(getSlideshowPreset()));
        }
    }

    private static ObservableList<Image> getPresetSlides(SlideshowPreset preset) {
        return EasyBind.map(preset.getImageURLs(), Memoizer.memoize(ControlPanelController::createImage));
    }

    private static Image createImage(URL imageURL) {
        return new Image(imageURL.toExternalForm(), true);
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

    private class EditionPresetConverter extends StringConverter<EditionPreset> {

        @Override
        public String toString(EditionPreset preset) {
            return preset.getTitle();
        }

        @Override
        public EditionPreset fromString(String title) {
            return EditionPreset.byTitle(title);
        }

    }

    private class SlideshowPresetConverter extends StringConverter<SlideshowPreset> {

        @Override
        public String toString(SlideshowPreset preset) {
            return preset.getTitle();
        }

        @Override
        public SlideshowPreset fromString(String title) {
            return getEditionPreset().getSlideshowByTitle(title);
        }

    }

    private class ScreenConverter extends StringConverter<Screen> {

        @Override
        public String toString(Screen screen) {
            return getScreenName(screen, screens.indexOf(screen));
        }

        @Override
        public Screen fromString(String string) {
            return null;
        }

    }

}
