package application.config;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by magomed on 28.04.17.
 */
public class TimestampHelper {
    public static Timestamp toTimestamp(String line){
        String st = ZonedDateTime.parse(line).format(DateTimeFormatter.ISO_INSTANT);
        return  new Timestamp(ZonedDateTime.parse(st).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static String fromTimestamp(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
