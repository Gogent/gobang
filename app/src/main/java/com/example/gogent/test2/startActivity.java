package com.example.gogent.test2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//import java.util.logging.Handler;

public class startActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String filename = "/data/data/com.example.gogent.test2/files/winrate";
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_start);
        Button start = (Button) findViewById(R.id.start);
        final Button netBattle = (Button) findViewById(R.id.net_battle);

        //查看文件是否存在，不存在则创建
        if(!fileExits(filename)) {
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try {
                out = openFileOutput("winrate", Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write("0\n0");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //读取并显示胜率
        TextView textView = (TextView)findViewById(R.id.winrateText);
        String winrate = textView.getText().toString();
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = openFileInput("winrate");
            reader = new BufferedReader(new InputStreamReader(in));
            int sum = 0;
            String win = "";
            String total = "";
            boolean isWin = true;
            while( (total = reader.readLine()) != null) {
                if(isWin == true) {
                    win = total;
                    isWin = false;
                }
                sum = Integer.parseInt(total);
            }
            if(sum == 0) {
                winrate = winrate + "0%";
            } else {
                total = String.valueOf(sum).toString();
                winrate = winrate + String.valueOf(Integer.parseInt(win) * 100 / Integer.parseInt(total)).toString() + "%";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        textView.setText(winrate);

        /*//查看文件夹是否存在
        File file = new File(historyPath);
        if(!file.exists())
            file.mkdir();*/

        Button record = (Button) findViewById(R.id.record);
        //Button config = (Button) findViewById(R.id.config);
        Button exit = (Button) findViewById(R.id.exit);
        //开始按钮
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //开始游戏
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(startActivity.this, MainActivity.class);
                                startActivity(intent);
                                //startActivity.this.finish();
                            }
                        });
                    }
                }).start();
            }
        });
        //网络对战按钮
        netBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "该功能暂未完成", Toast.LENGTH_SHORT).show();
            }
        });
        //回放按钮
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        //设置按钮
       /* config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(startActivity.this).setTitle("提示").setMessage("玩家胜利").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"you press this button", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });*/
        //结束按钮
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束程序
                new AlertDialog.Builder(startActivity.this).setTitle("退出").setMessage("是否要退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setNegativeButton("取消", null).show();
            }
        });
    }

    public boolean fileExits(String string) {
        try {
            File file = new File(string);
            if(!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
}