package fdtd;

import fdtd.util.MappedList;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public enum SlideshowPreset {

    BAR("Bar", "res/Bar1.jpg", "res/Bar2.jpg", "res/Bar3.jpg"),
    BONNEKES("Bonnekes", "res/Bonnekes1.jpg", "res/Bonnekes2.jpg", "res/Bonnekes3.jpg"),
    CHAMPAGNE("Champagne", "res/Champagne1.jpg", "res/Champagne2.jpg", "res/Champagne3.jpg"),
    COCKTAILS("Cocktails", "res/Cocktails1.jpg", "res/Cocktails2.jpg", "res/Cocktails3.jpg");

    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    private final ReadOnlyListWrapper<String> imagePaths = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final ReadOnlyListWrapper<Image> images = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    SlideshowPreset(String title, String... imagePaths) {
        this.title.set(title);
        this.imagePaths.addAll(imagePaths);
        this.images.set(new MappedList<>(this.imagePaths, SlideshowPreset::createImage));
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public ObservableStringValue titleProperty() {
        return title.getReadOnlyProperty();
    }

    public ObservableList<String> getImagePaths() {
        return imagePathsProperty();
    }

    public ObservableList<String> imagePathsProperty() {
        return imagePaths.getReadOnlyProperty();
    }

    public ObservableList<Image> getImages() {
        return imagesProperty();
    }

    public ObservableList<Image> imagesProperty() {
        return images.getReadOnlyProperty();
    }

    private static Image createImage(String imagePath) {
        return new Image(imagePath);
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
