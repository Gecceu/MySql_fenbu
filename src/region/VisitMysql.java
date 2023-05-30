package region;

import java.sql.*;
import java.util.ArrayList;

//调用getResult即可获得执行结果
public class VisitMysql {
    private static Connection connection;

    private static Statement statement;

    private static ResultSet resultSet;

    //查询语句
    private static String select (String sql) throws SQLException {

        String result = "";

        connection = MysqlConnect.getConnection();
        
        if(connection == null){
            System.out.println("数据库连接失败");
        }

        statement = connection.createStatement();

        resultSet = statement.executeQuery(sql);

        ResultSetMetaData metaData = resultSet.getMetaData();

        int  columnCount = metaData.getColumnCount();
        
        for(int i=1;i<=columnCount;i++){
            String columnLabel = metaData.getColumnLabel(i);
            result = result + columnLabel + " ";
    }

        while(resultSet.next()){
            result = result + "\n";
        for(int i=1;i<=columnCount;i++){
            Object value = resultSet.getObject(i);
            if(value == null) value = "null";
            result = result + value.toString()+" ";
        }

   }
        
        //关闭连接
        resultSet.close();
        statement.close();
        connection.close();

        System.out.println(result);

        return result;
    }

    //非查询语句
    private static int update(String sql) throws SQLException {
        
        connection = MysqlConnect.getConnection();
        
        if(connection == null){
            System.out.println("数据库连接失败");
        }

        statement = connection.createStatement();

        int row = statement.executeUpdate(sql);

        //关闭连接
        statement.close();
        connection.close();
        return row;
    }
    
    //解析sql语句，获取sql语句执行结果
   public static String getResult(String sql) throws SQLException{
    
    String result;

    String str = sql.toLowerCase();
    
    if(str.indexOf("select")>=0){ 
        
        result = select(sql);

        return result;
    }else if(str.indexOf("desc")>=0){
        result = select(sql);
        return result;
    }else{
        int row;
        row = update(sql);
        result = "本次操作改变了"+row+"行";
        //System.out.println("本次操作改变了"+row+"行");
    }
    //若是创建删除table，则使用zookeeper更新tableList
    if(str.indexOf("create table")>=0||str.indexOf("drop table")>=0){
        ZookeeperManager.tableChange();
    }
    return result;
   }

   //获取tableList
   public static ArrayList<String>  getTableList() throws SQLException{

    ArrayList<String> list = new ArrayList<>();
    
    connection = MysqlConnect.getConnection();

    statement = connection.createStatement();

    String sql = "show tables";

    if(connection == null){
        System.out.println("数据库连接失败");
    }

    resultSet = statement.executeQuery(sql);

    while(resultSet.next()){
        //System.out.println(resultSet.getString(1));
        list.add(resultSet.getString(1));
    }
    return list;
   }

}
