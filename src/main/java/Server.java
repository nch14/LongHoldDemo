import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by chenh on 2016/7/5.
 */
public class Server {
    public static final int PORT = 12345;//监听的端口号
    ArrayList<Socket> s;


    boolean write;


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
                write=true;
            }
        });
        panel.add(button);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Server(){
        s=new ArrayList<Socket>();
    }

    public static void main(String[] args) {
        System.out.println("服务器启动...\n");
        //  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Server server = new Server();
        server.buildGUI();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;

            try {
                socket.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            s.add(socket);
            System.out.println("客户端地址："+socket.getInetAddress());
            new Thread(this).start();
        }

        private void read(){
            DataInputStream inputStream = null;
            String strInputstream ="";
            try {
                inputStream =new DataInputStream(socket.getInputStream());
                strInputstream = inputStream.readUTF();
                System.out.println(strInputstream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void write(){
            DataOutputStream outputStream = null;
            String jsonString = Calendar.getInstance().getTime().toString()+ "爸爸我是服务器，我给了你一条消息";
            try {
                outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                outputStream.writeUTF(jsonString);
                outputStream.flush();
                /*outputStream.close();*/
                System.out.println("发送完成！");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {
            System.out.println("客户端数据已经连接" + "客户端地址：" + socket.getInetAddress().toString());
                //有没有要读的。要读就读一下
                new Thread(new Runnable() {
                    public void run() {
                        while (true){
                            read();
                        }
                    }
                }).start();
               //有没有要写的。要写的就写一下
               new Thread(new Runnable() {
                   public void run() {
                       while (true) {
                           if (write) {
                               write();
                               write = false;
                           }
                           try {
                               Thread.sleep(100);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }).start();
        }
    }

}
