package com.pisces.demo.commons.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.pisces.demo.R;
import com.pisces.demo.commons.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Currently we only support horizontal, cause it only make sense in horizontal mode
 * TODO...
 */
public class InstructionView extends View {

    private static final float DEFAULT_ITEM_SPACE = 10.0f; // 10dp
    private static final float DEFAULT_ITEM_DRAWABLE_PADDING = 10.0f; // 10dp
    private static final float DEFAULT_TEXT_SIZE = 14.0f; // 14sp

    private static final int DEFAULT_TYPEFACE = TypefaceUtils.TYPEFACE_LIGHT; // 14sp
    private static final int DEFAULT_COLOR = Color.GRAY;

    private static Rect sRect = new Rect();

    private CharSequence[] mCharSequences;

    private float mItemSpace;

    private TextPaint mTextPaint;
    private Drawable mItemDrawable;
    private float mItemDrawablePadding;

    private boolean mIsNeedRecalculate = false;

    private List<StaticLayout> mStaticLayoutList = new ArrayList<>();

    public InstructionView(Context context) {
        this(context, null);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.InstructionView);
        mCharSequences = ta.getTextArray(R.styleable.InstructionView_textArray);
        mItemSpace = ta.getDimension(R.styleable.InstructionView_itemSpace,
                dp2px(ctx, DEFAULT_ITEM_SPACE));
        mItemDrawable = ta.getDrawable(R.styleable.InstructionView_itemDrawable);
        mItemDrawable.setBounds(0, 0, mItemDrawable.getIntrinsicWidth(), mItemDrawable.getIntrinsicHeight());
        mItemDrawablePadding = ta.getDimension(R.styleable.InstructionView_itemDrawablePadding,
                dp2px(ctx, DEFAULT_ITEM_DRAWABLE_PADDING));
        float fontSize = ta.getDimensionPixelSize(R.styleable.InstructionView_android_textSize,
                (int) sp2px(ctx, DEFAULT_TEXT_SIZE));
        int textColor = ta.getColor(R.styleable.InstructionView_android_textColor, DEFAULT_COLOR);
        ta.recycle();

        mTextPaint = new TextPaint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setTextSkewX(0);
        mTextPaint.setTextScaleX(1.0f);
        mTextPaint.setTextSize(fontSize);
        mTextPaint.setTypeface(TypefaceUtils.getTypeface(ctx, DEFAULT_TYPEFACE));
        mTextPaint.setColor(textColor);
        mItemDrawable.setColorFilter(new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_IN));
    }

    public CharSequence[] getCharSequeneces() {
        return mCharSequences;
    }

    public void setCharSequences(CharSequence[] charSequences) {
        mCharSequences = charSequences;

        mIsNeedRecalculate = true;
        requestLayout();
    }

    public void setTextColor(@ColorInt int color) {
        mTextPaint.setColor(color);
        mItemDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

        if (mCharSequences != null) {
            invalidate();
        }
    }

    public void setTypeFace(Typeface typeFace) {
        mTextPaint.setTypeface(typeFace);

        if (mCharSequences != null) {
            mIsNeedRecalculate = true;
            requestLayout();
        }
    }

    private int mDesiredHeight;
    private int mDesiredWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // calculate the desired height and width
        if ((mCharSequences != null && mDesiredHeight == 0) || mIsNeedRecalculate) {
            int textPadding = (int) (mItemDrawable.getIntrinsicWidth() + mItemDrawablePadding);
            int textWidthSize = widthSize - textPadding;
            int itemSpace = Math.round(mItemSpace); // should be int

            int desiredTextWidth = 0;
            for (CharSequence charSequence : mCharSequences) {
                if (TextUtils.isEmpty(charSequence)) {
                    continue;
                }

                StaticLayout staticLayout = new StaticLayout(charSequence, mTextPaint,
                        textWidthSize, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
                mStaticLayoutList.add(staticLayout);
                mDesiredHeight += staticLayout.getHeight() + itemSpace;

                desiredTextWidth = Math.max(desiredTextWidth,
                        Math.round(StaticLayout.getDesiredWidth(charSequence, mTextPaint)));
            }
            desiredTextWidth = Math.min(textWidthSize, desiredTextWidth);

            mDesiredHeight -= itemSpace; // remove last space
            mDesiredWidth = desiredTextWidth + textPadding;

            mIsNeedRecalculate = false;
        }

        int width;
        int height;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if ((widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED)
                && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = mDesiredWidth;
        } else {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        }

        if ((heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)
                && lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = mDesiredHeight;
        } else {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean isLayoutRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
        float mCurItemTop = 0.0f;

        for (StaticLayout staticLayout : mStaticLayoutList) {
            float padding = mItemDrawable.getIntrinsicWidth() + mItemDrawablePadding;
            float textHeight = staticLayout.getHeight();
            // won't draw the rest of text if the space is not enough
            if (mCurItemTop + textHeight > getHeight()) {
                break;
            }

            // draw indicator
            canvas.save();
            staticLayout.getLineBounds(0, sRect);
            float itemDrawableTop = (float) sRect.height() / 2 -
                    (float) mItemDrawable.getIntrinsicHeight() / 2;
            if (isLayoutRtl) {
                canvas.translate(getWidth() - mItemDrawable.getIntrinsicWidth(),
                        mCurItemTop + itemDrawableTop);
            } else {
                canvas.translate(0, mCurItemTop + itemDrawableTop);
            }
            mItemDrawable.draw(canvas);
            canvas.restore();

            // draw text
            canvas.save();
            if (isLayoutRtl) {
                canvas.translate(0, mCurItemTop);
            } else {
                canvas.translate(padding, mCurItemTop);
            }
            staticLayout.draw(canvas);  // only accept
            canvas.restore();

            // next item position
            mCurItemTop += textHeight + mItemSpace;
        }
    }

    private static float sp2px(Context ctx, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                ctx.getResources().getDisplayMetrics());
    }

    private static float dp2px(Context ctx, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ctx.getResources().getDisplayMetrics());
    }
}
