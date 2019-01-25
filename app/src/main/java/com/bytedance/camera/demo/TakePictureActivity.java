package com.bytedance.camera.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.NUM_180;
import static com.bytedance.camera.demo.utils.Utils.NUM_270;
import static com.bytedance.camera.demo.utils.Utils.NUM_90;
import static com.bytedance.camera.demo.utils.Utils.rotateImage;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private File imgFile;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限

                 ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_IMAGE_CAPTURE);

            } else {
                takePicture();
            }
        });

    }

    private void takePicture() {
        //todo 打开相机
        Intent takePicterIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if(imgFile!=null){
            Uri fileUri = FileProvider.getUriForFile(this,"com.bytedance.camera.demo",imgFile);
            takePicterIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(takePicterIntent,REQUEST_IMAGE_CAPTURE);
        }
//        startActivityForResult(takePicterIntent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        //todo 根据imageView裁剪
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options bmOption = new BitmapFactory.Options();
        bmOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOption);
        int photoW = bmOption.outWidth;
        int photoH = bmOption.outHeight;

        int scaleFactor = Math.min(photoW/targetW,photoH/targetW);
        bmOption.inJustDecodeBounds = false;
        bmOption.inSampleSize = scaleFactor;
        bmOption.inPurgeable = true;

        //todo 根据缩放比例读取文件，生成Bitmap

        Bitmap bitmap =BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOption);

        //todo 如果存在预览方向改变，进行图片旋转

        imageView.setImageBitmap(rotateImage(bitmap,imgFile.getAbsolutePath()));
    }

    public static  Bitmap rotateImage(Bitmap bitmap, String path) {
        ExifInterface srcExif = null;
        try {
            srcExif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = NUM_90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = NUM_180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = NUM_270;
                break;
            default:
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                //todo 判断权限是否已经授予
                if(grantResults.length>0&&grantResults[0]==grantResults[1]&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    takePicture();
                break;
            }
        }
    }
}
