package fdtd;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;

public class SlideshowPreset {

    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    private final ReadOnlyListWrapper<URL> imageURLs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    public SlideshowPreset(String title, String... imagePaths) {
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

}
