package com.tencent.qcloud.tuikit.tuibeauty.view.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.StringRes;

public class ResourceUtils {
    private static final String TYPE_QUOTE_PREFIX = "@";
    private static final String TYPE_COLOR_PREFIX = "#";

    private static final String TYPE_STRING   = "string";
    private static final String TYPE_COLOR    = "color";
    private static final String TYPE_DRAWABLE = "drawable";

    public static int getDrawableId(String resName) {
        if (resName.startsWith(TYPE_QUOTE_PREFIX)) {
            return getResources().getIdentifier(resName, TYPE_DRAWABLE, TUIBeautyResourceParse.getPackageName());
        }
        throw new IllegalArgumentException("\"" + resName + "\" is illegal, must start with \"@\".");
    }

    public static int getStringId(String resName) {
        return getResources().getIdentifier(resName, TYPE_STRING, TUIBeautyResourceParse.getPackageName());
    }

    public static String getString(String resName) {
        if (resName.startsWith(TYPE_QUOTE_PREFIX)) {
            return getResources().getString(getStringId(resName.substring(1)));
        }
        return resName;
    }

    public static String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    public static int getColor(String resName) {
        if (resName.startsWith(TYPE_COLOR_PREFIX)) {
            return Color.parseColor(resName);
        }
        if (resName.startsWith(TYPE_QUOTE_PREFIX)) {
            return getResources().getColor(getColorId(resName));
        }
        throw new IllegalArgumentException("\"" + resName + "\" is unknown color.");
    }

    public static int getColorId(String resName) {
        return getResources().getIdentifier(resName, TYPE_COLOR, TUIBeautyResourceParse.getPackageName());
    }

    public static Resources getResources() {
        return TUIBeautyResourceParse.getApplication().getResources();
    }

    public static Drawable getLinearDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(new float[]{
                TUIBeautyResourceParse.dip2px(TUIBeautyResourceParse.getApplication(), 10), TUIBeautyResourceParse.dip2px(TUIBeautyResourceParse.getApplication(), 10),
                TUIBeautyResourceParse.dip2px(TUIBeautyResourceParse.getApplication(), 10), TUIBeautyResourceParse.dip2px(TUIBeautyResourceParse.getApplication(), 10),
                0, 0, 0, 0});
        return drawable;
    }
}