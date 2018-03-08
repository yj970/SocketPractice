package com.example.yangjie.socketpractice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by yangjie on 2018/3/7.
 */
public class UDPServerSocketActivity extends Activity {
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
        // 启动Socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServerSocket();
            }
        }).start();
    }

    private void startServerSocket() {
        try {
            // 指定端口
            DatagramSocket socket = new DatagramSocket(4444);
            // 创建datagramPacket
            byte[] bytes = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            // 接收（阻塞状态）
            socket.receive(packet);
            // byte转字符串
            String msg = new String(bytes, 0, packet.getLength());
            message += msg;
            setMessage();

            // 关闭
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
            setMessage(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
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
