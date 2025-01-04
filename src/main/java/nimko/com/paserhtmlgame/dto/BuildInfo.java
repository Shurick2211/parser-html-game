package nimko.com.paserhtmlgame.dto;

import java.time.Instant;

public record BuildInfo(
    String artifact,
    String name,
    Instant time,
    String version,
    String group
) {

}
