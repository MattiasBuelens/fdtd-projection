package fdtd;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;

public enum SlideshowPreset {

    BAR("Bar", "res/Bar1.jpg", "res/Bar2.jpg", "res/Bar3.jpg"),
    BONNEKES("Bonnekes", "res/Bonnekes1.jpg", "res/Bonnekes2.jpg", "res/Bonnekes3.jpg"),
    CHAMPAGNE_COCKTAILS("Champagne en Cocktails", "res/Champagne1.jpg", "res/Champagne2.jpg", "res/Champagne3.jpg", "res/Cocktails1.jpg", "res/Cocktails2.jpg", "res/Cocktails3.jpg");

    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    private final ReadOnlyListWrapper<URL> imageURLs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    SlideshowPreset(String title, String... imagePaths) {
        this.title.set(title);
        for (String path : imagePaths) {
            this.imageURLs.add(SlideshowPreset.class.getResource(path));
        }
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public ObservableStringValue titleProperty() {
        return title.getReadOnlyProperty();
    }

    public ObservableList<URL> getImageURLs() {
        return imageURLsProperty();
    }

    public ObservableList<URL> imageURLsProperty() {
        return imageURLs.getReadOnlyProperty();
    }

    public static SlideshowPreset byTitle(String title) {
        for (SlideshowPreset preset : values()) {
            if (preset.getTitle().equals(title)) {
                return preset;
            }
        }
        return null;
    }

}
