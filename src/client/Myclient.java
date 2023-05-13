package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Myclient extends JFrame{
   Socket socket;
   private JTextArea jta = new JTextArea(); //用于提示信息的文本域
   private JTextField jtf = new JTextField(); //用于输入sql的文本框
   private JButton jb = new JButton("发送"); //发送按钮
   private Container cc;
   
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
      jtf.setText("请输入sql语句:");
      jb.setBounds(440, 320, 100, 30);
      cc.add(jb);
      cc.add(jsp);
      cc.add(jtf);
      setVisible(true);
   }

   private void connect() {
      for(int i=0;i<30;i++)
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
      client.connect();
   }
}