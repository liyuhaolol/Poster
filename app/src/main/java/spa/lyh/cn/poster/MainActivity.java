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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import spa.lyh.cn.peractivity.PermissionActivity;
import spa.lyh.cn.poster.dialog.PosterDialog;
import spa.lyh.cn.utils_io.IOUtils;
import spa.lyh.cn.utils_io.model.FileData;

public class MainActivity extends PermissionActivity {

    private static final int REQUEST_STATE_CODE = 1010;

    private Button btn;

    private Bitmap mBitmap;

    private PosterDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new PosterDialog(MainActivity.this);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        dialog.setOnWechatClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(REQUIRED_LOAD_METHOD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    @Override
    public void permissionAllowed() {
        savePoster(dialog.getPosterLayout());
    }

    /**
     * 保存海报图片
     */
    private void savePoster(ViewGroup group) {

        // 1.View截图
        group.setDrawingCacheEnabled(true);
        // 2.创建一个Bitmap
        Bitmap bitmap = group.getDrawingCache();
        float scaleWidth = ((float)788)/bitmap.getWidth();
        float scaleHeight = ((float)1280)/bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth>1?1:scaleWidth,scaleHeight>1?1:scaleHeight);
        mBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        // 3.保存到SD卡
        if (mBitmap != null) {
            saveToLocal(mBitmap);
        }
    }

    /**
     * 保存一张Bitmap图到本地
     */
    private void saveToLocal(Bitmap bitmap) {
        try {
            FileOutputStream out;
            FileData data;
            String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Dxw/Poster";
            String fileName = "view_" + System.currentTimeMillis() + ".jpg";
            String filePath = dirPath + "/" + fileName;
            try {
                data = IOUtils.createFileOutputStream(this,dirPath,fileName,IOUtils.ADD_ONLY);
                out = data.getFos();
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                    Toast.makeText(this, "保存图片到相册成功", Toast.LENGTH_SHORT).show();
                    Uri uri = IOUtils.getFileUri(this,filePath);
                    if (uri != null){
                        Log.e("qwer","得到了uri");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}