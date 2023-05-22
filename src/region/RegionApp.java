package region;

public class RegionApp {
  
    static final int FAILURE_PORT = 8010;   //备份端口

    public static void main(String[] args) {
        try {
            //连接ftp和zookeeper
            //FTPConnector.connectFTP();
            ZookeeperManager.zookeeperConnect();

            Myregion region = new Myregion();
            region.getServer();
           
        } catch (Exception e) {
           
            if (ZookeeperManager.client != null) {
                ZookeeperManager.closeConnect();
            }
            e.printStackTrace();
        }
    }

}
