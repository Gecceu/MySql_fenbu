package region;

import java.sql.*;

import utils.params;

public class MysqlConnect {
    private static Connection Conn;
    //数据库url,用户名,密码
    private static String Url = "jdbc:mysql://localhost:3306/fenbu?useAffectedRows=true";

    private static String UserName = params.Database_Name;

    private static String Password = params.Database_Password;
    
    //获取链接
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            //System.out.println("加载驱动成功");
        
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Conn = DriverManager.getConnection(Url, UserName, Password);
            
            //System.out.println("数据库连接成功");
            
            return Conn;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}