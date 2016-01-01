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

public class CountdownModel {

    private final ObjectProperty<LocalDateTime> now = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final IntegerBinding year;

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final ObjectBinding<Duration> difference;

    public CountdownModel(LocalDateTime newYear) {
        newYearProperty().set(newYear);
        year = Bindings.createIntegerBinding(() -> newYearProperty().get().getYear(), newYearProperty());
        difference = Bindings.createObjectBinding(() -> Duration.between(now.get(), newYearProperty().get()), now, newYearProperty());
    }

    public CountdownModel(int year) {
        this(getNewYearDate(year));
    }

    public ObservableIntegerValue yearProperty() {
        return year;
    }

    public ObjectProperty<LocalDateTime> nowProperty() {
        return now;
    }

    public ObjectProperty<LocalDateTime> newYearProperty() {
        return newYear;
    }

    public ObservableObjectValue<Duration> differenceProperty() {
        return difference;
    }

    public static LocalDateTime getNewYearDate(int year) {
        return LocalDateTime.of(LocalDate.ofYearDay(year, 1), LocalTime.MIDNIGHT);
    }

}
