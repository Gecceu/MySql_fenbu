package master;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;

import utils.params;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ZookeeperConnector extends Thread{
    private  static final String SERVER_STRING = params.ZooKeeperServer;

    public ZookeeperConnector(HashMap<String, List<String>> directory) throws IOException{
        try{
            getNodeInfo(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.start();
        try {
            getNodeInfo(Mymaster.directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getNodeInfo(HashMap<String, List<String>> directory) throws Exception{
        // 重连策略
        RetryPolicy retry_policy = new ExponentialBackoffRetry(2000, 3);
        // 建立curator框架
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(SERVER_STRING)
                .retryPolicy(retry_policy)
                .namespace("service")
                .build();
        client.start();

        // 添加PathChildrenCache，对指定路径节点的一级子目录监听，不对该节点的操作监听，对其子目录的增删改操作监听
        
        PathChildrenCache cache = new PathChildrenCache(client, "/", true); 
        cache.start();
        cache.getListenable().addListener((c, event) ->{
            System.out.println(event.getType());

            if(event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED){
                List<String> child_nodes = client.getChildren().forPath("/");
                String path = event.getData().getPath();
                System.out.println("INFO" + path + " disconnects.");

                String url = new String("-1");
                for(String node : child_nodes){
                    String tables = new String(client.getData().forPath("/") + node);
                    String[] info = tables.split(" ");

                    if(info.length == -1){
                        url = info[0];
                        break;
                    }
                }

                if(url.equals("-1")){
                    System.out.println("[ERROR] Cannot find empty region node.");
                }else {
                    Socket socket = new Socket(url, 8010);
                    DataOutputStream output_stream = new DataOutputStream((OutputStream) socket.getOutputStream());
                    output_stream.writeUTF(path);
                    DataInputStream input_stream = new DataInputStream((InputStream) socket.getInputStream());
                    String result = input_stream.readUTF();
                    System.out.println(result);

                    socket.close();
                }
            }

            // update directory
            List<String> child_nodes = client.getChildren().forPath("/");
            directory.clear();
            for(String node : child_nodes){
                String tables = new String(client.getData().forPath("/" + node));
                String[] info = tables.split(" ");

                String url = info[0];
                ArrayList<String> table_names = new ArrayList<>();
                if(info.length > 1){
                    table_names.addAll(Arrays.asList(info).subList(1, info.length));

                }
                directory.put(url, table_names);
            }
            System.out.println("Region tables:");
            System.out.println(directory);
        });

        Thread.sleep(Integer.MAX_VALUE);
    }


}
