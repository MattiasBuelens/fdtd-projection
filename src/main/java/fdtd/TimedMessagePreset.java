package fdtd;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TimedMessagePreset {

    private TimedMessagePreset() {
    }

    public static final List<TimedMessage> MESSAGES = Arrays.asList(
            new TimedMessageImpl("Now playing: Tzitwelsnor", Duration.ofHours(22).minusDays(1)),
            new TimedMessageImpl("Now playing: Supernovazz", Duration.ofHours(23).minusDays(1)),
            new TimedMessageImpl("Now playing: Petidis", Duration.ofHours(0).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Dave Lambert", Duration.ofHours(1).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Kastaar", Duration.ofHours(2).plusMinutes(30)),
            new TimedMessageImpl("Now playing: Boost", Duration.ofHours(4))
    );

}
