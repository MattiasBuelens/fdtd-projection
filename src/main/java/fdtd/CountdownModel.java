package fdtd;

import javafx.beans.binding.IntegerExpression;
import javafx.beans.value.ObservableValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface CountdownModel {

    IntegerExpression yearProperty();

    ObservableValue<LocalDateTime> nowProperty();

    ObservableValue<LocalDateTime> newYearProperty();

    ObservableValue<Duration> timeUntilNewYearProperty();

    static LocalDateTime getNewYearDate(int year) {
        return LocalDateTime.of(LocalDate.ofYearDay(year, 1), LocalTime.MIDNIGHT);
    }

}
