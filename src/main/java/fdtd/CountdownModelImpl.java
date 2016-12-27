package fdtd;

import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.fxmisc.easybind.EasyBind;

import java.time.Duration;
import java.time.LocalDateTime;

public class CountdownModelImpl implements CountdownModel {

    private final ObjectProperty<LocalDateTime> now = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final IntegerExpression year;

    private final ObjectProperty<LocalDateTime> newYear = new SimpleObjectProperty<>(LocalDateTime.MIN);
    private final ObservableValue<Duration> timeUntilNewYear;

    public CountdownModelImpl() {
        year = IntegerExpression.integerExpression(EasyBind.map(newYearProperty(), LocalDateTime::getYear));
        timeUntilNewYear = EasyBind.combine(now, newYearProperty(), Duration::between);
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
    public final ObservableValue<Duration> timeUntilNewYearProperty() {
        return timeUntilNewYear;
    }


}
