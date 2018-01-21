package com.tzutalin.dlibtest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class ResultActivity extends AppCompatActivity {

    Bitmap bmp;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        byte[] byteArray = getIntent().getExtras().getByteArray("imageResult");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.resultImage);

        image.setImageBitmap(bmp);
    }

    public void saveToGallery(View view){
        saveToGalleary(bmp);
    }

    private void saveToGalleary(Bitmap bmp){
        verifyStoragePermissions(this);
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bmp,
                "result",
                "result of combination"
        );
        Toast.makeText(this, "Saved to Gallery!", Toast.LENGTH_LONG).show();

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        //int permission = ActivityCompat.checkSelfPermission(activity,
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
