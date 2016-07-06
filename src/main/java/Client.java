import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by chenh on 2016/7/7.
 */
public class Client {
    public static final String IP_ADDR = "192.168.1.106";//服务器地址  这里要改成服务器的ip
    public static final int PORT = 12345;//服务器端口号

    Socket socket;


    private void buildGUI(){
        JFrame frame=new JFrame();
        frame.setBounds(0,0,500,300);
        frame.setLayout(null);

        JPanel panel=new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,500,300);
        frame.add(panel);

        JButton button=new JButton("发送一条消息");
        button.setBounds(40,40,180,40);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                write();
            }
        });
        panel.add(button);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        System.out.println("客户端启动...\n");
        Client client=new Client();
        client.buildGUI();
        client.init();
    }


    private void init(){
        try {
            socket = new Socket(IP_ADDR, PORT);
            System.out.println("连接已经建立");
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonString = Calendar.getInstance().getTime().toString()+ "我才是爸爸！我给了你一条消息";
                DataOutputStream outputStream = null;
                try {
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    //System.out.println("发的数据长度为:"+jsonString.length);
                    outputStream.writeUTF(jsonString);
                    outputStream.flush();
                    System.out.println("传输数据完毕");
                    //socket.shutdownOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void read(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    DataInputStream inputStream = null;
                    String strInputstream ="";
                    try {
                        inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        strInputstream=inputStream.readUTF();
                        System.out.println(strInputstream.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
