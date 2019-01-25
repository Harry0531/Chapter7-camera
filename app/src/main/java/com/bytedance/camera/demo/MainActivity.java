package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TakePictureActivity.class));
        });

        findViewById(R.id.btn_camera).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RecordVideoActivity.class));
        });

        findViewById(R.id.btn_custom).setOnClickListener(v -> {
            //todo 在这里申请相机、麦克风、存储的权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},12);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_APN_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
                }

            }
            startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
        });
    }

}
