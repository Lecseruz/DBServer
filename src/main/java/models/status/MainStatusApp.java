package models.status;

import com.zetcode.conf.AppConfig;

/**
 * Created by magomed on 20.03.17.
 */
public class MainStatusApp {
    public static void main(String[] args) {
        StatusJDBCTemplate statusJDBCTemplate= new StatusJDBCTemplate(AppConfig.primaryDataSource());
        statusJDBCTemplate.updateCountForum(5);
    }
}
