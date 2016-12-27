package fdtd;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.time.Duration;

public class TimedMessageImpl implements TimedMessage {

    private final StringProperty message = new SimpleStringProperty();
    private final ObjectProperty<Duration> start = new SimpleObjectProperty<>();

    public TimedMessageImpl(String message, Duration startOffset) {
        this.message.set(message);
        this.start.set(startOffset);
    }

    @Override
    public final ObservableValue<String> messageProperty() {
        return message;
    }

    @Override
    public final ObservableValue<Duration> startOffsetProperty() {
        return start;
    }

}
