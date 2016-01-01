package fdtd;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;

public enum SlideshowPreset {

    BAR("Bar", "images/Bar1.jpg", "images/Bar2.jpg", "images/Bar3.jpg"),
    BONNEKES("Bonnekes", "images/Bonnekes1.jpg", "images/Bonnekes2.jpg", "images/Bonnekes3.jpg"),
    CHAMPAGNE_COCKTAILS("Champagne en Cocktails", "images/Champagne1.jpg", "images/Champagne2.jpg", "images/Champagne3.jpg", "images/Cocktails1.jpg", "images/Cocktails2.jpg", "images/Cocktails3.jpg");

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
