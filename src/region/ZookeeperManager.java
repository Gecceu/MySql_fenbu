package region;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.NetworkInterface;
import java.net.Socket;

public class ZookeeperManager {
    static final String ZOOKEEPER_SERVER = "localhost:2181";
  
    static CuratorFramework client;
  
    static ArrayList<String> table_list;
  
    static String REGION_SERVER_IP;

    static String path = "/";
    
    static final String MASTER_SERVER_IP = "localhost";
   
    static final int MASTER_PORT = 5143;

    public static void zookeeperConnect() throws SQLException {
        //获取数据库表名
        table_list = VisitMysql.getTableList();
        System.out.println("(init) table_list: " + table_list);

        try {
            //获取本地ip地址
            REGION_SERVER_IP = String.valueOf(getLocalHostLANAddress()).substring(1);
        } catch (UnknownHostException e) {
            
            System.out.println("can not get region's ip");
            e.printStackTrace();
        }
        
        path = getRegionName();//与master连接并获取region的路径名
        
        //连接zk
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 5);
        client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_SERVER)
                .retryPolicy(retryPolicy)
                .namespace("service")
                .build();
        client.start();

        //construct data: (region_ip table_name@1 table_name@2 ...)
        StringBuilder data = new StringBuilder(REGION_SERVER_IP);
        for (String t : table_list) {
            data.append(" ");
            data.append(t);
        }
        
        try {
            Stat isExist = client.checkExists().forPath(path);
            if (isExist != null) {
                client.delete().forPath(path);
            } else {
                //使用ftp备份表和索引文件
                
                /*
                FTPConnector.unloadFile("table_catalog");
                FTPConnector.unloadFile("index_catalog");
                for (String t : table_list) {
                    FTPConnector.unloadFile(t);
                    FTPConnector.unloadFile(t + "_index.index");
                }
                */
            }
            //创建zk节点，值为ip地址和tableList
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.toString().getBytes());
        } catch (Exception e) {
            
            System.out.println("ftp failed");
            e.printStackTrace();
        }
    }

    //与master连接并获取region的路径名
    public static String getRegionName() {
        
        try {
            //与master创建socket连接，并告诉master ip地址
            Socket masterSocket = new Socket(MASTER_SERVER_IP, MASTER_PORT);
            OutputStream outputStream = masterSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);
            
            //发送ip地址和表名
            out.writeUTF(REGION_SERVER_IP);              
            for(int i=0;i<table_list.size();i++)
            out.writeUTF(table_list.get(i)+" ");
            InputStream inputStream = masterSocket.getInputStream();
            DataInputStream in = new DataInputStream(inputStream);
            String name = in.readUTF();
            masterSocket.close();
            return name;
        } catch (IOException e) {
            
            System.out.println("socket connect with master is wrong");
            e.printStackTrace();
            return "error";
        }
    }
    
    //获取本机的ip地址
    public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress.isSiteLocalAddress()) {
                            return inetAddress;
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddress;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    public static void closeConnect() {
        client.close();
    }

    public static String getPath() {
        return path;
    }

    //修改tableList
    public static void tableChange() throws SQLException {
        StringBuilder data = new StringBuilder(REGION_SERVER_IP);
        table_list = VisitMysql.getTableList();
        System.out.println("(tableChange) table_list:" + table_list);
        for (String t : table_list) {
            data.append(" ");
            data.append(t);
        }
        try {
            client.setData().forPath(path, data.toString().getBytes());
        } catch (Exception e) {
            System.out.println("zk set Data failed");
            e.printStackTrace();
        }
    }
}
