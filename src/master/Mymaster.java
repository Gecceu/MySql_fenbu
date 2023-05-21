package master;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.util.HashMap;
import java.util.List;

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
                jta.append("[SYSTEM] 等待客户连接\n");
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
                jta.append("[SYSTEM] 等待从服务器连接\n\n");
                socket = regionserver.accept();
                Regionhandler handler = new Regionhandler(socket, jta);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
                String clinetip=socket.getInetAddress().getHostAddress();
                jta.append("\n[INFO] 客户端连接成功\n");
                jta.append("[INFO] 客户端ip："+clinetip+"\n");
                while(true){
                    DataInputStream input_stream = new DataInputStream(socket.getInputStream());
                    
                    byte[] bytes=new byte[1024];
                    int len = input_stream.read(bytes);
                    String sql=new String(bytes, 0,len);
                    String response;

                    jta.append("[INFO] 接收到的sql为："+sql+"\n\n");
                    response=MasterProcess.JudgeType(sql);

                    DataOutputStream output_stream = new DataOutputStream(socket.getOutputStream());
                    output_stream.writeUTF(response);
                }
            }catch(Exception e){
                jta.append("[ERROR] 客户端断开连接或错误\n");
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
                    jta.append("[INFO] 从节点 " + url + " 已连接 \n");
                    response = region_names.get(url);
                }
                else{//新节点注册
                    response = "/server-" + regions_num; // region name
                    regions_num++;
                    region_names.put(url, response);
                    jta.append("[INFO] 新节点注册 "+url + " " + response + "\n");
                }

                DataOutputStream output_stream = new DataOutputStream(socket.getOutputStream());
                output_stream.writeUTF(response);

                // close
                socket.close();

            }catch(Exception e){
                e.printStackTrace();
                jta.append("[ERROR] 无法连接从服务器.\n");
            }
        }
    }

}

