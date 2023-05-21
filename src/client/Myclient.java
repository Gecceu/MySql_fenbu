package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Myclient extends JFrame{
   Socket socket;
   private JTextArea jta = new JTextArea(); //用于提示信息的文本域
   private JTextField jtf = new JTextField(); //用于输入sql的文本框
   private JButton jb = new JButton("发送"); //发送按钮
   private Container cc;
   private String sql;
   private int REGION_PORT = 5314;
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
      try {
         socket.getOutputStream().write(sql.getBytes()); 
         socket.getOutputStream().flush(); 
      }catch(Exception e){
         e.printStackTrace();
      }
   }

   public void connect(int port) throws IOException {
      jta.append("正在连接服务器...\n");
      try {
         socket = new Socket("127.0.0.1",port);
         jta.append("连接成功！\n");
         String  result = null;
         InputStream is = socket.getInputStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String  regionIP= null;
         //获取从节点IP地址并获取执行结果
         while ((regionIP = br.readLine()) != null) {
            jta.append(regionIP+"\n");//从节点IP地址
            result = getMessage(sql,regionIP);
            jta.append(result);
         }
      }catch(Exception e){
         e.printStackTrace();
         jta.append("连接失败！\n");
      }
   } 
   
   //与region建立socket连接并获取执行结果
   private String getMessage(String sql,String regionIP){
      String result = null;
      try {
         socket = new Socket("127.0.0.1",REGION_PORT);
         //socket = new Socket(regionIP,REGION_PORT);
         jta.append("连接成功！\n");
         InputStream is = socket.getInputStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String info = null;
            while ((info = br.readLine()) != null) {
                result = result + info; 
            }
      }catch(Exception e){
         e.printStackTrace();
         jta.append("连接失败！\n");
      }
      return result;
   }
}
