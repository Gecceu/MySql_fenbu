package origin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 查询语句调用Select(sql)函数,返回哈希list
 * 非查询语句调用Update(sql)函数，返回改变行数
 */
public class VisitMysql {
    private static Connection connection;

    private static Statement statement;

    private static ResultSet resultSet;

    //查询语句
    public static List<Map<String,Object>> Select (String sql) throws SQLException {

        connection = MysqlConnect.getConnection();
        
        if(connection == null){
            System.out.println("连接失败");
        }

        statement = connection.createStatement();

        resultSet = statement.executeQuery(sql);

        //list<Map>储存返回结果
        List<Map<String,Object>> list = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();

        int  columnCount = metaData.getColumnCount();
        
        while(resultSet.next()){
            Map<String,Object> map =new HashMap<>();
            
            for(int i=1;i<=columnCount;i++){
                String columnLabel = metaData.getColumnLabel(i);
                Object value = resultSet.getObject(i);
                map.put(columnLabel, value);
        }

        list.add(map);
   }
        
        //关闭连接
        resultSet.close();
        statement.close();
        connection.close();

        return list;
    }

    //非查询语句
    public static int Update(String sql) throws SQLException {
        
        connection = MysqlConnect.getConnection();
        
        if(connection == null){
            System.out.println("连接失败");
        }

        statement = connection.createStatement();

        int row = statement.executeUpdate(sql);

        //关闭连接
        statement.close();
        connection.close();
        return row;
    }
    
    //解析sql语句
   public static void Analyse(String sql) throws SQLException{
    
    String str = sql.toLowerCase();
    
    if(str.indexOf("select")>=0){ 
        List<Map<String,Object>> list = new ArrayList<>();
        list = Select(sql);
        PrintResultset(list);
    }else{
        int row;
        row = Update(sql);
        System.out.println("本次操作改变了"+row+"行");
    }

   }

   //打印结果
   public static void PrintResultset(List<Map<String,Object>> list) throws SQLException{

    System.out.println(list.get(1).keySet());
    
    for(int i=0; i< list.size(); i++)
        System.out.println(list.get(i).values());
   }

}
