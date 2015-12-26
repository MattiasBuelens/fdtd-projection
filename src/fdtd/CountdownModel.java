package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CountdownModel {

    private final ObjectProperty<LocalDateTime> now = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final IntegerProperty year = new SimpleIntegerProperty();

    private final ObjectBinding<LocalDateTime> newYear;
    private final ObjectBinding<Duration> difference;

    public CountdownModel() {
        newYear = Bindings.createObjectBinding(() -> getNewYearDate(year.get()), year);
        difference = Bindings.createObjectBinding(() -> Duration.between(now.get(), newYear.get()), now, newYear);
    }

    public CountdownModel(int initialYear) {
        this();
        year.set(initialYear);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public ObjectProperty<LocalDateTime> nowProperty() {
        return now;
    }

    public ObservableObjectValue<LocalDateTime> newYearProperty() {
        return newYear;
    }

    public ObservableObjectValue<Duration> differenceProperty() {
        return difference;
    }

    private static LocalDateTime getNewYearDate(int year) {
        return LocalDateTime.of(LocalDate.ofYearDay(year, 1), LocalTime.MIDNIGHT);
    }

}
