package com.example.yangjie.socketpractice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by yangjie on 2018/3/7.
 */
public class UDPSocketActivity extends Activity {
    private TextView tv;
    private EditText et;
    private Button btn;
    private String message="";
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
        setContentView(R.layout.activity_socket);

        tv = (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.et_ip);
        btn = (Button) findViewById(R.id.btn_send);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendSocket();
                    }
                }).start();
            }
        });

    }

    private void SendSocket() {
        try {
            // 定义发送的信息
            InetAddress inetAddress = InetAddress.getByName(et.getText().toString());
            int port = 4444;
            byte[] bytes = "你好吗？服务器，我是UDP客户端".getBytes();
            // DatagramPacket
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
            // socket
            DatagramSocket socket = new DatagramSocket();
            // 发送
            socket.send(packet);
            // 关闭
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
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
