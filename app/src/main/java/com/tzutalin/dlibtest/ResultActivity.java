package com.tzutalin.dlibtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.R.*;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        byte[] byteArray = getIntent().getExtras().getByteArray("imageResult");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.resultImage);

        image.setImageBitmap(bmp);
    }
}
