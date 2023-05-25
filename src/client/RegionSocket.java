package client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;

public class RegionSocket {
    private Socket socket;
    private JTextArea jta;
    private String sql;
   
    public RegionSocket(JTextArea jta, String sql){
        this.jta = jta;
        this.sql = sql;
    }
    public void connect(String IP,int port) throws IOException{
        jta.append("[INFO]正在连接从服务器...\n");
        try {
           socket = new Socket(IP.trim(),port);
           jta.append("[INFO]从服务器连接成功！\n");
           Regionhandler regionhandler = new Regionhandler(socket, jta, sql);
           Thread thread = new Thread(regionhandler);
           thread.start();
        }catch(Exception e){
           e.printStackTrace();
           jta.append("[INFO]从服务器连接失败！\n");
        }
     }

    private static class Regionhandler implements Runnable{
        private Socket socket;
        private JTextArea jta;
        private String sql;

        public Regionhandler(Socket socket, JTextArea jta, String sql){
            this.socket = socket;
            this.jta = jta;
            this.sql = sql;
        }

    @Override
    public void run() {
        String result = new String();
        try {   
            socket.getOutputStream().write(sql.getBytes()); 
            socket.getOutputStream().flush(); 
            byte[] buf = new byte[5120];
            int len = socket.getInputStream().read(buf);
            result = new String(buf, 0, len);
            jta.append("\n执行结果如下:\n"+result+"\n");
            socket.close();
        } catch (IOException e) {
           jta.append("从节点连接失败");
           e.printStackTrace();
        }
    }
    }
}
