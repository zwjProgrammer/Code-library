package com.zwj.androidhigh;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText et_input;
    TextView show;
    Button send;
    Handler handler;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_input = (EditText) findViewById(R.id.ed1);
        show = (TextView) findViewById(R.id.txt1);
        send = (Button) findViewById(R.id.send);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 2){
                    //将读取的内容追加显示在文本框中
                    show.append("\n" +msg.obj.toString());
                }
            }
        };

        //开启一个子线程，用于发送和接收服务端信息
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_input = et_input.getText().toString();
                if (TextUtils.isEmpty(str_input)){
                    Toast.makeText(MainActivity.this,"消息不能为空",Toast.LENGTH_SHORT).toString();
                    return;
                }
                //向子线程发消息，子线程接收到消息后向服务端发送聊天信息
                Message message = new Message();
                message.what=1;
                message.obj=str_input;
                clientThread.revHandler.sendMessage(message);
                et_input.setText("");
            }
        });

    }

}
