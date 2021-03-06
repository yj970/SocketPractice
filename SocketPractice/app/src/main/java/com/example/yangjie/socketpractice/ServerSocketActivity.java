package com.example.yangjie.socketpractice;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketActivity extends AppCompatActivity {

    private TextView tv;
    private String message;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                tv.setText(message);
            } else if (msg.what == 1) {
                tv.setText(msg.getData().getString("error"));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_socket);

        tv = (TextView) findViewById(R.id.tv);
        // 启动ServerSocket
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServerSocket();
            }
        }).start();
    }

    private void startServerSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            // Socket
            message = "服务器已启动，等待客户端连接...";
            setMessage();
            Socket socket = serverSocket.accept();
            // 字节输入流
            InputStream is = socket.getInputStream();
            // 字符输入流
            InputStreamReader isr = new InputStreamReader(is);
            // 缓冲
            BufferedReader br = new BufferedReader(isr);
            // 读取
            String info = null;
            while ((info = br.readLine()) != null) {
                message+="\n";
                message+=info;
            }
            setMessage();
            // 关闭socket输入流
            socket.shutdownInput();

            // 回应客户端
            // 输出字节流
            OutputStream os = socket.getOutputStream();
            // 打印流
            PrintWriter pw = new PrintWriter(os);
            pw.write("我是服务端，我收到了你的信息了！");
            // 刷新、输出
            pw.flush();
            // 关闭socket输出流
            socket.shutdownOutput();
            // 关闭资源
//            pw.close(); 输出流不能关闭，关闭这个会导致socket也会被关闭，只关闭socket就好
            os.close();
            br.close();
            isr.close();
            is.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            setMessage(e.getMessage());
        }
    }


    private void setMessage() {
        handler.sendEmptyMessage(0);
    }

    private void setMessage(String error) {
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("error", error);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
