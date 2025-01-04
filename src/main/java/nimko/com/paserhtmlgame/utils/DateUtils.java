package nimko.com.paserhtmlgame.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static String formatInstantToLocalDateTimeString(Instant date) {
    var dateTime = date.atZone(ZoneId.systemDefault()).toLocalDateTime();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    return dateTime.format(formatter);
  }
}
