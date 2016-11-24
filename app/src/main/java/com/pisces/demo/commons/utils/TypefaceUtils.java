package com.pisces.demo.commons.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.widget.TextView;

import com.pisces.demo.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TypefaceUtils {

    public static final int TYPEFACE_REGULAR = 0;
    public static final int TYPEFACE_LIGHT = 1;
    public static final int TYPEFACE_MEDIUM = 2;

    private static final int[] ATTRS_FONT_PATH = new int[]{R.attr.fontPath};

    @IntDef({TYPEFACE_REGULAR, TYPEFACE_LIGHT, TYPEFACE_MEDIUM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypefaceValue {
    }

    public static void applyTypeface(TextView view, @TypefaceValue int typeface) {
        view.setTypeface(getTypeface(view.getContext(), typeface));
    }

    public static void applyTextAppearance(TextView view, @StyleRes int textAppearance) {
        view.setTextAppearance(view.getContext(), textAppearance);

        // Apply typeface.
        TypedArray ta = view.getContext().obtainStyledAttributes(textAppearance, ATTRS_FONT_PATH);
        String fontPath = ta.getString(0);
        if (TextUtils.isEmpty(fontPath)) {
            // If not specified, use default typeface.
            fontPath = view.getResources().getString(R.string.fontGothamRegular);
        }
        view.setTypeface(
                uk.co.chrisjenx.calligraphy.TypefaceUtils.load(
                        view.getResources().getAssets(), fontPath));
        ta.recycle();
    }

    public static Typeface getTypeface(Context context, @TypefaceValue int typeface) {
        String fontPath = null;
        switch (typeface) {
            case TYPEFACE_REGULAR:
                fontPath = context.getResources().getString(R.string.fontGothamRegular);
                break;
            case TYPEFACE_LIGHT:
                fontPath = context.getResources().getString(R.string.fontGothamLight);
                break;
            case TYPEFACE_MEDIUM:
                fontPath = context.getResources().getString(R.string.fontGothamMedium);
                break;
            default:
                throw new IllegalArgumentException("Invalid typeface: " + typeface);
        }
        return uk.co.chrisjenx.calligraphy.TypefaceUtils.load(context.getResources().getAssets(),
                fontPath);
    }
}
