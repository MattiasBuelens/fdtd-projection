package fdtd;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SlideshowController extends ScreenController {

    @FXML
    private Pane slideshowRoot;

    @FXML
    private Parent countdownBar;

    @FXML
    private CountdownBarController countdownBarController;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();


    public SlideshowController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);
    }

    public void initialize() {
        countdownBarController.visibleProperty().bind(visibleProperty());
        countdownBarController.yearProperty().bind(yearProperty());
        countdownBarController.timeUntilNewYearProperty().bind(timeUntilNewYearProperty());

        image1.fitWidthProperty().bind(slideshowRoot.widthProperty());
        image1.fitHeightProperty().bind(slideshowRoot.heightProperty());
        image2.fitWidthProperty().bind(slideshowRoot.widthProperty());
        image2.fitHeightProperty().bind(slideshowRoot.heightProperty());
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
