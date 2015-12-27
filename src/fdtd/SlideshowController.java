package fdtd;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Locale;

public class SlideshowController extends ScreenController {

    @FXML
    private Pane slideshowRoot;

    @FXML
    private Text countdownCelebrating;

    @FXML
    private Text countdownYear;

    @FXML
    private Text countdownDirection;

    @FXML
    private Text countdownTime;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();

    private final ObservableStringValue countdownYearString;
    private final ObservableStringValue countdownDirectionString;
    private final ObservableStringValue countdownTimeString;

    public SlideshowController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);

        countdownYearString = Bindings.format(Locale.US, "%d", yearProperty());
        countdownDirectionString = Bindings.createStringBinding(() -> isNewYear() ? " since " : " in ", isNewYearProperty());
        countdownTimeString = Bindings.createStringBinding(() -> formatTime(getSecondsUntilNewYear()), secondsUntilNewYearProperty());
    }

    public void initialize() {
        image1.fitWidthProperty().bind(slideshowRoot.widthProperty());
        image1.fitHeightProperty().bind(slideshowRoot.heightProperty());
        image2.fitWidthProperty().bind(slideshowRoot.widthProperty());
        image2.fitHeightProperty().bind(slideshowRoot.heightProperty());

        countdownCelebrating.visibleProperty().bind(isNewYearProperty());
        countdownYear.textProperty().bind(countdownYearString);
        countdownDirection.textProperty().bind(countdownDirectionString);
        countdownTime.textProperty().bind(countdownTimeString);
    }

    private String formatTime(long seconds) {
        seconds = Math.abs(seconds);
        long hours = (seconds / 3600) % 60;
        long minutes = (seconds / 60) % 60;
        seconds = seconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public ObservableObjectValue<ScreenVisibility> screenVisibilityProperty() {
        return screenVisibility.getReadOnlyProperty();
    }

    @Override
    public BooleanProperty visibleProperty() {
        return slideshowRoot.visibleProperty();
    }

}
