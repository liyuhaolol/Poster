package spa.lyh.cn.poster.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import spa.lyh.cn.lib_utils.PixelUtils;
import spa.lyh.cn.lib_utils.translucent.TranslucentUtils;
import spa.lyh.cn.lib_utils.translucent.listener.OnNavHeightListener;
import spa.lyh.cn.lib_utils.translucent.navbar.NavBarFontColorControler;
import spa.lyh.cn.poster.R;
import spa.lyh.cn.poster.qrcode.QrcodeUtils;

public class PosterDialog extends Dialog {
    private ViewGroup contentView;
    private Activity activity;
    private TextView news_title;
    private LinearLayout ll_height;
    private ImageView logo,qrcode;
    private ConstraintLayout poster_area;
    private View.OnClickListener wechatListener;
    private View click;


    public PosterDialog(@NonNull Activity context) {
        this(context, R.style.CommonDialog);
    }

    public PosterDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        this.activity = context;
        initDialogStyle();
    }

    private void initDialogStyle(){
        setContentView(createDialogView(R.layout.dialog_poster));
        if (getWindow() != null){
            //getWindow().setWindowAnimations(R.style.dialogWindowAnim);
            //设置布局顶部显示
            getWindow().setGravity(Gravity.TOP);
            //设置背景透明后设置该属性，可去除dialog边框
            getWindow().setBackgroundDrawable(new ColorDrawable());
            //设置横向铺满全屏
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            //开启沉浸式
            TranslucentUtils.setTranslucentBoth(getWindow());
            //兼容刘海屏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            getWindow().setAttributes(lp);
        }
        FrameLayout.LayoutParams llParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llParams.gravity = Gravity.BOTTOM;
        contentView.setLayoutParams(llParams);

        poster_area = contentView.findViewById(R.id.poster_area);
        news_title = contentView.findViewById(R.id.news_title);
        news_title.setText("祖国的飞天英雄征寰宇：唯一的使命就是为国出征--记神舟十三号航天员");
        ll_height = contentView.findViewById(R.id.ll_height);
        ll_height.post(new Runnable() {
            @Override
            public void run() {
                setTextSize(ll_height.getHeight());
            }
        });
        logo = contentView.findViewById(R.id.logo);
        logo.setImageResource(R.drawable.ic_logo);
        qrcode = contentView.findViewById(R.id.qrcode);
        qrcode.post(new Runnable() {
            @Override
            public void run() {
                qrcode.setImageBitmap(QrcodeUtils.createQRCode(
                        "https://www.baidu.com",
                        qrcode.getHeight(),
                        qrcode.getHeight(),
                        QrcodeUtils.getBitmapFromDrawable(activity.getResources().getDrawable(R.mipmap.ic_launcher))
                ));
            }
        });
        click = contentView.findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wechatListener != null){
                    wechatListener.onClick(view);
                }
            }
        });
    }

    private void setTextSize(int viewHeight){
        int normalHeight = PixelUtils.dip2px(activity,100);
        float scale = (float) viewHeight / (float) normalHeight;
        float textSize = scale * 20;
        news_title.setTextSize(textSize);
    }

    private ViewGroup createDialogView(int layoutId){
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    @Override
    public void show() {
        super.show();
        autoFitNavBar(contentView, R.id.nav_bar);
        setNavBarColor();
    }

    //这个懒得用外部方法兼容
    public void autoFitNavBar(ViewGroup viewGroup, int navigationBarId){
        if (navigationBarId > 0){
            final View navigationbar = viewGroup.findViewById(navigationBarId);
            PixelUtils.getNavigationBarHeight(activity, new OnNavHeightListener() {
                @Override
                public void getHeight(int height,int navbarType) {
                    ViewGroup.LayoutParams layoutParams = navigationbar.getLayoutParams();
                    layoutParams.height = height;
                    navigationbar.setLayoutParams(layoutParams);
                }
            });
        }
    }

    public void setNavBarColor(){
        NavBarFontColorControler.setNavBarMode(getWindow(),true);
    }

    public ConstraintLayout getPosterLayout(){
        return poster_area;
    }

    public void setOnWechatClickListener(View.OnClickListener listener){
        this.wechatListener = listener;
    }
}
