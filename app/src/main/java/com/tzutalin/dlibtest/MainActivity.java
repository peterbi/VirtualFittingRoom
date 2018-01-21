/*
*  Copyright (C) 2015-present TzuTaLin
*/

package com.tzutalin.dlibtest;

import android.content.Context;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BigImageCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.PedestrianDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.graphics.Bitmap;

import android.os.Environment;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private Button btnItem;
    private ImageView ivItem;
    private String userChoosenTask;
    Bitmap selfie = null;
    Bitmap item = null;
    private boolean setSelfie = false;
    private String spinnerResult = "";
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;
    ArrayList<Point> cords = new ArrayList<>();

    private static final String TAG = "MainActivity";

    // Storage Permissions
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // UI
    private ProgressDialog mDialog;
    private MaterialListView mListView;
    private FloatingActionButton mFabActionBt;
    private FloatingActionButton mFabCamActionBt;
    private Toolbar mToolbar;

    private String mTestImgPath;
    private FaceDet mFaceDet;
    private PedestrianDet mPersonDet;
    private List<Card> mCard = new ArrayList<>();

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mListView = (MaterialListView) findViewById(R.id.material_listview);
//        setSupportActionBar(mToolbar);
//        // Just use hugo to print log
//        isExternalStorageWritable();
//        isExternalStorageReadable();
//
//        // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//
//        if (currentapiVersion >= Build.VERSION_CODES.M) {
//            verifyPermissions(this);
//        }
//
//        setupUI();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);

        btnItem = (Button) findViewById(R.id.btnSelectItem);
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem();
            }
        });
        ivItem = (ImageView) findViewById(R.id.ivItem);

        Spinner spinner=(Spinner)findViewById(R.id.selectCa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int pos, long id) {
                // TODO Auto-generated method stub
                spinnerResult = parent.getItemAtPosition(pos).toString();//获取选择项的值
                Log.i("spinner", spinnerResult);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                    Log.i(userChoosenTask,"start rec");
                    demoStaticImage();
                    Log.i(userChoosenTask,"end rec");
                    Log.d(userChoosenTask,Integer.toString(cords.size()));
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        setSelfie = true;
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {

                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectItem() {
        setSelfie = false;
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);

            demoStaticImage();
        }
    }

    private void onCaptureImageResult(Intent data) {
        selfie = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        selfie.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(selfie);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap image = null;

        if (data != null) {
            try {
                Uri selectedImage = data.getData();
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                image = scaleDownBitmap(image, 100, this);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mTestImgPath = cursor.getString(columnIndex);
                cursor.close();
                if (mTestImgPath != null) {
                    runDemosAsync(mTestImgPath);
                    Toast.makeText(this, "Img Path:" + mTestImgPath, Toast.LENGTH_SHORT).show();
                }
                } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (setSelfie) {
            ivImage.setImageBitmap(image);
            selfie = image;
        }
        else {
            ivItem.setImageBitmap(image);
            item = image;
        }

    }

    /**
     * Merges two images.
     *
     * @param view
     */
    public void submit(View view){
        //convert bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        item = resizeImage(item, (cords.get(2).x-cords.get(0).x)*2, (cords.get(1).y - cords.get(0).y)*2);
        int left = (int)(cords.get(0).x-(cords.get(2).x-cords.get(0).x)*0.2);
        int top = (int)(3 * cords.get(0).y - 2*cords.get(1).y);
        selfie = combineImages(selfie, item, left, top);
        selfie.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Toast.makeText(this,spinnerResult, Toast.LENGTH_SHORT).show();

        //pass byte array into intent
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("imageResult", byteArray);
        intent.putExtra("spinnerResult", spinnerResult);
        startActivity(intent);
    }

    private static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    private Bitmap combineImages(Bitmap selfie, Bitmap item, int left, int top){
        if (!selfie.isMutable())
        {
            // 设置图片为背景为透明
            selfie = selfie.copy(Bitmap.Config.ARGB_8888, true);
        }

        //Canvas canvas = new Canvas(selfie);
        Bitmap selfieCopy = selfie.copy(selfie.getConfig(), true);
        Canvas canvas = new Canvas(selfieCopy);
        Paint paint = new Paint();

        canvas.drawBitmap(item, left, top, paint);// 叠加新图b2 (120-85)/2= 17.5
        //canvas.save(Canvas.ALL_SAVE_FLAG);
        //canvas.restore();

        return selfieCopy;



    }
//////////////////////////////////////////////////////////////////////hualidefengexian////////////////////////////////////////


//    protected void setupUI() {
//        mListView = (MaterialListView) findViewById(R.id.material_listview);
//        mFabActionBt = (FloatingActionButton) findViewById(R.id.fab);
//        mFabCamActionBt = (FloatingActionButton) findViewById(R.id.fab_cam);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        mFabActionBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // launch Gallery
//                Toast.makeText(MainActivity.this, "Pick one image", Toast.LENGTH_SHORT).show();
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
//            }
//        });
//
//        mFabCamActionBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, CameraActivity.class));
//            }
//        });
//
//        mToolbar.setTitle(getString(R.string.app_name));
//        Toast.makeText(MainActivity.this, getString(R.string.description_info), Toast.LENGTH_LONG).show();
//    }

    /**
     * Checks if the app has permission to write to device storage or open camera
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    @DebugLog
    private static boolean verifyPermissions(Activity activity) {
        // Check if we have write permission
        int write_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_persmission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (write_permission != PackageManager.PERMISSION_GRANTED ||
                read_persmission != PackageManager.PERMISSION_GRANTED ||
                camera_permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }

    /* Checks if external storage is available for read and write */
    @DebugLog
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    @DebugLog
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @DebugLog
    protected void demoStaticImage() {
        if (mTestImgPath != null) {
            Timber.tag(TAG).d("demoStaticImage() launch a task to det");
            runDemosAsync(mTestImgPath);
        } else {
            Timber.tag(TAG).d("demoStaticImage() mTestImgPath is null, go to gallery");
            Toast.makeText(MainActivity.this, "Pick an image to run algorithms", Toast.LENGTH_SHORT).show();
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
    }

 //   @Override
 /*   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Toast.makeText(MainActivity.this, "Demo using static images", Toast.LENGTH_SHORT).show();
            demoStaticImage();
        }
    }
    */

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mTestImgPath = cursor.getString(columnIndex);
                cursor.close();
                if (mTestImgPath != null) {
                    runDemosAsync(mTestImgPath);
                    Toast.makeText(this, "Img Path:" + mTestImgPath, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
*/
    // ==========================================================
    // Tasks inner class
    // ==========================================================

    @NonNull
    private void runDemosAsync(@NonNull final String imgPath) {
        //demoPersonDet(imgPath);
        demoFaceDet(imgPath);
    }
/*
    private void demoPersonDet(final String imgPath) {
        new AsyncTask<Void, Void, List<VisionDetRet>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<VisionDetRet> personList) {
                super.onPostExecute(personList);
                if (personList.size() > 0) {

                    /*Card card = new Card.Builder(MainActivity.this)
                            .withProvider(BigImageCardProvider.class)
                            .setDrawable(drawRect(imgPath, personList, Color.BLUE))
                            .setTitle("Person det")
                            .endConfig()
                            .build();
                    mCard.add(card);--
                } else {
                    Toast.makeText(getApplicationContext(), "No person", Toast.LENGTH_LONG).show();
                }
                updateCardListView();
            }

            @Override
            protected List<VisionDetRet> doInBackground(Void... voids) {
                // Init
                if (mPersonDet == null) {
                    mPersonDet = new PedestrianDet();
                }

                Timber.tag(TAG).d("Image path: " + imgPath);

                List<VisionDetRet> personList = mPersonDet.detect(imgPath);
                return personList;
            }
        }.execute();
    }
    */

    /**
     * Detect faces
     * @param imgPath
     */
    private void demoFaceDet(final String imgPath) {
        new AsyncTask<Void, Void, List<VisionDetRet>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDiaglog("Detecting faces");
            }

            @Override
            protected void onPostExecute(List<VisionDetRet> faceList) {
                super.onPostExecute(faceList);
                if (faceList.size() > 0) {
                    drawRect(imgPath,faceList,Color.GREEN);
                    Card card = new Card.Builder(MainActivity.this)
                            .withProvider(BigImageCardProvider.class)
                            .setDrawable(drawRect(imgPath, faceList, Color.GREEN))
                            .setTitle("Face det")
                            .endConfig()
                            .build();
                    //mCard.add(card);
                } else {
                    Toast.makeText(getApplicationContext(), "No face", Toast.LENGTH_LONG).show();
                }
                //updateCardListView();
                dismissDialog();
            }

            @Override
            protected List<VisionDetRet> doInBackground(Void... voids) {
                // Init
                if (mFaceDet == null) {
                    mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
                }

                final String targetPath = Constants.getFaceShapeModelPath();
                if (!new File(targetPath).exists()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Copy landmark model to " + targetPath, Toast.LENGTH_SHORT).show();
                        }
                    });
                    FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
                }

                List<VisionDetRet> faceList = mFaceDet.detect(imgPath);
                return faceList;
            }
        }.execute();
    }

    private void updateCardListView() {
        mListView.clearAll();
        for (Card each : mCard) {
            mListView.add(each);
        }
    }

    private void showDiaglog(String title) {
        dismissDialog();
        mDialog = ProgressDialog.show(MainActivity.this, title, "process..", true);
    }

    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @DebugLog
    private BitmapDrawable drawRect(String path, List<VisionDetRet> results, int color) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        android.graphics.Bitmap.Config bitmapConfig = bm.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bm = bm.copy(bitmapConfig, true);
        int width = bm.getWidth();
        int height = bm.getHeight();
        // By ratio scale
        float aspectRatio = bm.getWidth() / (float) bm.getHeight();

        final int MAX_SIZE = 512;
        int newWidth = MAX_SIZE;
        int newHeight = MAX_SIZE;
        float resizeRatio = 1;
        newHeight = Math.round(newWidth / aspectRatio);
        if (bm.getWidth() > MAX_SIZE && bm.getHeight() > MAX_SIZE) {
            Timber.tag(TAG).d("Resize Bitmap");
            bm = getResizedBitmap(bm, newWidth, newHeight);
            resizeRatio = (float) bm.getWidth() / (float) width;
            Timber.tag(TAG).d("resizeRatio " + resizeRatio);
        }

        // Create canvas to draw
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        // Loop result list
        for (VisionDetRet ret : results) {
            Rect bounds = new Rect();
            bounds.left = (int) (ret.getLeft() * resizeRatio);
            bounds.top = (int) (ret.getTop() * resizeRatio);
            bounds.right = (int) (ret.getRight() * resizeRatio);
            bounds.bottom = (int) (ret.getBottom() * resizeRatio);
            //canvas.drawRect(bounds, paint);
            // Get landmark
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            int counter = 0;
            for (Point point : landmarks) {
                if((counter==0) || (counter==8) || (counter==16)) {
                    cords.add(point);
                    int pointX = (int) (point.x * resizeRatio);
                    int pointY = (int) (point.y * resizeRatio);
                    //canvas.drawCircle(pointX, pointY, 2, paint);
                }
                counter++;
            }
        }

        return new BitmapDrawable(getResources(), bm);
    }

    @DebugLog
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }
}
