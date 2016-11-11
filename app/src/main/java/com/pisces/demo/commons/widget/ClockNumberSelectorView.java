package com.pisces.demo.commons.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.pisces.demo.R;

import java.util.List;

/**
 * @author kevin_yao
 */
public class ClockNumberSelectorView extends View {

    private Paint mBitmapPaint;
    private Paint mColorPaint;

    private Paint mTextPaint;

    private static final Rect sRect = new Rect();

    private float mCellRadius;
    private float mCellTextSize;
    private Cell[] mCellArray = new Cell[12];

    private Drawable mClockImageDrawable;
    private Drawable mHandImageDrawable;
    private Drawable mColorDotDrawable;

    private float mTouchDownX;
    private float mTouchDownY;

    private Cell mSelectedCell;

    @ColorInt
    private int mCellNumberEnabledColor;
    @ColorInt
    private int mCellNumberDisabledColor;
    @ColorInt
    private int mCellNumberSelectedColor;
    @ColorInt
    private int mCellSelectedColor;

    private final int INVALID_INDICATOR = -1;

    private OnClockNumberSelectListener mOnClockNumberSelectListener;

    public ClockNumberSelectorView(Context context) {
        this(context, null);
    }

    public ClockNumberSelectorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockNumberSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.ClockNumberSelectorView);
        mCellRadius = ta.getDimension(R.styleable.ClockNumberSelectorView_cellRadius,
                getResources().getDimension(R.dimen.clock_number_selector_cell_default_radius));

        mCellTextSize = ta.getDimension(R.styleable.ClockNumberSelectorView_cellTextSize,
                getResources().getDimension(R.dimen.clock_number_selector_cell_text_default_size));

        mClockImageDrawable = ta.getDrawable(R.styleable.ClockNumberSelectorView_watchFaceImage);
        mHandImageDrawable = ta.getDrawable(R.styleable.ClockNumberSelectorView_handImage);
        mColorDotDrawable = ta.getDrawable(R.styleable.ClockNumberSelectorView_colorDotImage);

        mCellSelectedColor = ta.getColor(
                R.styleable.ClockNumberSelectorView_cellBackgroundColorSelected,
                ContextCompat.getColor(ctx, R.color.clock_number_selector_cell_selected));
        mCellNumberDisabledColor = ta.getColor(
                R.styleable.ClockNumberSelectorView_cellNumberColorDisabled,
                ContextCompat.getColor(ctx, R.color.clock_number_selector_cell_number_disabled));
        mCellNumberEnabledColor = ta.getColor(
                R.styleable.ClockNumberSelectorView_cellNumberColorEnabled,
                ContextCompat.getColor(ctx, R.color.clock_number_selector_cell_number_enabled));
        mCellNumberSelectedColor = ta.getColor(
                R.styleable.ClockNumberSelectorView_cellNumberColorSelected,
                ContextCompat.getColor(ctx, R.color.clock_number_selector_cell_number_selected));
        ta.recycle();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);

        mColorPaint = new Paint();
        mColorPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mCellTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        // init cells
        for (int i = 0; i < mCellArray.length; i++) {
            int number = i + 1;
            Cell cell = new Cell(number);
            cell.radius = mCellRadius;
            mCellArray[i] = cell;
        }
    }

    /**
     * Override this method to set the view size to be a square,
     * take the minimal size of {@link widthMeasureSpec} and {@link heightMeasureSpec} as square's width.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSpecSize, heightSpecSize);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // calculate cell bounds
        if (w != 0 && h != 0) {
            int size = Math.min(w, h);

            // ignore padding
            float outterRadius = size / 2.0f; // square
            float innerRadius = outterRadius - mCellRadius;

            for (Cell cell : mCellArray) {
                float cellCenterX = outterRadius -
                        innerRadius * (float) Math.cos(Math.toRadians(cell.degree));
                float cellCenterY = outterRadius -
                        innerRadius * (float) Math.sin(Math.toRadians(cell.degree));

                float cellLeft = cellCenterX - cell.radius;
                float cellTop = cellCenterY - cell.radius;
                cell.bounds.set(cellLeft, cellTop, cellLeft + cell.radius * 2, cellTop + cell.radius * 2);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = Math.min(getWidth(), getHeight());
        // draw watch face
        if (mClockImageDrawable != null) {
            int left = (size - mClockImageDrawable.getIntrinsicWidth()) / 2;
            int top = (size - mClockImageDrawable.getIntrinsicHeight()) / 2;
            mClockImageDrawable.setBounds(left, top,
                    left + mClockImageDrawable.getIntrinsicWidth(),
                    top + mClockImageDrawable.getIntrinsicHeight());
            mClockImageDrawable.draw(canvas);
        }

        // draw color dot
        if (mColorDotDrawable != null) {
            int left = (size - mColorDotDrawable.getIntrinsicWidth()) / 2;
            int top = (size - mColorDotDrawable.getIntrinsicHeight()) / 2;
            mColorDotDrawable.setBounds(left, top,
                    left + mColorDotDrawable.getIntrinsicWidth(),
                    top + mColorDotDrawable.getIntrinsicHeight());
            mColorDotDrawable.draw(canvas);
        }

        // draw hands
        if (mHandImageDrawable != null) {
            canvas.save();
            if (mSelectedCell != null) {
                canvas.rotate(mSelectedCell.degree - 90, size / 2.0f, size / 2.0f);
            }
            int left = (size - mHandImageDrawable.getIntrinsicWidth()) / 2;
            int top = size / 2 - mHandImageDrawable.getIntrinsicHeight();
            mHandImageDrawable.setBounds(left, top,
                    left + mHandImageDrawable.getIntrinsicWidth(),
                    top + mHandImageDrawable.getIntrinsicHeight());
            mHandImageDrawable.draw(canvas);
            canvas.restore();
        }

        // draw cells
        for (Cell cell : mCellArray) {
            cell.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                for (Cell cell : mCellArray) {
                    if (cell.bounds.contains(event.getX(), event.getY())
                            && cell.bounds.contains(mTouchDownX, mTouchDownY)) {
                        if (cell.state != Cell.STATE_DISABLED) {
                            if (mSelectedCell != null && mSelectedCell != cell) {
                                mSelectedCell.state = Cell.STATE_ENABLE;
                            }

                            mSelectedCell = cell;
                            mSelectedCell.state = Cell.STATE_SELECTED;
                            invalidate();

                            if (mOnClockNumberSelectListener != null) {
                                mOnClockNumberSelectListener.onNumberSelected(mSelectedCell.number);
                            }

                            playSoundEffect(SoundEffectConstants.CLICK);
                        }

                        break;
                    }
                }

                break;
        }

        return true;
    }

    public void setOnClockNumberSelectListener(OnClockNumberSelectListener listener) {
        mOnClockNumberSelectListener = listener;
    }

    /**
     * Listener for external subscribers to receive the call back when a number selected
     */
    public interface OnClockNumberSelectListener {

        /**
         * Called when a clock number selected
         */
        void onNumberSelected(int clockNumber);
    }

    public int updateNumbersAndGetDefaultNumber(int currentSelectedNumber,
                                                List<Integer> selectedNumbers) {
        // filter out the selected numbers
        for (Cell cell : mCellArray) {
            if (selectedNumbers.contains(cell.number)) {
                cell.state = Cell.STATE_DISABLED;
            }
        }

        if (currentSelectedNumber != INVALID_INDICATOR) {
            for (Cell cell : mCellArray) {
                if (cell.number == currentSelectedNumber) {
                    mSelectedCell = cell;
                    mSelectedCell.state = Cell.STATE_SELECTED;
                    break;
                }
            }
        } else {
            // if selectNumbers is empty, so the minimal number would be 1
            Cell minCell = new Cell(Integer.MAX_VALUE); // a test invalid cell
            for (Cell cell : mCellArray) {
                if (cell.state == Cell.STATE_ENABLE
                        && cell.number < minCell.number) {
                    minCell = cell;
                }
            }

            // change minimal cell to selected state
            if (minCell.number != Integer.MAX_VALUE) {
                mSelectedCell = minCell;
                mSelectedCell.state = Cell.STATE_SELECTED;
            }

        }
        invalidate();

        if (mSelectedCell == null) {
            return INVALID_INDICATOR;
        } else {
            return mSelectedCell.number;
        }
    }

    private class Cell {

        public static final int STATE_ENABLE = 0;   //  enable color
        public static final int STATE_DISABLED = 1;  // disable color
        public static final int STATE_SELECTED = 2; // selected color

        public int number;
        public float degree;
        public float radius;
        public int state = STATE_ENABLE;    // default state

        public RectF bounds; // touch area to receive touch event


        public Cell(int number) {
            this.number = number;

            // calculate the clock number hand degree on clock plate, start from 1
            degree = 90 + 30 * number;
            if (degree > 360) {
                degree -= 360;
            }

            bounds = new RectF();
        }

        public void draw(Canvas canvas) {
            // draw cell circle background
            if (state == STATE_SELECTED) {
                mColorPaint.setColor(mCellSelectedColor);
            } else if (state == STATE_ENABLE || state == STATE_DISABLED) {
                mColorPaint.setColor(Color.TRANSPARENT);
            }
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, mColorPaint);

            // draw cell number text
            String text = String.valueOf(number);
            mTextPaint.getTextBounds(text, 0, text.length(), sRect);
            if (state == STATE_SELECTED) {
                mTextPaint.setColor(mCellNumberSelectedColor);
            } else if (state == STATE_DISABLED) {
                mTextPaint.setColor(mCellNumberDisabledColor);
            } else if (state == STATE_ENABLE) {
                mTextPaint.setColor(mCellNumberEnabledColor);
            }
            canvas.drawText(text, bounds.centerX(),
                    bounds.centerY() - sRect.exactCenterY(), mTextPaint);
        }
    }
}
