package com.example.yangjie.socketpractice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by yangjie on 2018/3/7.
 */
public class SocketActivity extends Activity{
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
            Socket socket = new Socket(et.getText().toString(), 4444);
            message += "连接的服务器ip地址是"+et.getText().toString()+":4444";
            setMessage();
            // 输出字节流
            OutputStream os = socket.getOutputStream();
            // 打印流
            PrintWriter pw = new PrintWriter(os);
            pw.write("我是客户端，服务端收到了吗？");
            // 刷新、输出
            pw.flush();
            message+="\n信息已发出";
            setMessage();
            // 关闭socket输出流
            socket.shutdownOutput();

            // 接受服务端信息
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
            // 关闭socket输入流(如果不注释这行代码会报错，不知道为什么？？？？？)
//            socket.shutdownInput();

            // 关闭资源
            br.close();
            isr.close();
            is.close();
            pw.close();
            os.close();
            socket.close();

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
