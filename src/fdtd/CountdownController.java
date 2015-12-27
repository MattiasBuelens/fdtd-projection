package fdtd;

import fdtd.fireworks.FireworksPane;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import java.util.Locale;

public class CountdownController extends ScreenController {

    private static final int TIME_START = 60; // in seconds before new year
    private static final int TIME_START_FINALE = 10; // in seconds before new year
    private static final int TIME_END = 5; // in seconds after new year

    private static final String CSS_CLASS_FINALE = "is-finale";
    private static final String CSS_CLASS_NEWYEAR = "is-newyear";

    // region Fields

    @FXML
    private Parent countdownRoot;

    @FXML
    private Text counterFill;

    @FXML
    public Text counterStroke;

    @FXML
    private Text newYearText;

    @FXML
    private FireworksPane fireworks;

    private final ObservableObjectValue<ScreenVisibility> screenVisibility;

    private final ObservableStringValue countdownString;
    private final BooleanBinding isFinale;

    private Animation finaleAnimation;

    // endregion

    public CountdownController() {
        countdownString = Bindings.format(Locale.US, "%d", secondsUntilNewYearProperty());

        // finale if 0 < seconds until new year <= finale start
        isFinale = secondsUntilNewYearProperty().lessThanOrEqualTo(TIME_START_FINALE).and(isNewYearProperty().not());

        // must show only between start time and end time
        BooleanBinding mustShow = secondsUntilNewYearProperty().lessThanOrEqualTo(TIME_START).and(secondsSinceNewYearProperty().lessThanOrEqualTo(TIME_END));
        screenVisibility = Bindings.createObjectBinding(() -> mustShow.get() ? ScreenVisibility.MUST_SHOW : ScreenVisibility.MUST_HIDE, mustShow);

        secondsUntilNewYearProperty().addListener((observable, oldValue, newValue) -> playFinaleAnimation());
    }

    public void initialize() {
        counterFill.textProperty().bind(countdownString);
        counterStroke.textProperty().bind(countdownString);

        bindStyleClass(CSS_CLASS_FINALE, isFinale);
        bindStyleClass(CSS_CLASS_NEWYEAR, isNewYearProperty());

        finaleAnimation = createFinaleAnimation();

        isNewYearProperty().addListener((observable, oldValue, newValue) -> playFireworks());
        visibleProperty().addListener((observable, oldValue, newValue) -> playFireworks());
    }

    @Override
    public ObservableObjectValue<ScreenVisibility> screenVisibilityProperty() {
        return screenVisibility;
    }

    private Animation createFinaleAnimation() {
        FadeTransition hideStroke = new FadeTransition(javafx.util.Duration.millis(0), counterStroke);
        hideStroke.setFromValue(0d);
        hideStroke.setToValue(0d);

        ScaleTransition scaleFill = new ScaleTransition(javafx.util.Duration.millis(100), counterFill);
        scaleFill.setFromX(0d);
        scaleFill.setFromY(0d);
        scaleFill.setToX(1d);
        scaleFill.setToY(1d);

        ScaleTransition scaleStroke = new ScaleTransition(javafx.util.Duration.millis(300), counterStroke);
        scaleStroke.setFromX(1d);
        scaleStroke.setFromY(1d);
        scaleStroke.setToX(3d);
        scaleStroke.setToY(3d);

        FadeTransition fadeStroke = new FadeTransition(javafx.util.Duration.millis(300), counterStroke);
        fadeStroke.setFromValue(1d);
        fadeStroke.setToValue(0d);

        Animation animateStroke = new ParallelTransition(scaleStroke, fadeStroke);

        return new SequentialTransition(hideStroke, scaleFill, animateStroke);
    }

    private void playFinaleAnimation() {
        if (isFinale.get()) {
            finaleAnimation.playFromStart();
        }
    }

    private void playFireworks() {
        if (isNewYear() && isVisible()) {
            fireworks.play();
        } else {
            fireworks.stop();
        }
    }

    private void bindStyleClass(String cssClass, ObservableBooleanValue toggle) {
        toggle.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                countdownRoot.getStyleClass().add(cssClass);
            } else {
                countdownRoot.getStyleClass().remove(cssClass);
            }
        }));
    }

    @Override
    public BooleanProperty visibleProperty() {
        return countdownRoot.visibleProperty();
    }

}
