import java.util.HashSet;
import java.util.Set;

import utils.Getname;

public class App {
    public static void main(String[] args) throws Exception {
        String sql="select name from student,teacher where id=1";
        Set<String> tableList = new HashSet<String>();
        Getname.parseSql(sql, tableList);
        System.out.println(tableList);
    }
}






