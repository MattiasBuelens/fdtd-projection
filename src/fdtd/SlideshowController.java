package fdtd;

import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class SlideshowController extends ScreenController {

    @FXML
    private Parent slideshowRoot;

    @FXML
    private Region imageBack;

    @FXML
    private Region imageFront;

    private final ReadOnlyObjectWrapper<ScreenVisibility> screenVisibility = new ReadOnlyObjectWrapper<>();

    private final ObservableList<Image> slides = FXCollections.observableList(new ArrayList<>());
    private final ReadOnlyObjectWrapper<Image> currentSlide = new ReadOnlyObjectWrapper<>();
    private final ObjectProperty<Duration> transitionDuration = new SimpleObjectProperty<>(Duration.millis(500));
    private final ObjectProperty<Duration> slideDuration = new SimpleObjectProperty<>(Duration.seconds(5));

    private Animation animation;
    private final Random random = new Random();

    public SlideshowController() {
        screenVisibility.set(ScreenVisibility.CAN_SHOW);
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
    }

    public void start() {
        if (slides.isEmpty()) {
            animation.jumpTo(Duration.INDEFINITE);
            animation.stop();
        } else {
            animation.play();
        }
    }

    public void stop() {
        animation.pause();
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
        if (slides.isEmpty()) {
            // nothing to show
            return;
        }

        int index = slides.indexOf(getCurrentSlide());
        if (index >= 0) {
            // next slide
            index = (index + 1) % slides.size();
        } else if (!slides.isEmpty()) {
            // random slide
            index = random.nextInt(slides.size());
        }

        // update current slide
        Image nextSlide = slides.get(index);
        currentSlide.set(nextSlide);

        // prepare slide in background
        imageBack.setBackground(createBackground(nextSlide));
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

    public ObservableList<Image> getSlides() {
        return slides;
    }

    public Image getCurrentSlide() {
        return currentSlideProperty().get();
    }

    public ObservableObjectValue<Image> currentSlideProperty() {
        return currentSlide.getReadOnlyProperty();
    }

    public ObjectProperty<Duration> transitionDurationProperty() {
        return transitionDuration;
    }

    public ObjectProperty<Duration> slideDurationProperty() {
        return slideDuration;
    }

}
