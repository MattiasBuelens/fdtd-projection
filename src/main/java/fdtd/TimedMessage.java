package fdtd;

import javafx.beans.value.ObservableValue;

import java.time.Duration;
import java.util.Comparator;

public interface TimedMessage {

    ObservableValue<String> messageProperty();

    ObservableValue<Duration> startOffsetProperty();

    default String getMessage() {
        return messageProperty().getValue();
    }

    default Duration getStartOffset() {
        return startOffsetProperty().getValue();
    }

    Comparator<TimedMessage> COMPARATOR = Comparator
            .comparing(TimedMessage::getStartOffset)
            .thenComparing(TimedMessage::getMessage);

}
