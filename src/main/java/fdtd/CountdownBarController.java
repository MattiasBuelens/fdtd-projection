package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fxmisc.easybind.EasyBind;

import java.util.Locale;

public class CountdownBarController extends ScreenController {

    @FXML
    private Pane barRoot;

    @FXML
    private Text countdownCelebrating;

    @FXML
    private Text countdownYear;

    @FXML
    private Text countdownDirection;

    @FXML
    private Text countdownTime;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();

    private final ObservableValue<String> countdownYearString;
    private final ObservableValue<String> countdownDirectionString;
    private final ObservableValue<String> countdownTimeString;

    public CountdownBarController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);

        countdownYearString = Bindings.format(Locale.US, "%d", yearProperty());
        countdownDirectionString = EasyBind.map(isNewYearProperty(), (isNewYear) -> isNewYear ? " since " : " in ");
        countdownTimeString = EasyBind.map(secondsUntilNewYearProperty(), Number::longValue).map(this::formatTime);
    }

    public void initialize() {
        countdownCelebrating.visibleProperty().bind(isNewYearProperty());
        countdownYear.textProperty().bind(countdownYearString);
        countdownDirection.textProperty().bind(countdownDirectionString);
        countdownTime.textProperty().bind(countdownTimeString);
    }

    private String formatTime(long seconds) {
        seconds = Math.abs(seconds);
        long hours = seconds / 3600;
        long minutes = (seconds / 60) % 60;
        seconds = seconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public ObservableValue<ScreenVisibility> screenVisibilityProperty() {
        return screenVisibility.getReadOnlyProperty();
    }

    @Override
    public BooleanProperty visibleProperty() {
        return barRoot.visibleProperty();
    }

}
