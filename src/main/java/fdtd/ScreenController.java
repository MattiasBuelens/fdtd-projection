package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;

/**
 * Abstract controller for a screen.
 * <p>
 * The {@link MainController} will bind the countdown time properties when initializing its screens,
 * allowing all screen controllers to observe the countdown state.
 */
public abstract class ScreenController {

    private final ObjectProperty<Duration> timeUntilNewYear = new SimpleObjectProperty<>(Duration.ZERO);
    private final IntegerProperty year = new SimpleIntegerProperty(0);

    private final LongBinding secondsUntilNewYear;
    private final LongBinding secondsSinceNewYear;
    private final BooleanBinding isNewYear;

    protected ScreenController() {
        secondsUntilNewYear = Bindings.createLongBinding(() -> timeUntilNewYear.get().getSeconds(), timeUntilNewYear);
        secondsSinceNewYear = secondsUntilNewYear.negate();
        isNewYear = secondsSinceNewYear.greaterThanOrEqualTo(0d);
    }

    public final ScreenVisibility getScreenVisibility() {
        return screenVisibilityProperty().get();
    }

    /**
     * The screen's desired visibility.
     */
    public abstract ObservableObjectValue<ScreenVisibility> screenVisibilityProperty();

    public final boolean isVisible() {
        return visibleProperty().get();
    }

    public final void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }

    /**
     * Property to show or hide this screen.
     */
    public abstract BooleanProperty visibleProperty();

    public final Duration getTimeUntilNewYear() {
        return timeUntilNewYear.get();
    }

    public final ObjectProperty<Duration> timeUntilNewYearProperty() {
        return timeUntilNewYear;
    }

    public final void setTimeUntilNewYear(Duration timeUntilNewYear) {
        this.timeUntilNewYear.set(timeUntilNewYear);
    }

    public final int getYear() {
        return yearProperty().get();
    }

    public final IntegerProperty yearProperty() {
        return year;
    }

    public final void setYear(int year) {
        yearProperty().set(year);
    }

    public final long getSecondsUntilNewYear() {
        return secondsUntilNewYearProperty().get();
    }

    public final LongBinding secondsUntilNewYearProperty() {
        return secondsUntilNewYear;
    }

    public final long getSecondsSinceNewYear() {
        return secondsSinceNewYearProperty().get();
    }

    public final LongBinding secondsSinceNewYearProperty() {
        return secondsSinceNewYear;
    }

    public final boolean isNewYear() {
        return isNewYearProperty().get();
    }

    public final BooleanBinding isNewYearProperty() {
        return isNewYear;
    }

}
