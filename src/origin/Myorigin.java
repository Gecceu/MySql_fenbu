package origin;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

public class Myorigin extends JFrame{
    private JTextArea jta = new JTextArea(); //用于提示信息的文本域
    private Container cc;
    private ServerSocket server;
    private Socket fromclientsocket;

    public Myorigin(){
        super("从节点服务器");
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

    public void StartConnect(){
        try{
            server = new ServerSocket(5143);
        
            jta.append("从服务器启动成功\n");
            while(true){
                jta.append("等待客户连接\n");
                fromclientsocket = server.accept();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
