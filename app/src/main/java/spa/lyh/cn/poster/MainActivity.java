package spa.lyh.cn.poster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STATE_CODE = 1010;

    private LinearLayout layout_poster;
    private LinearLayout layout_save;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout_poster = (LinearLayout) findViewById(R.id.layout_poster);
        layout_save = (LinearLayout) findViewById(R.id.layout_save);
        layout_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 保存海报图片
                savePoster();
            }
        });
    }

    /**
     * 保存海报图片
     */
    private void savePoster() {

        // 1.View截图
        layout_poster.setDrawingCacheEnabled(true);
        // 重新测量View
        layout_poster.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // 2.创建一个Bitmap
        Bitmap bitmap = layout_poster.getDrawingCache();
        float scaleWidth = ((float)500)/bitmap.getWidth();
        float scaleHeight = ((float)500)/bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        mBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        // 3.保存到SD卡
        if (mBitmap != null) {
            //判断是否为Android 6.0 以上的系统版本，如果是，需要动态添加权限
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions();
            } else {
                saveToLocal(mBitmap);
            }
        }

    }

    private void requestPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_STATE_CODE);
        } else {
            saveToLocal(mBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STATE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveToLocal(mBitmap);
                } else {
                    Toast.makeText(this, "权限授予失败，请重新授予", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 保存一张Bitmap图到本地
     */
    private void saveToLocal(Bitmap bitmap) {
        Log.e("qwer",getExternalCacheDir().toString());
        try {
            File appDir = new File(getExternalCacheDir().toString()+"/Poster");
            // 没有目录创建目录
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, "view_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                    // 通知图库更新
                    Uri uri = Uri.fromFile(file);
                    Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    sendBroadcast(scannerIntent);
                    Toast.makeText(this, "保存图片到相册成功", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}