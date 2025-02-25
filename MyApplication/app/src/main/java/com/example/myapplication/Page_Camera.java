package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.FastApiService;
import com.example.myapplication.models.RecogResult;
import com.example.myapplication.models.RetrofitClient;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Page_Camera extends AppCompatActivity {

    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    ImageView imv; //用來參照 ImageView 物件
    TextView resultTextView;
    private static final String TAG = "MainActivity";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // 設定螢幕不隨手機旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 設定螢幕直向顯示
//        imv = (ImageView) findViewById(R.id.imageView);  //參照 Layout 中的 ImageView 元件
        imv = (ImageView) findViewById(R.id.imagePictureFood);  //參照 Layout 中的 imagePictureFood 元件
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void onGet(View v) {
//        if (ActivityCompat.checkSelfPermission(this,    //檢查是否已取得寫入權限
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            //尚未取得權限
//            ActivityCompat.requestPermissions(this,   //向使用者要求允許寫入權限
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
//        }else {
//            //已經取得權限
//            savePhoto();  //拍照並存檔
//        }
        savePhoto();  //拍照並存檔
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //使用者允許權限
                savePhoto();  //拍照並存檔
            } else { //使用者拒絕權限
                Toast.makeText(this, "程式需要寫入權限才能運作", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePhoto() {
        imgUri = getContentResolver().insert(  //透過內容資料庫新增一個圖片檔
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        Intent it = new Intent("android.media.action.IMAGE_CAPTURE"); //建立動作為拍照的 Intent
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 100);  //啟動 Intent 並要求傳回資料
    }

    public void onPick(View v) {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);    //動作設為 "選取內容"
        it.setType("image/*");            //設定要選取的媒體類型為：所有類型的圖片
        startActivityForResult(it, 101);  //啟動 Intent 並要求傳回選取的圖檔
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 100: //拍照
                    Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);//設為系統共享媒體檔
                    sendBroadcast(it);
                    break;
                case 101: //選取相片
                    imgUri = data.getData();  //取得選取相片的 Uri
                    break;
            }
            showImg();  //顯示 imgUri 所指明的相片
        } else {
            Toast.makeText(this,
                    (requestCode == 100 ? "沒有拍到照片" : "沒有選取相片"),
                    Toast.LENGTH_LONG).show();
        }
    }

    void showImg() {
        int iw, ih, vw, vh;
        boolean needRotate;  //用來儲存是否需要旋轉

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔

        //讀取圖檔資訊存入 Option 中
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri), null, option);
        } catch (IOException e) {
            Toast.makeText(this, "讀取照片資訊時發生錯誤", Toast.LENGTH_LONG).show();
            return;
        }

        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv.getWidth();    //取得 ImageView 的寬度
        vh = imv.getHeight();   //取得 ImageView 的高度

        int scaleFactor;
        if (iw <= ih) {    //如果圖片的寬度小於高度，或圖片為正方形
            needRotate = false;                    //不需要旋轉
            scaleFactor = Math.min(iw / vw, ih / vh);   // 計算縮小比率
        } else {
            needRotate = true;                    //需要旋轉
            scaleFactor = Math.min(iw / vh, ih / vw);   // 將 ImageView 的寬、高互換來計算縮小比率
        }

        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = scaleFactor;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2

        //載入圖檔
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri), null, option);
        } catch (IOException e) {
            Toast.makeText(this, "無法取得照片", Toast.LENGTH_LONG).show();
        }

        if (needRotate) { //如果需要旋轉
            Matrix matrix = new Matrix();       //建立 Matrix 物件
            matrix.postRotate(90);      //設定旋轉角度
            bmp = Bitmap.createBitmap(bmp,      //用原來的 Bitmap 產生一個新的 Bitmap
                    0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        imv.setImageBitmap(bmp);

        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔URI：" + imgUri.toString() +
                        "\n原始尺寸：" + iw + " x " + ih +
                        "\n載入尺寸：" + bmp.getWidth() + " x " + bmp.getHeight() +
                        "\n顯示尺寸：" + vw + " x " + vh + (needRotate ? "(旋轉)" : ""))
                .setNeutralButton("關閉", null)
                .show();
    }

    public void onRecog(View v) {
        try {
//            InputStream inputStream = getAssets().open(imgUri.toString());
//            InputStream inputStream = openFileInput(imgUri.toString());
            InputStream inputStream = getContentResolver().openInputStream(imgUri);


            // 读取图像并进行预处理
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            FastApiService fastApiService = RetrofitClient.getRetrofitInstance().create(FastApiService.class);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imgUri.toString(), requestBody);

            Call<RecogResult> call = fastApiService.FoodImageUpload(imagePart);
            call.enqueue(new Callback<RecogResult>() {
                @Override
                public void onResponse(Call<RecogResult> call, Response<RecogResult> response) {
                    if (response.isSuccessful()) {
                        // 请求成功，处理 FastAPI 的响应结果
                        // RecogResult result = response.body();
                        // 在这里使用 FastAPI 的响应结果做进一步操作
                        Log.e(TAG, "onResponse: code: " + response.code());
                        Log.e(TAG, "onResponse: name: " + response.body().getResult());
                        resultTextView.setText(response.body().getResult());
                    } else {
                        // 请求失败
                        // 可以通过 response.code() 获取响应的 HTTP 状态码，并处理相应的错误
                        Log.e(TAG, "onResponse: code: " + response.code());
                        Log.e(TAG, "onResponse: error: " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<RecogResult> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "onIOException: " + e.toString());
//            e.printStackTrace();
        }
    }


}