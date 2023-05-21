package master;
import java.io.IOException;

import utils.params;


public class MasterAPP {
    public static void main(String args[]) throws IOException{
        Mymaster master = new Mymaster();

        Thread regionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.RegionServer(params.MasterPort_Region);
            }
        });
        regionThread.start();

        Thread clientThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.CilentServer(params.MatserPort_Client);
            }
        });
        clientThread.start();

        Thread zookeeper_thread = new ZookeeperConnector(Mymaster.directory);
        zookeeper_thread.start();
    }
}
