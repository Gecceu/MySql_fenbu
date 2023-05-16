package master;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mymaster extends JFrame{
    private JTextArea jta = new JTextArea(); //用于提示信息的文本域
    private Container cc;
    private ServerSocket server;
    private Socket socket;

    public static HashMap<String, List<String>> directory = new HashMap<>(); // region_ip : tables
    
    public static HashMap<String, String> region_names = new HashMap<>();    // region_id : region_name
    
    public static int regions_num = 0;

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

    public void getServer(){
        try{
            server = new ServerSocket(5143);
        
            jta.append("服务器启动成功\n");
            while(true){
                jta.append("等待客户连接\n");
                socket = server.accept();
                Clienthandler handler = new Clienthandler(socket, jta, cc);
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


    /*
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

    private static class Clienthandler implements Runnable{
        private Socket socket;
        private JTextArea jta;

        public Clienthandler(Socket socket, JTextArea jta, Component cc){
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
                e.printStackTrace();
            }
        }
    }   
}

