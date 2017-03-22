package models.post;

import com.zetcode.conf.AppConfig;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by magomed on 21.03.17.
 */
public class MainPostApp {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Timestamp ourJavaTimestampObject = new Timestamp(calendar.getTime().getTime());
        PostJDBCTemplate postJDBCTemplate = new PostJDBCTemplate(AppConfig.primaryDataSource());
        postJDBCTemplate.create(2, 2, "a", "as", true, "dds", 4, ourJavaTimestampObject);
    }
}
