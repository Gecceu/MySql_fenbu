package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import utils.params;
public class Myclient extends JFrame{
   Socket socket;
   private JTextArea jta = new JTextArea(); //用于提示信息的文本域
   private JTextField jtf = new JTextField(); //用于输入sql的文本框
   private JButton jb = new JButton("发送"); //发送按钮
   private Container cc;
   public static String sql;

   public Myclient(){
      super("客户端");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(0, 0, 600, 400);

      cc = this.getContentPane();
      setLayout(null);     //设置为绝对布局
      JScrollPane jsp = new JScrollPane(jta);
      jsp.setBounds(10, 10, 560, 300);
      jta.setBackground(Color.LIGHT_GRAY);
      jta.setFont(new Font("宋体", Font.BOLD, 13));
      jtf.setBounds(10, 320, 400, 30);
      jb.setBounds(440, 320, 100, 30);
      cc.add(jb);
      cc.add(jsp);
      cc.add(jtf);
      setVisible(true);

      jb.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            sendActionPerformed(evt);
         }
      });
   }

   private void sendActionPerformed(java.awt.event.ActionEvent evt) {
      sql = jtf.getText();
      jta.append("发送的sql语句为："+sql+"\n");
      jtf.setText("");
      try {
         socket.getOutputStream().write(sql.getBytes()); 
         socket.getOutputStream().flush(); 
      }catch(Exception e){
         e.printStackTrace();
      }
   }

   //建立socket连接
   public void connect(String IP,int port) throws IOException{
      jta.append("[INFO]正在连接主服务器...\n\n");
      try {
         socket = new Socket(IP,port);
         jta.append("[INFO]主服务器连接成功！\n");
         MasterSocket mastersocket = new MasterSocket(socket, jta);
         Thread thread = new Thread(mastersocket);
         thread.start();
      }catch(Exception e){
         e.printStackTrace();
         jta.append("[WARN]主服务器连接失败！\n");
      }
   } 

   /*
    * 主节点处理线程
    */
    private static class MasterSocket implements Runnable{
      private Socket socket;
      private JTextArea jta;
      public MasterSocket(Socket socket, JTextArea jta){
         this.socket = socket;
         this.jta = jta;
      }
      @Override
      public void run() {
         try {
            byte[] buf = new byte[5120];
            while(true){
               int len = socket.getInputStream().read(buf);
               String regionIP = new String(buf, 0, len);
               jta.append("从节点IP地址为："+regionIP+"\n\n");
               //创建线程与从节点连接获取执行结果 
               RegionSocket regionSocket = new RegionSocket(jta, sql);
               Thread regionThread = new Thread(new Runnable(){
                  @Override
                  public void run(){
                      try {
                          regionSocket.connect(regionIP,params.ClientPort_Region);
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              });
              regionThread.start();
            }  
         }catch(Exception e){
            e.printStackTrace();
            jta.append("连接失败！\n");
         }
      }
  }
}
