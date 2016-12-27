package fdtd;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class TimedMessagePreset {

    private TimedMessagePreset() {
    }

    public static final List<TimedMessage> MESSAGES = Arrays.asList(
            new TimedMessageImpl("Now playing: DJ A", Duration.of(-1, ChronoUnit.HOURS)),
            new TimedMessageImpl("Now playing: DJ B", Duration.of(1, ChronoUnit.HOURS)),
            new TimedMessageImpl("Now playing: DJ C", Duration.of(3, ChronoUnit.HOURS))
    );

}
