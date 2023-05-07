package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class Myclient extends JFrame{
   Socket socket;
   private JTextArea jta = new JTextArea(); //用于提示信息的文本域
   private JTextField jtf = new JTextField(); //用于输入sql的文本框
   private Container cc;
   
   public Myclient(){
      super("客户端");
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      cc=this.getContentPane();
      final JScrollPane scrollPane = new JScrollPane(); //滚动条
      scrollPane.setBorder(new BevelBorder(BevelBorder.RAISED)); //设置边框
      getContentPane().add(scrollPane,BorderLayout.CENTER);//将滚动条添加到窗体中
      scrollPane.setViewportView(jta);//将文本域添加到滚动条中
      cc.add(jtf, "South"); //将文本框添加到窗体中
   }

   private void connect() {
      jta.append("正在连接服务器...\n");
      try {
         socket = new Socket("127.0.0.1",5143);
         jta.append("连接成功！\n");
      }catch(Exception e){
         e.printStackTrace();
         jta.append("连接失败！\n");
      }
   }

   public static void main(String[] args){
      Myclient client = new Myclient();
      client.setSize(600, 400);
      client.setVisible(true);
      client.connect();
   }
}