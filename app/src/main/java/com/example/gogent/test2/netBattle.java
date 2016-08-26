package com.example.gogent.test2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.EditText;


public class netBattle extends AppCompatActivity {


    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_battle);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View ipAddress = layoutInflater.inflate(R.layout.ip_edit_text, null);
        this.getScreen();
        //serverView = new ServerView(this);
        //clientView = new ClientView(this);
        final Button server = (Button) findViewById(R.id.Server);
        final Button client = (Button) findViewById(R.id.Client);
        //加载服务端
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(netBattle.this)
                        .setTitle("请输入客户端IP")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(ipAddress)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(netBattle.this, ServerActivity.class);
                                String ip = ipAddress.toString();
                                intent.putExtra("ipAddress", ip);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        //加载客户端
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(netBattle.this).setTitle("请输入服务端IP").setIcon(android.R.drawable.ic_dialog_info).setView(ipAddress).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(netBattle.this, ClientActivity.class);
                        String ip = ipAddress.toString();
                        intent.putExtra("ipAddress", ip);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_net_battle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Screen getScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        Screen screen = new Screen(screenWidth, screenHeight);
        return screen;
    }
}
