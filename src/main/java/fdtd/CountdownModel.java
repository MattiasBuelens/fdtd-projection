package fdtd;

import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface CountdownModel {

    ObservableIntegerValue yearProperty();

    ObservableObjectValue<LocalDateTime> nowProperty();

    ObservableObjectValue<LocalDateTime> newYearProperty();

    ObservableObjectValue<Duration> differenceProperty();

    static LocalDateTime getNewYearDate(int year) {
        return LocalDateTime.of(LocalDate.ofYearDay(year, 1), LocalTime.MIDNIGHT);
    }

}
