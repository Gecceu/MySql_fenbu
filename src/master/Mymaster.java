package master;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.xml.crypto.Data;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mymaster extends JFrame{
    public static final int Region_Port =5143;
    public static final int Client_Port = 5307;

    private JTextArea jta = new JTextArea(); //用于提示信息的文本域
    private Container cc;
    private ServerSocket clientserver;
    private ServerSocket regionserver;
    private Socket socket;

    public static HashMap<String, List<String>> directory = new HashMap<>(); // region_ip : tables
    
    public static HashMap<String, String> region_names = new HashMap<>();    // region_id : region_name
    
    public static int regions_num = 0;

    /**
     * 构造函数
     * 初始化界面
     */
    public Mymaster(){
        super("主节点服务器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 600, 400);
        
        cc = this.getContentPane();
        setLayout(null);     //设置为绝对布局
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setBounds(0, 0, 560, 380);
        jta.setBackground(Color.LIGHT_GRAY);
        jta.setFont(new Font("宋体", Font.BOLD, 13));
        cc.add(jsp);
        setVisible(true);
    }

    /**
     * 启动监听客户端服务器
     * @param port
     */
    public void CilentServer(int port){
        try{
            clientserver = new ServerSocket(port);

            while(true){
                jta.append("等待客户连接\n");
                socket = clientserver.accept();
                Clienthandler handler = new Clienthandler(socket, jta);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 启动监听从服务器
     * @param port
     */
    public void RegionServer(int port){
        try{
            regionserver = new ServerSocket(port);

            while(true){
                jta.append("等待从服务器连接\n");
                socket = regionserver.accept();
                Regionhandler handler = new Regionhandler(socket, jta);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
    * 通过表名找到ip地址
     */
    public String getAddrByTableName(String table_name){
        for(HashMap.Entry<String, List<String>> entry : directory.entrySet()){
            if(entry.getValue().contains(table_name)){
                return new String(entry.getKey());                
            }
        }
        return new String("-1");
    }


    /**
     * 负载均衡
     */
    public String getMostSpareRegion(){
        int mini = 0;
        String spare_region_addr = "";
        for(Map.Entry<String, List<String>> entry : directory.entrySet()){
            if(entry.getValue().size() < mini){
                mini = entry.getValue().size();                    
                spare_region_addr = entry.getKey();
            }
        }
        return spare_region_addr;
    }

    /**
     * 客户端处理线程
     */
    private static class Clienthandler implements Runnable{
        private Socket socket;
        private JTextArea jta;

        public Clienthandler(Socket socket, JTextArea jta){
            this.socket = socket;
            this.jta = jta;
        }

        @Override
        public void run(){
            try{
                jta.append("客户端连接成功\n");
                jta.append("客户端ip："+socket.getInetAddress().getHostAddress()+"\n");
                while(true){
                    byte[] buf = new byte[1024];
                    int len = socket.getInputStream().read(buf);
                    String sql = new String(buf, 0, len);
                    jta.append("接收到的sql语句为："+sql+"\n");
                }
            }catch(Exception e){
                jta.append("客户端断开连接或错误\n");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 从服务器处理线程
     */
    private static class Regionhandler implements Runnable{
        private Socket socket;
        private JTextArea jta;

        public Regionhandler(Socket socket, JTextArea jta){
            this.socket = socket;
            this.jta = jta;
        }

        @Override
        public void run() {
            try{
                DataInputStream input_stream = new DataInputStream(socket.getInputStream());
                
                String response;
                String url = input_stream.readUTF();

                //查找注册节点 IP：name
                if(region_names.containsKey(url)){
                    jta.append("[INFO] " + url + " reconnected.\n");
                    response = region_names.get(url);
                }
                else{//新节点注册
                    response = "/server-" + regions_num; // region name
                    regions_num++;
                    region_names.put(url, response);
                }

                DataOutputStream output_stream = new DataOutputStream(socket.getOutputStream());
                output_stream.writeUTF(response);

                // close
                socket.close();

            }catch(Exception e){
                e.printStackTrace();
                jta.append("[ERROR] Cannot connect to region server.\n");
            }
        }
    }

    public static void main(String args[]) throws IOException{
        Mymaster master = new Mymaster();

        Thread regionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.RegionServer(Region_Port);
                while(true){
                    master.jta.append(Mymaster.directory.toString() + "\n");
                }
            }
        });
        regionThread.start();

        Thread clientThread = new Thread(new Runnable(){
            @Override
            public void run(){
                master.CilentServer(Client_Port);
            }
        });
        clientThread.start();

        Thread zookeeper_thread = new ZookeeperConnector(directory);
        zookeeper_thread.start();
    }
}

