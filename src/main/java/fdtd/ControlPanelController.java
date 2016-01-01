package fdtd;

import fdtd.util.MappedList;
import fdtd.util.Memoizer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class ControlPanelController {

    @FXML
    private Parent root;

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
    private DatePicker dateNewYear;

    @FXML
    private TextField timeNewYear;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonStop;

    private Stage projectionStage;
    private MainController projectionController;

    private final BooleanProperty projectionRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty projectionFullscreen = new SimpleBooleanProperty(true);

    private final ObjectProperty<SlideshowPreset> slideshowPreset = new SimpleObjectProperty<>();
    private final ObjectProperty<Duration> slideDuration = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> newYearDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> newYearTime = new SimpleObjectProperty<>();
    private final StringProperty newYearTimeString = new SimpleStringProperty();

    private final DateTimeFormatter timeFormat = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter();

    private final ObservableList<Screen> screens = Screen.getScreens();
    private final ObjectProperty<Screen> projectionScreen = new SimpleObjectProperty<>();

    public ControlPanelController() {
        // Default to first slideshow preset
        setSlideshowPreset(SlideshowPreset.values()[0]);

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

        newYear.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                newYearDate.set(newValue.toLocalDate());
                newYearTime.set(newValue.toLocalTime());
            }
        });

        newYearDate.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                newYear.set(LocalDateTime.of(newValue, newYearTime.get()));
            }
        });

        newYearTime.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                newYear.set(LocalDateTime.of(newYearDate.get(), newValue));
                newYearTimeString.set(timeFormat.format(newValue));
            }
        });

        newYearTimeString.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                try {
                    newYearTime.set(timeFormat.parse(newValue, LocalTime::from));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        // Default new year
        newYear.set(CountdownModel.getNewYearDate(Main.NEW_YEAR));
        newYearDate.set(newYear.get().toLocalDate());
        newYearTime.set(newYear.get().toLocalTime());
        newYearTimeString.set(timeFormat.format(newYearTime.get()));
    }

    public void initialize() {
        choiceSlideshowPreset.setConverter(new SlideshowPresetConverter());
        choiceSlideshowPreset.getItems().addAll(SlideshowPreset.values());
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
                Bindings.createObjectBinding(
                        () -> Duration.seconds(sliderSlideDuration.getValue()),
                        sliderSlideDuration.valueProperty()
                )
        );

        dateNewYear.valueProperty().bindBidirectional(newYearDate);
        timeNewYear.textProperty().bindBidirectional(newYearTimeString);

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
        projectionStage.setScene(new Scene(root, 400, 300));
        projectionStage.setFullScreenExitHint("");
        projectionStage.fullScreenProperty().addListener(this::onStageFullScreenChanged);
        projectionStage.setOnCloseRequest(event -> destroyProjection());

        projectionController = fxmlLoader.getController();
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
            projectionController.setSlides(getPresetSlides(getSlideshowPreset()));
        }
    }

    private static ObservableList<Image> getPresetSlides(SlideshowPreset preset) {
        return new MappedList<>(preset.getImageURLs(), Memoizer.memoize(ControlPanelController::createImage));
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

    private class SlideshowPresetConverter extends StringConverter<SlideshowPreset> {

        @Override
        public String toString(SlideshowPreset preset) {
            return preset.getTitle();
        }

        @Override
        public SlideshowPreset fromString(String title) {
            return SlideshowPreset.byTitle(title);
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