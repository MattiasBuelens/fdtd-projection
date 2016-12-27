package fdtd;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class MessageBarController extends ScreenController {

    @FXML
    private Pane barRoot;

    @FXML
    private Text message;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();

    private final ListProperty<TimedMessage> messages = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TimedMessage> sortedMessages = new SimpleListProperty<>(messages.sorted(TimedMessage.COMPARATOR));

    private final MonadicBinding<TimedMessage> currentMessage;
    private final MonadicBinding<String> currentMessageString;

    public MessageBarController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);

        currentMessage = EasyBind.combine(sortedMessages, timeSinceNewYearProperty(), this::getMessageAtOffset);
        currentMessageString = currentMessage.map(TimedMessage::getMessage);
    }

    public void initialize() {
        message.textProperty().bind(currentMessageString);
    }

    private static boolean isOffsetBetweenMessages(@NotNull Duration offset, @NotNull TimedMessage leftMessage, @Nullable TimedMessage rightMessage) {
        return leftMessage.getStartOffset().compareTo(offset) <= 0
                && (rightMessage == null || rightMessage.getStartOffset().compareTo(offset) > 0);
    }

    private TimedMessage getMessageAtOffset(ObservableList<TimedMessage> messages, Duration offset) {
        int size = messages.size();
        for (int i = 0; i < size - 1; i++) {
            TimedMessage message = messages.get(i);
            TimedMessage nextMessage = messages.get(i + 1);
            if (isOffsetBetweenMessages(offset, message, nextMessage)) {
                return message;
            }
        }
        if (size > 0) {
            TimedMessage lastMessage = messages.get(size - 1);
            if (isOffsetBetweenMessages(offset, lastMessage, null)) {
                return lastMessage;
            }
        }
        return null;
    }

    @Override
    public ObservableValue<ScreenVisibility> screenVisibilityProperty() {
        return screenVisibility.getReadOnlyProperty();
    }

    @Override
    public BooleanProperty visibleProperty() {
        return barRoot.visibleProperty();
    }

    public final ObservableList<TimedMessage> getMessages() {
        return messagesProperty().getValue();
    }

    public final void setMessages(ObservableList<TimedMessage> messages) {
        messagesProperty().setValue(messages);
    }

    public final ListProperty<TimedMessage> messagesProperty() {
        return messages;
    }

    public final TimedMessage getCurrentMessage() {
        return currentMessageProperty().getValue();
    }

    public final ObservableValue<TimedMessage> currentMessageProperty() {
        return currentMessage;
    }

}
