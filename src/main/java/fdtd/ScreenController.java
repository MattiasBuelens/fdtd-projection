package fdtd;

import javafx.beans.binding.Binding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.LongExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import org.fxmisc.easybind.EasyBind;

import java.time.Duration;

/**
 * Abstract controller for a screen.
 * <p>
 * The {@link MainController} will bind the countdown model when initializing its screens,
 * allowing all screen controllers to observe the countdown state.
 */
public abstract class ScreenController {

    private final ObjectProperty<CountdownModel> countdownModel = new SimpleObjectProperty<>();

    private final IntegerExpression year;
    private final Binding<Duration> timeUntilNewYear;
    private final LongExpression secondsUntilNewYear;
    private final LongExpression secondsSinceNewYear;
    private final BooleanExpression isNewYear;

    protected ScreenController() {
        year = IntegerExpression.integerExpression(EasyBind
                .select(countdownModelProperty())
                .selectObject(CountdownModel::yearProperty));
        timeUntilNewYear = EasyBind
                .select(countdownModelProperty())
                .selectObject(CountdownModel::timeUntilNewYearProperty);
        secondsUntilNewYear = LongExpression.longExpression(EasyBind
                .select(countdownModelProperty())
                .selectObject(CountdownModel::secondsUntilNewYearProperty));
        secondsSinceNewYear = LongExpression.longExpression(EasyBind
                .select(countdownModelProperty())
                .selectObject(CountdownModel::secondsSinceNewYearProperty));
        isNewYear = BooleanExpression.booleanExpression(EasyBind
                .select(countdownModelProperty())
                .selectObject(CountdownModel::isNewYearProperty));
    }

    /**
     * The screen's desired visibility.
     */
    public final ScreenVisibility getScreenVisibility() {
        return screenVisibilityProperty().get();
    }

    public abstract ObservableObjectValue<ScreenVisibility> screenVisibilityProperty();

    /**
     * The screen's actual visibility.
     */
    public final boolean isVisible() {
        return visibleProperty().get();
    }

    public final void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }

    public abstract BooleanProperty visibleProperty();

    // region Countdown model properties

    public final Duration getTimeUntilNewYear() {
        return timeUntilNewYear.getValue();
    }

    public final ObservableValue<Duration> timeUntilNewYearProperty() {
        return timeUntilNewYear;
    }

    public final int getYear() {
        return yearProperty().get();
    }

    public final IntegerExpression yearProperty() {
        return year;
    }

    public final long getSecondsUntilNewYear() {
        return secondsUntilNewYearProperty().get();
    }

    public final LongExpression secondsUntilNewYearProperty() {
        return secondsUntilNewYear;
    }

    public final long getSecondsSinceNewYear() {
        return secondsSinceNewYearProperty().get();
    }

    public final LongExpression secondsSinceNewYearProperty() {
        return secondsSinceNewYear;
    }

    public final boolean isNewYear() {
        return isNewYearProperty().get();
    }

    public final BooleanExpression isNewYearProperty() {
        return isNewYear;
    }

    // endregion

    /**
     * Countdown model.
     */
    public final CountdownModel getCountdownModel() {
        return countdownModelProperty().get();
    }

    public final void setCountdownModel(CountdownModel countdownModel) {
        countdownModelProperty().set(countdownModel);
    }

    public final ObjectProperty<CountdownModel> countdownModelProperty() {
        return countdownModel;
    }

}
