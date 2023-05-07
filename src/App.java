import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.Getname;//获取表名的工具类

public class App {
    
    public static void main(String[] args) throws Exception {
        Set<String> tableList = new HashSet<>();//存放表名的集合
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("请输入sql语句：");
            String sql = sc.nextLine();

            while(!sql.equals("!!!")){
                Getname.parseSql(Getname.dSql(sql),tableList);
                sql=sc.nextLine();
            }
        }
        System.out.println("表名："+tableList);
        System.out.println("感谢使用");
    }
}






