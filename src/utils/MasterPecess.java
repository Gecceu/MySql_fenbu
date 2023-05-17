package utils;
import java.util.HashSet;
import java.util.Set;

public class MasterPecess {

    public static void JudgeType(String sql){
        if(sql.contains("create table")){
            System.out.println("创建");
        }
        else{
            System.out.println(exisittable(sql));
        }
    }
    
    private static String exisittable(String sql){
        Set <String> tableList = new HashSet<>();
        Getname.parseSql(Getname.dSql(sql), tableList);
        return tableList.iterator().next(); //返回第一个表名
    }
}
