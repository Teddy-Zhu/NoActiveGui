package com.v2dawn.noactivegui.ui;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

public class Utils {

    public static Drawable convertToGrayscale(Drawable drawable)
    {
        Drawable newDrawable = drawable.getConstantState().newDrawable().mutate();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        newDrawable.setColorFilter(filter);
        return newDrawable;
    }

    /**
     * Converts dp to px
     *
     * @param res Resources
     * @param dp  the value in dp
     * @return int
     */
    public static int toPixels(Resources res, float dp) {
        return (int) (dp * res.getDisplayMetrics().density);
    }

    /**
     * Converts sp to px
     *
     * @param res Resources
     * @param sp  the value in sp
     * @return int
     */
    public static int toScreenPixels(Resources res, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isRtl(Resources res) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) &&
                (res.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
    }
}
