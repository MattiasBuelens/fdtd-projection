package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CountdownModelImpl implements CountdownModel {

    private final ObjectProperty<LocalDateTime> now = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final IntegerBinding year;

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final ObjectBinding<Duration> difference;

    public CountdownModelImpl() {
        year = Bindings.createIntegerBinding(() -> newYearProperty().get().getYear(), newYearProperty());
        difference = Bindings.createObjectBinding(() -> Duration.between(now.get(), newYearProperty().get()), now, newYearProperty());
    }

    public CountdownModelImpl(LocalDateTime newYear) {
        this();
        newYearProperty().set(newYear);
    }

    public CountdownModelImpl(int year) {
        this(CountdownModel.getNewYearDate(year));
    }

    @Override
    public final ObservableIntegerValue yearProperty() {
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
    public final ObservableObjectValue<Duration> differenceProperty() {
        return difference;
    }

}
