package fdtd;

import javafx.animation.*;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.fxmisc.easybind.EasyBind;

public class SlideshowController extends ScreenController {

    @FXML
    private Parent slideshowRoot;

    @FXML
    private Region imageBack;

    @FXML
    private Region imageFront;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<SlideshowModel> model = new SimpleObjectProperty<>();

    private final ListProperty<Image> slides = new SimpleListProperty<>();
    private final ObjectProperty<Duration> transitionDuration = new SimpleObjectProperty<>(Duration.millis(500));
    private final ObjectProperty<Duration> slideDuration = new SimpleObjectProperty<>(Duration.seconds(5));

    private Animation animation;

    public SlideshowController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);

        slides.bind(EasyBind
                .select(slideshowModelProperty())
                .selectObject(SlideshowModel::slidesProperty)
                .orElse(FXCollections.emptyObservableList()));
    }

    public void initialize() {
        animation = createAnimation();
        animation.setCycleCount(Animation.INDEFINITE);

        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (newValue) {
                    start();
                } else {
                    stop();
                }
            }
        });

        slidesProperty().emptyProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && isVisible()) {
                start();
            }
        });

        slideDurationProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != newValue && animation.getStatus() == Animation.Status.RUNNING) {
                restart();
            }
        }));

        if (isVisible()) {
            start();
        }
    }

    public void start() {
        if (getSlides().isEmpty()) {
            animation.stop();
        } else {
            animation.play();
        }
    }

    public void stop() {
        animation.pause();
    }

    public void restart() {
        animation.stop();
        start();
    }

    private Animation createAnimation() {
        PauseTransition transitionStart = new PauseTransition(Duration.ZERO);
        transitionStart.setOnFinished(event -> onTransitionStart());

        FadeTransition fadeOut = new FadeTransition();
        fadeOut.durationProperty().bind(transitionDurationProperty());
        fadeOut.setToValue(0d);
        fadeOut.setNode(imageFront);

        FadeTransition fadeIn = new FadeTransition();
        fadeIn.durationProperty().bind(transitionDurationProperty());
        fadeIn.setFromValue(0d);
        fadeIn.setToValue(1d);
        fadeIn.setNode(imageBack);

        ParallelTransition crossFade = new ParallelTransition(fadeOut, fadeIn);

        PauseTransition transitionEnd = new PauseTransition(Duration.ZERO);
        transitionEnd.setOnFinished(event -> onTransitionEnd());

        PauseTransition slideEnd = new PauseTransition();
        slideEnd.durationProperty().bind(slideDurationProperty());

        return new SequentialTransition(transitionStart, crossFade, transitionEnd, slideEnd);
    }

    private void onTransitionStart() {
        // get next slide
        getSlideshowModel().nextSlide();

        // prepare slide in background
        imageBack.setBackground(createBackground(getSlideshowModel().getCurrentSlide()));
    }

    private void onTransitionEnd() {
        // swap slides
        imageFront.setBackground(imageBack.getBackground());
        imageFront.setOpacity(1);

        imageBack.setBackground(null);
        imageBack.setOpacity(0);
    }

    private static BackgroundSize SIZE_STRETCH = new BackgroundSize(1d, 1d, true, true, false, false);
    private static BackgroundSize SIZE_FIT = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);

    private Background createBackground(Image image) {
        if (image == null) {
            return null;
        }

        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, SIZE_STRETCH));
    }

    @Override
    public ObservableValue<ScreenVisibility> screenVisibilityProperty() {
        return screenVisibility.getReadOnlyProperty();
    }

    @Override
    public BooleanProperty visibleProperty() {
        return slideshowRoot.visibleProperty();
    }

    /**
     * Slideshow model.
     */
    public final SlideshowModel getSlideshowModel() {
        return slideshowModelProperty().get();
    }

    public final void setSlideshowModel(SlideshowModel model) {
        slideshowModelProperty().set(model);
    }

    public final ObjectProperty<SlideshowModel> slideshowModelProperty() {
        return model;
    }

    /**
     * Slides.
     */
    public final ObservableList<Image> getSlides() {
        return slidesProperty().get();
    }

    public final ListExpression<Image> slidesProperty() {
        return slides;
    }

    /**
     * Duration of transitions between slides.
     */
    public ObjectProperty<Duration> transitionDurationProperty() {
        return transitionDuration;
    }

    /**
     * Duration of a single slide.
     */
    public ObjectProperty<Duration> slideDurationProperty() {
        return slideDuration;
    }

}
