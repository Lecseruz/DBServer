package models.user;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by magomed on 15.03.17.
 */
public class MainUserApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

        UserJDBCTemplate studentJDBCTemplate =
                (UserJDBCTemplate)context.getBean("userJDBCTemplate");
        System.out.println("------Records Creation--------" );
//        studentJDBCTemplate.create("maga", "gadjiev", "idiot", "mm");
       User user = studentJDBCTemplate.getUser("maga");
       System.out.print(user.getEmail());

    }
}
