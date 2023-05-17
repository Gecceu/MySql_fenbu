package master;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

import utils.MasterPecess;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class Mymaster extends JFrame{
    private JTextArea jta = new JTextArea(); //用于提示信息的文本域
    private Container cc;
    private ServerSocket server;
    private Socket fromclientsocket;

    public static Map<String, String> TableInfo = new HashMap<>(); //存储表名和ip的映射

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
                fromclientsocket = server.accept();
                Clienthandler handler = new Clienthandler(fromclientsocket, jta, cc);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
                String cilentip=socket.getInetAddress().getHostAddress();
                jta.append("客户端连接成功\n");
                jta.append("客户端ip："+cilentip+"\n");
                while(true){
                    byte[] buf = new byte[1024];
                    int len = socket.getInputStream().read(buf);
                    String sql = new String(buf, 0, len);
                    jta.append("接收到的sql语句为："+sql+"\n");
                    MasterPecess.JudgeType(sql);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }   
}

