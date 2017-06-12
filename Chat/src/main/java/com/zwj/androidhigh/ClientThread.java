package com.zwj.androidhigh;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by admin on 2017/6/9.
 */

class ClientThread implements Runnable {
    private final Handler handler;
    public Handler revHandler;

    public ClientThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            final Socket socket = new Socket("10.18.31.163",30000);
            final OutputStream outputStream = socket.getOutputStream();
            final BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //开启一个子线程读取服务端发送来的聊天信息
            new Thread(){
                @Override
                public void run() {
                    String content = null;
                    try {
                        while ((content = bufferedReader.readLine())!=null){
                            //where循环一直读取，读取到服务端发送的消息后给主线程发送消息，主线程更新UI
                            Message msg = new Message();
                            msg.what=2;//规定what=2为子线程给主线程发送的消息
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            Looper.prepare();//子线程中创建Handler必须受到开始Looper
            revHandler = new Handler(){
                //用于接收主线程发送过来的聊天信息并将其发送给服务端

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1){
                        String content = msg.obj.toString() + "\n";//不要忘记加换行符
                        Log.i("Net","主线程传来的消息" +content);
                        try {
                            //发送信息给服务端
                            outputStream.write(content.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
