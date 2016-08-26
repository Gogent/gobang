package com.example.gogent.test2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private List<String> lstFile =new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //ActionBar actionBar=getSupportActionBar();
        //actionBar.hide();
        GetFiles("/data/data/com.example.gogent.test2/files", "txt", false);
        ArrayAdapter<String> filename = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, lstFile);
        ListView listView = (ListView) findViewById(R.id.history);
        listView.setAdapter(filename);
        listView.setHeaderDividersEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("filename", lstFile.get(position));
                intent.setClass(HistoryActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(HistoryActivity.this).setTitle("删除").setMessage("是否要删除该记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File("/data/data/com.example.gogent.test2/files/" + lstFile.get(position));
                        if(file.isFile() && file.exists()) {
                            file.delete();
                            finish();
                            Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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

    public void GetFiles(String Path, String Extension,boolean IsIterative) {

        File[] files =new File(Path).listFiles();
        String p = "/data/data/com.example.gogent.test2/files/";
        String filename;

        for (int i =0; i < files.length; i++)
        {
            File f = files[i];
            if (f.isFile())
            {
                if (f.getName().endsWith(".txt")) { //判断扩展名
                    filename = f.getPath();
                    lstFile.add(filename.substring(42));
                }
            }
            else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //忽略点文件（隐藏文件/文件夹）
                GetFiles(f.getPath(), Extension, IsIterative);
        }
    }
}
