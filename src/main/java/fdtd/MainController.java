package fdtd;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainController {

    // region Fields

    @FXML
    private Parent root;

    @FXML
    private Parent countdown;

    @FXML
    private CountdownController countdownController;

    @FXML
    private Parent slideshow;

    @FXML
    private SlideshowController slideshowController;

    @FXML
    private Parent countdownBar;

    @FXML
    private CountdownBarController countdownBarController;

    private final CountdownModelImpl countdownModel = new CountdownModelImpl();
    private final Timeline countdownClock;

    private final ObservableList<ScreenController> screens = FXCollections.observableArrayList();
    private final ObjectProperty<ScreenController> visibleScreen = new SimpleObjectProperty<>();

    private final SlideshowModel slideshowModel = new SlideshowModel();
    private final ObjectProperty<javafx.util.Duration> slideDuration = new SimpleObjectProperty<>(javafx.util.Duration.seconds(5));

    private final ViewportUnits viewportUnits = new ViewportUnits();
    private final ReadOnlyDoubleWrapper rem = new ReadOnlyDoubleWrapper(16d);

    // endregion

    public MainController() {
        // Screens
        screens.addListener(this::onScreensListChange);

        visibleScreen.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.setVisible(false);
            }
            if (newValue != null) {
                newValue.setVisible(true);
            }
        });

        // Update countdown a few times per second
        countdownClock = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(250), event -> updateModel())
        );
        countdownClock.setCycleCount(Animation.INDEFINITE);

        // Make 1 rem = 1 vmax
        rem.bind(viewportUnits.vmax);
    }

    public void initialize() {
        // Initialize viewport unit calculation
        viewportUnits.initialize(root);

        // Bind root element's font size
        root.styleProperty().bind(rem.asString(Locale.US, "-fx-font-size: %.2f px"));

        // Initialize screens
        screens.add(slideshowController);
        screens.add(countdownController);
        for (ScreenController screen : screens) {
            screen.setVisible(false);
            screen.setCountdownModel(countdownModel);
        }

        // Initialize countdown bar
        countdownBarController.visibleProperty().bind(countdownController.visibleProperty().not());
        countdownBarController.setCountdownModel(countdownModel);

        // Initialize slideshow
        slideshowController.setSlideshowModel(slideshowModel);
        slideshowController.slideDurationProperty().bind(slideDurationProperty());

        // Start ticking
        countdownClock.play();

        // Initial update
        updateModel();
    }

    private void updateModel() {
        countdownModel.nowProperty().set(LocalDateTime.now());
    }

    // region Screen visibility

    /**
     * Switch to the next screen.
     */
    private void updateScreen() {
        List<ScreenController> candidateScreens = new ArrayList<>(screens.size());

        for (ScreenController screen : screens) {
            ScreenVisibility screenVisibility = screen.getScreenVisibility();
            if (screenVisibility == ScreenVisibility.MUST_SHOW) {
                // must show
                visibleScreen.set(screen);
                return;
            } else if (screenVisibility.canShow()) {
                // can show
                candidateScreens.add(screen);
            }
        }

        if (candidateScreens.isEmpty()) {
            // nothing to show
            visibleScreen.set(null);
            return;
        }

        int index = candidateScreens.indexOf(visibleScreen.get());
        if (index >= 0) {
            // next screen
            index = (index + 1) % candidateScreens.size();
        } else if (!candidateScreens.isEmpty()) {
            // first screen
            index = 0;
        }
        visibleScreen.set(candidateScreens.get(index));
    }

    private void onScreenVisibilityChange(ObservableValue<? extends ScreenVisibility> observable, ScreenVisibility oldValue, ScreenVisibility newValue) {
        updateScreen();
    }

    private void onScreensListChange(ListChangeListener.Change<? extends ScreenController> change) {
        while (change.next()) {
            for (ScreenController screen : change.getRemoved()) {
                screen.screenVisibilityProperty().removeListener(this::onScreenVisibilityChange);
            }
            for (ScreenController screen : change.getAddedSubList()) {
                screen.screenVisibilityProperty().addListener(this::onScreenVisibilityChange);
            }
        }
    }

    public void dispose() {
        // Stop ticking
        countdownClock.stop();

        // Hide screens
        for (ScreenController screen : screens) {
            screen.setVisible(false);
        }
    }

    // endregion

    // region Properties


    public final double getRem() {
        return rem.get();
    }

    public final ObservableDoubleValue remProperty() {
        return rem.getReadOnlyProperty();
    }

    public final Duration getTimeUntilNewYear() {
        return timeUntilNewYearProperty().get();
    }

    public final ObservableObjectValue<Duration> timeUntilNewYearProperty() {
        return countdownModel.timeUntilNewYearProperty();
    }

    public final LocalDateTime getNewYear() {
        return newYearProperty().get();
    }

    public final void setNewYear(LocalDateTime newYear) {
        newYearProperty().set(newYear);
    }

    public final ObjectProperty<LocalDateTime> newYearProperty() {
        return countdownModel.newYearProperty();
    }

    public final int getYear() {
        return yearProperty().get();
    }

    public final ObservableIntegerValue yearProperty() {
        return countdownModel.yearProperty();
    }

    public final ObservableList<Image> getSlides() {
        return slidesProperty().get();
    }

    public final void setSlides(ObservableList<Image> slides) {
        slidesProperty().set(slides);
    }

    public final ListProperty<Image> slidesProperty() {
        return slideshowModel.slidesProperty();
    }

    public javafx.util.Duration getSlideDuration() {
        return slideDurationProperty().get();
    }

    public void setSlideDuration(javafx.util.Duration slideDuration) {
        slideDurationProperty().set(slideDuration);
    }

    public ObjectProperty<javafx.util.Duration> slideDurationProperty() {
        return slideDuration;
    }

    // endregion

}
