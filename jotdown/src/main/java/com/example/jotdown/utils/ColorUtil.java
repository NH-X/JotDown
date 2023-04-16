package com.example.jotdown.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

public class ColorUtil {
    private static final String TAG="ColorUtil";

    public static int strToColor(String strColor){                  //将字符串颜色值转int型颜色值
        Log.d(TAG, "strToColor: strColor:"+strColor);
        int customColor= Color.parseColor("#"+strColor);
        Log.d(TAG, "strToColor: color:"+customColor);
        return customColor;
    }

    public static void settingShapeColor(View view, float[] radii, int color){  //绘制形状矩阵
        GradientDrawable drawable=new GradientDrawable();
        drawable.setCornerRadii(radii);
        drawable.setColor(color);
        view.setBackground(drawable);
    }

    public static BitmapDrawable settingPNGColor(Context context, int resId, int color) {       //设置.png类型的形状图的颜色
        Drawable drawable= ContextCompat.getDrawable(context,resId);
        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
        PorterDuff.Mode mode=PorterDuff.Mode.SRC_ATOP;
        ColorFilter colorFilter=new PorterDuffColorFilter(color,mode);
        Bitmap newBitmap=bitmapDrawable.getBitmap().copy(bitmapDrawable.getBitmap().getConfig(),true);
        BitmapDrawable newDrawable=new BitmapDrawable(context.getResources(),newBitmap);
        newDrawable.setColorFilter(colorFilter);

        return newDrawable;
    }
}
