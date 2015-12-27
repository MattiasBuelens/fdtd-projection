package fdtd;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class SlideshowController extends ScreenController {

    @FXML
    private Parent slideshowRoot;

    @FXML
    private Region image1;

    @FXML
    private Region image2;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();


    public SlideshowController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);
    }

    public void initialize() {
    }

    private static BackgroundSize SIZE_STRETCH = new BackgroundSize(1d, 1d, true, true, false, false);
    private static BackgroundSize SIZE_FIT = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);

    private Background createBackground(Image image) {
        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, SIZE_STRETCH));
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
