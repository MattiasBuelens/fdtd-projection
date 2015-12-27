package fdtd;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;

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
    private Parent priceList;

    @FXML
    private SlideshowController priceListController;

    @FXML
    private Parent countdownBar;

    @FXML
    private CountdownBarController countdownBarController;

    private final CountdownModel countdownModel = new CountdownModel(Main.NEW_YEAR);

    private final ObservableList<ScreenController> screens = FXCollections.observableList(new ArrayList<>());
    private final ObjectProperty<ScreenController> visibleScreen = new SimpleObjectProperty<>();

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

        // Make 1 rem = 1 vmax
        rem.bind(viewportUnits.vmax);
    }

    public void initialize() {
        // Initialize viewport unit calculation
        viewportUnits.initialize(root);

        // Bind root element's font size
        root.styleProperty().bind(rem.asString(Locale.US, "-fx-font-size: %.2f px"));

        // Initialize screens
        screens.add(priceListController);
        screens.add(countdownController);
        for (ScreenController screen : screens) {
            screen.setVisible(false);
            screen.yearProperty().bind(yearProperty());
            screen.timeUntilNewYearProperty().bind(timeUntilNewYearProperty());
        }

        // Initialize countdown bar
        countdownBarController.visibleProperty().bind(countdownController.visibleProperty().not());
        countdownBarController.yearProperty().bind(yearProperty());
        countdownBarController.timeUntilNewYearProperty().bind(timeUntilNewYearProperty());

        // Update countdown every second
        final Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1), (event -> updateModel()))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

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

    // endregion

    // region Properties


    public final double getRem() {
        return rem.get();
    }

    @FXML
    private final ObservableDoubleValue remProperty() {
        return rem.getReadOnlyProperty();
    }

    public final Duration getTimeUntilNewYear() {
        return timeUntilNewYearProperty().get();
    }

    @FXML
    private final ObservableObjectValue<Duration> timeUntilNewYearProperty() {
        return countdownModel.differenceProperty();
    }

    public final int getYear() {
        return yearProperty().get();
    }

    @FXML
    private final ObservableIntegerValue yearProperty() {
        return countdownModel.yearProperty();
    }

    // endregion

}
