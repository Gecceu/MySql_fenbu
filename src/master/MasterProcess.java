package master;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.Getname;

public class MasterProcess {

    public static String JudgeType(String sql){
        String reponse;
        if(sql.contains("create table")){
            reponse = getMostSpareRegion();
            if(getAddrByTableName(exisittable(sql))!=null){
                reponse = "该表已存在";
            }
        }
        else if(sql.contains("drop table")){
            reponse=getAddrByTableName(Getname.getdropname(sql));
        }
        else{
            reponse = getAddrByTableName(exisittable(sql));
            System.out.println(getAddrByTableName(exisittable(sql)));
            if(reponse.equals(null)){
                reponse="该表不存在";
            }
        }
        return reponse;
    }

    private static String exisittable(String sql){
        Set <String> tableList = new HashSet<>();
        Getname.parseSql(Getname.dSql(sql), tableList);
        return tableList.iterator().next(); //返回第一个表名
    }

    /**
     * 通过表名找到ip地址
     */
    private static String getAddrByTableName(String table_name){
        for(HashMap.Entry<String, List<String>> entry : Mymaster.directory.entrySet()){
            if(entry.getValue().contains(table_name)){
                return new String(entry.getKey());                
            }
        }
        return null;
    }
    
    /**
     * 负载均衡
     */
    private static String getMostSpareRegion(){
        int mini = 0;
        String spare_region_addr = "";
        for(Map.Entry<String, List<String>> entry : Mymaster.directory.entrySet()){
            if(entry.getValue().size() < mini){
                mini = entry.getValue().size();                    
                spare_region_addr = entry.getKey();
            }
        }
        return spare_region_addr;
    }
}
