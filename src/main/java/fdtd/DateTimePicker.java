package fdtd;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Date and time picker control.
 * <p>
 * Original by Edvin Syse from http://stackoverflow.com/a/33953850/1321716
 */
public class DateTimePicker extends DatePicker {

    private final ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty<>();

    private final ObjectProperty<DateTimeFormatter> dateTimeFormatter = new SimpleObjectProperty<>();

    public DateTimePicker() {
        // Synchronize changes to the underlying date value back to the dateTimeValue
        valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate == null) {
                setDateTimeValue(null);
            } else {
                LocalDateTime oldDateTime = getDateTimeValue();
                if (oldDateTime == null) {
                    setDateTimeValue(LocalDateTime.of(newDate, LocalTime.now()));
                } else {
                    setDateTimeValue(LocalDateTime.of(newDate, oldDateTime.toLocalTime()));
                }
            }
        });

        // Synchronize changes to dateTimeValue back to the underlying date value
        dateTimeValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                valueProperty().set(null);
                return;
            }
            valueProperty().set(newValue.toLocalDate());
        });

        // Persist changes on blur
        TextField editor = getEditor();
        editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                simulateEnterPressed();
            }
        });

        // Default date time formatter
        setDateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT));

        // Use date time value and formatter for string conversions
        setConverter(new LocalDateStringConverter());
    }

    private void simulateEnterPressed() {
        TextField editor = getEditor();
        editor.fireEvent(new KeyEvent(editor, editor, KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
    }

    public final LocalDateTime getDateTimeValue() {
        return dateTimeValueProperty().getValue();
    }

    public final void setDateTimeValue(LocalDateTime value) {
        dateTimeValueProperty().setValue(value);
    }

    public final Property<LocalDateTime> dateTimeValueProperty() {
        return dateTimeValue;
    }

    public final DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatterProperty().get();
    }

    public final void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        dateTimeFormatterProperty().set(dateTimeFormatter);
    }

    public final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        return dateTimeFormatter;
    }

    private class LocalDateStringConverter extends StringConverter<LocalDate> {

        @Override
        public String toString(LocalDate localDate) {
            LocalDateTime dateTimeValue = getDateTimeValue();
            if (dateTimeValue == null) {
                return "";
            }
            return getDateTimeFormatter().format(dateTimeValue);
        }

        @Override
        public LocalDate fromString(String string) {
            if (string == null) {
                setDateTimeValue(null);
                return null;
            }

            LocalDateTime newValue = LocalDateTime.parse(string, getDateTimeFormatter());
            setDateTimeValue(newValue);
            return newValue.toLocalDate();
        }

    }

}
