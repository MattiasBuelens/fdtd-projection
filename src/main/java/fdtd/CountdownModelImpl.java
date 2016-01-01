package fdtd;

import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;
import java.time.LocalDateTime;

public class CountdownModelImpl implements CountdownModel {

    private final ObjectProperty<LocalDateTime> now = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final IntegerBinding year;

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final ObjectBinding<Duration> difference;

    private final LongBinding secondsUntilNewYear;
    private final LongBinding secondsSinceNewYear;
    private final BooleanBinding isNewYear;

    public CountdownModelImpl() {
        year = Bindings.createIntegerBinding(() -> newYearProperty().get().getYear(), newYearProperty());
        difference = Bindings.createObjectBinding(() -> Duration.between(now.get(), newYearProperty().get()), now, newYearProperty());

        secondsUntilNewYear = Bindings.createLongBinding(() -> timeUntilNewYearProperty().get().getSeconds(), timeUntilNewYearProperty());
        secondsSinceNewYear = secondsUntilNewYear.negate();
        isNewYear = secondsSinceNewYear.greaterThanOrEqualTo(0d);
    }

    public CountdownModelImpl(LocalDateTime newYear) {
        this();
        newYearProperty().set(newYear);
    }

    public CountdownModelImpl(int year) {
        this(CountdownModel.getNewYearDate(year));
    }

    @Override
    public final IntegerExpression yearProperty() {
        return year;
    }

    @Override
    public final ObjectProperty<LocalDateTime> nowProperty() {
        return now;
    }

    @Override
    public final ObjectProperty<LocalDateTime> newYearProperty() {
        return newYear;
    }

    @Override
    public final ObservableObjectValue<Duration> timeUntilNewYearProperty() {
        return difference;
    }

    @Override
    public LongExpression secondsUntilNewYearProperty() {
        return secondsUntilNewYear;
    }

    @Override
    public LongExpression secondsSinceNewYearProperty() {
        return secondsSinceNewYear;
    }

    @Override
    public BooleanExpression isNewYearProperty() {
        return isNewYear;
    }

}
