package fdtd;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableIntegerValue;
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

    @FXML
    private MessageBarController messageBarController;

    private final CountdownModelImpl countdownModel = new CountdownModelImpl();
    private final Timeline countdownClock;

    private final ObservableList<ScreenController> screens = FXCollections.observableArrayList();
    private final ObjectProperty<ScreenController> visibleScreen = new SimpleObjectProperty<>();

    private final SlideshowModel slideshowModel = new SlideshowModel();
    private final ObjectProperty<javafx.util.Duration> slideDuration = new SimpleObjectProperty<>(javafx.util.Duration.seconds(5));

    private final ObservableList<TimedMessage> timedMessages = FXCollections.observableArrayList(TimedMessagePreset.MESSAGES);

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

        BooleanExpression barsVisible = countdownController.visibleProperty().not();

        // Initialize countdown bar
        countdownBarController.visibleProperty().bind(barsVisible);
        countdownBarController.setCountdownModel(countdownModel);

        // Initialize message bar
        messageBarController.visibleProperty().bind(barsVisible);
        messageBarController.setCountdownModel(countdownModel);
        messageBarController.setMessages(timedMessages);

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


    public final ObservableDoubleValue remProperty() {
        return rem.getReadOnlyProperty();
    }

    public final ObservableValue<Duration> timeSinceNewYearProperty() {
        return countdownModel.timeSinceNewYearProperty();
    }

    public final ObjectProperty<LocalDateTime> newYearProperty() {
        return countdownModel.newYearProperty();
    }

    public final ObservableIntegerValue yearProperty() {
        return countdownModel.yearProperty();
    }

    public final ListProperty<Image> slidesProperty() {
        return slideshowModel.slidesProperty();
    }

    public ObjectProperty<javafx.util.Duration> slideDurationProperty() {
        return slideDuration;
    }

    // endregion

}
