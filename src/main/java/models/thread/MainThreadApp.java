package models.thread;

import com.zetcode.conf.AppConfig;

import java.sql.Timestamp;
import java.util.AbstractCollection;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by magomed on 20.03.17.
 */
public class MainThreadApp {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Timestamp ourJavaTimestampObject = new Timestamp(calendar.getTime().getTime());
        Thread thread = new Thread(1,"m", "maga", "mm", "ma", 5, "ff", "asd");
        ThreadJDBCTemplate threadJDBCTemplate = new ThreadJDBCTemplate(AppConfig.primaryDataSource());
        threadJDBCTemplate.create(1, "m", "maga", "mm", "ma", 5, "ff", ourJavaTimestampObject);
    }
}
