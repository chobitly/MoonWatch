package com.chobitly.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SilverXia on 1/6/15.
 */
public class MoonRotateUtil {

    public static Bitmap getRotateImage(Context context, int imageResID, float degree) {
        Matrix matrix = new Matrix();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageResID);
        // 设置旋转角度
        matrix.setRotate(degree);
        // 重新绘制Bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (rotateBitmap != bitmap) {
            bitmap.recycle();
        }
        return BitmapCut.toRoundBitmap(rotateBitmap, width, height, true);
    }


    //月相周期为2551443s，即2551443s转180度，850481s转60度，212620.25s转15度，42524.05s（11h48m44.05s）转3度
    //与2015-01-15 04:53（世界时）比较，计算需要转几个3度
    public static float getDegree() {
        try {
            Date base = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.UK).parse("2015-01-05 04:53");
            double _3DegreeCount = Arith.round(Arith.div(System.currentTimeMillis() - base.getTime(), 42524050), 0);//除以后向下取整来计算需要转多少个3度
            int remainder = ((int) _3DegreeCount) % 60;//计算刨去足够转180度的部分还要转多少度
            return 3 * remainder;
        } catch (ParseException e) {
            return 0;
        }
    }
}
