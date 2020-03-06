package com.example.phonebook;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.phonebook.ui.main.SectionsPagerAdapter;
public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS},1);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setBackgroundColor(0xEEE7E7E7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getIntExtra("back",-1) == 1) {
            LiaisonMan liaisonMan = new LiaisonMan();
            liaisonMan.number = data.getStringExtra("phone");
            liaisonMan.location = data.getStringExtra("location");
            liaisonMan.name = data.getStringExtra("name");
            liaisonMan.group = "新联系人";
            sectionsPagerAdapter.liaisonFragment.operation.Insert(liaisonMan);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){//
            case 1://如果申请权限回调的参数
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshRecord();
                    refreshLiaison();
                    refreshRecord();
                }
                break;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        refreshRecord();
        refreshLiaison();
        refreshRecord();
    }

    public void refreshRecord(){
        if (sectionsPagerAdapter!= null && sectionsPagerAdapter.recordFragment != null)
            sectionsPagerAdapter.recordFragment.refresh();;
    }

    public void refreshLiaison(){
        if (sectionsPagerAdapter!= null && sectionsPagerAdapter.liaisonFragment != null) {
            sectionsPagerAdapter.liaisonFragment.operation.UpdateDataFromSystemLiaison(this, this);
            sectionsPagerAdapter.liaisonFragment.liaisonAdapter.setGroupCursor(sectionsPagerAdapter.liaisonFragment.operation.fetchGroup());
            sectionsPagerAdapter.liaisonFragment.liaisonAdapter.notifyDataSetChanged();
            sectionsPagerAdapter.liaisonFragment.UpdateBlackList();
        }
    }
}