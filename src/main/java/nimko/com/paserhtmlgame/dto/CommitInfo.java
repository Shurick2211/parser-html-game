package nimko.com.paserhtmlgame.dto;

import static nimko.com.paserhtmlgame.utils.DateUtils.formatInstantToLocalDateTimeString;

import java.time.Instant;

public record CommitInfo(
    String id,
    Instant time
) {


  @Override
  public String toString() {
    return "id: '" + id + '\'' +
        ", time: " + formatInstantToLocalDateTimeString(time);
  }


}
