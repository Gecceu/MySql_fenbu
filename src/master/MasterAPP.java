package master;
import java.io.IOException;


public class MasterAPP {
    public static void main(String args[]) throws IOException{
        Mymaster master = new Mymaster();

        Thread regionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.RegionServer(Mymaster.Region_Port);
            }
        });
        regionThread.start();

        Thread clientThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.CilentServer(Mymaster.Client_Port);
            }
        });
        clientThread.start();

        Thread zookeeper_thread = new ZookeeperConnector(Mymaster.directory);
        zookeeper_thread.start();
    }
}
