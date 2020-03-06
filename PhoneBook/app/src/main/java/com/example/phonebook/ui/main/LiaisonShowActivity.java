package com.example.phonebook.ui.main;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.LiaisonMan;
import com.example.phonebook.R;
import com.google.android.material.tabs.TabLayout;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class LiaisonShowActivity extends AppCompatActivity {
    private DataBaseOperation operation;
    public LiaisonMan liaisonMan = new LiaisonMan();
    private SectionsLiaisonAdapter sectionsLiaisonAdapter;
    private ViewPager viewPager;
    private ImageView imageView;
    public static String weather = "未知";
    public static String temp = "未知";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liaisonman_show);
        operation = new DataBaseOperation(this, "Record");
        liaisonMan.number = getIntent().getStringExtra("phone");
        viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiaisonShowActivity.this, EditActivity.class);
                intent.putExtra("name",liaisonMan.name);
                intent.putExtra("location",liaisonMan.location);
                intent.putExtra("phone",liaisonMan.number);
                intent.putExtra("edit",1);
                intent.putExtra("file",liaisonMan.file);
                startActivityForResult(intent,1);
            }
        });
        imageView = findViewById(R.id.imageView);
        Refresh();
        SetImageView();
    }

    private void SetImageView(){
        if (!liaisonMan.file.equals("")){
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(Uri.parse(liaisonMan.file)).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                    saveImageToServer(bitmap, outfile);//显示图片到imgView上
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1)
            Edit(data);
    }

    private void Edit(Intent data){
        String oldNumber = liaisonMan.number;
        liaisonMan.number = data.getStringExtra("phone");
        liaisonMan.location = data.getStringExtra("location");
        liaisonMan.name = data.getStringExtra("name");
        String file = data.getStringExtra("file");
        Toast.makeText(this,file,Toast.LENGTH_LONG).show();
        if (!file.equals("")){
            liaisonMan.file = file;
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(Uri.parse(file)).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                    saveImageToServer(bitmap, outfile);//显示图片到imgView上
                }
            });
        }
        else liaisonMan.file = "";
        operation.Update(oldNumber,liaisonMan);
        Refresh();
    }

    private void saveImageToServer(final Bitmap bitmap, String outfile) {
        File file = new File(outfile);
        // TODO: 2018/12/4  这里就可以将图片文件 file 上传到服务器,上传成功后可以将bitmap设置给你对应的图片展示
        imageView.setImageBitmap(bitmap);
    }

    private void Refresh(){
        TextView textView = findViewById(R.id.textView2);
        Cursor cursor = operation.helper.getReadableDatabase().query("LiaisonMan",null,"phone = ?",new String[]{liaisonMan.number},null,null,null);
        if (cursor!=null) {
            cursor.moveToFirst();
            textView.setText(cursor.getString(cursor.getColumnIndex("name")));
            liaisonMan.name = cursor.getString(cursor.getColumnIndex("name"));
            liaisonMan.location = cursor.getString(cursor.getColumnIndex("location"));
            liaisonMan.group = cursor.getString(cursor.getColumnIndex("groupID"));
            liaisonMan.file = cursor.getString(cursor.getColumnIndex("file"));
            sectionsLiaisonAdapter = new SectionsLiaisonAdapter(this, getSupportFragmentManager(), liaisonMan);
            viewPager.setAdapter(sectionsLiaisonAdapter);
            if (liaisonMan.location.indexOf(" ") > 0)
                sendRequestWithHttpURLConnection(liaisonMan.location.substring(0, liaisonMan.location.indexOf(" ")));
        }
    }

    public void sendRequestWithHttpURLConnection(final String cityname){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try{
                    URL url=new URL("https://api.help.bj.cn/apis/weather2d?id="+cityname);
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    //下面对获得的输入流进行读取
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject=new JSONObject(jsonData);
            String city=jsonObject.getString("city");
            temp=jsonObject.getString("temp");
            weather=jsonObject.getString("weather");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
