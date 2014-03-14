package utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by wouter on 28/02/14.
 */
public abstract class Utils {

    public static float convertPixelsToDp(float px,Activity activity){
        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static Bitmap grayScaleImage(Bitmap source) {
        final double GS_RED = 0.299, GS_GREEN = 0.587, GS_BLUE = 0.114;
        Bitmap bmOut = Bitmap.createBitmap(source.getWidth(),source.getHeight(),source.getConfig());
        int A,R,G,B,pixel;
        int width = source.getWidth();
        int heigth = source.getHeight();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < heigth; ++j) {
                pixel = source.getPixel(i,j);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                bmOut.setPixel(i, j, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static void showDialog(Activity activity, String message) {

    }


}
