package fdtd;

import javafx.beans.binding.ListExpression;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Duration;

public enum EditionPreset {

    Y2017("2016-2017", new SlideshowPreset[]{
            new SlideshowPreset("Bar",
                    "images/2017/Bar1.jpg",
                    "images/2017/Bar2.jpg",
                    "images/2017/Bar3.jpg"
            ),
            new SlideshowPreset("Bonnekes",
                    "images/2017/Bonnekes1.jpg",
                    "images/2017/Bonnekes2.jpg",
                    "images/2017/Bonnekes3.jpg"
            ),
            new SlideshowPreset("Champagne en Cocktails",
                    "images/2017/Champagne1.jpg",
                    "images/2017/Champagne2.jpg",
                    "images/2017/Champagne3.jpg",
                    "images/2017/Cocktails1.jpg",
                    "images/2017/Cocktails2.jpg",
                    "images/2017/Cocktails3.jpg"
            )
    }, new TimedMessage[]{
            new TimedMessageImpl("Now playing: Tzitwelsnor", Duration.ofHours(22).minusDays(1)),
            new TimedMessageImpl("Now playing: Supernovazz", Duration.ofHours(23).minusDays(1)),
            new TimedMessageImpl("Now playing: Petidis", Duration.ofHours(0).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Dave Lambert", Duration.ofHours(1).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Kastaar", Duration.ofHours(2).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Boost", Duration.ofHours(4))
    });

    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    private final ReadOnlyListWrapper<SlideshowPreset> slideshows = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final ReadOnlyListWrapper<TimedMessage> messages = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    EditionPreset(String title, SlideshowPreset[] slideshows, TimedMessage[] messages) {
        this.title.set(title);
        this.slideshows.addAll(slideshows);
        this.messages.addAll(messages);
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public ObservableStringValue titleProperty() {
        return title.getReadOnlyProperty();
    }

    public ObservableList<SlideshowPreset> getSlideshows() {
        return slideshowsProperty();
    }

    public ListExpression<SlideshowPreset> slideshowsProperty() {
        return slideshows.getReadOnlyProperty();
    }

    public ObservableList<TimedMessage> getMessages() {
        return messagesProperty();
    }

    public ListExpression<TimedMessage> messagesProperty() {
        return messages.getReadOnlyProperty();
    }

    public SlideshowPreset getSlideshowByTitle(String title) {
        for (SlideshowPreset slideshow : getSlideshows()) {
            if (slideshow.getTitle().equals(title)) {
                return slideshow;
            }
        }
        return null;
    }

    public static EditionPreset byTitle(String title) {
        for (EditionPreset preset : values()) {
            if (preset.getTitle().equals(title)) {
                return preset;
            }
        }
        return null;
    }

}
