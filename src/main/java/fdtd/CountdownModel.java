package fdtd;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.LongExpression;
import javafx.beans.value.ObservableObjectValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface CountdownModel {

    IntegerExpression yearProperty();

    ObservableObjectValue<LocalDateTime> nowProperty();

    ObservableObjectValue<LocalDateTime> newYearProperty();

    ObservableObjectValue<Duration> timeUntilNewYearProperty();

    LongExpression secondsUntilNewYearProperty();

    LongExpression secondsSinceNewYearProperty();

    BooleanExpression isNewYearProperty();

    static LocalDateTime getNewYearDate(int year) {
        return LocalDateTime.of(LocalDate.ofYearDay(year, 1), LocalTime.MIDNIGHT);
    }

}
