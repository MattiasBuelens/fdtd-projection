package fdtd;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SlideshowModel {

    private final ListProperty<Image> slides = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    private final List<Image> cycleSlides = new ArrayList<>();
    private final ReadOnlyObjectWrapper<Image> currentSlide = new ReadOnlyObjectWrapper<>();

    private static final Random random = new Random();

    public SlideshowModel() {
        slidesProperty().addListener((observable, oldValue, newValue) -> resetCycle());
        slidesProperty().addListener(this::onSlidesListChange);
    }

    public final ObservableList<Image> getSlides() {
        return slidesProperty().get();
    }

    public final void setSlides(ObservableList<Image> slides) {
        slidesProperty().set(slides);
    }

    public final ListProperty<Image> slidesProperty() {
        return slides;
    }

    public final Image getCurrentSlide() {
        return currentSlideProperty().get();
    }

    public final ObservableObjectValue<Image> currentSlideProperty() {
        return currentSlide.getReadOnlyProperty();
    }

    public void nextSlide() {
        // remove previous from cycle
        Image previousSlide = getCurrentSlide();
        cycleSlides.remove(previousSlide);

        if (cycleSlides.isEmpty()) {
            // next cycle
            resetCycle();
        }

        Image nextSlide;
        if (cycleSlides.isEmpty()) {
            // nothing else to show
            nextSlide = previousSlide;
        } else {
            // select next
            nextSlide = cycleSlides.get(0);
            // do not show the same slide twice (if more than one slide)
            if (nextSlide.equals(previousSlide) && cycleSlides.size() > 1) {
                int index = 1 + random.nextInt(cycleSlides.size() - 1);
                Collections.swap(cycleSlides, 0, index);
                nextSlide = cycleSlides.get(0);
            }
        }

        // update current slide
        currentSlide.set(nextSlide);
    }

    /**
     * Reset the current cycle to contain all slides.
     */
    private void resetCycle() {
        cycleSlides.clear();
        cycleSlides.addAll(getSlides());
        Collections.shuffle(cycleSlides, random);
    }

    private void onSlidesListChange(ListChangeListener.Change<? extends Image> change) {
        // add new slides to cycle rotation
        boolean hasAdded = false;
        while (change.next()) {
            if (change.wasRemoved()) {
                cycleSlides.removeAll(change.getRemoved());
            }
            if (change.wasAdded()) {
                hasAdded = true;
                cycleSlides.addAll(change.getAddedSubList());
            }
        }
        // re-shuffle if new slides were added
        if (hasAdded) {
            Collections.shuffle(cycleSlides, random);
        }
    }

}
