package oyh.ccmusic.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

import oyh.ccmusic.R;

/**
 * 音频条
 * Created by yihong.ou on 17-10-24.
 */
public class BarChartView extends LinearLayout implements Runnable{
    private ViewWrapper[] mViewWrapper;

    private int barchartCount = 3;
    private int barchartWidth = 3;
    private int barchartHeight = 4;
    private int barcharMarginLeft = 3;
    private int barchartDuration = 300;
    private int barcharBackColor;

    private boolean startAnimtor = false;

    @DrawableRes
    private int myShape;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        addBarView();
    }

    /**
     * 初始化配置
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BarChartView);
        barchartCount = typedArray.getInt(R.styleable.BarChartView_barchartCount, 3);
        barchartWidth = typedArray.getDimensionPixelSize(R.styleable.BarChartView_barchartWidth, 5);
        barchartHeight = typedArray.getDimensionPixelSize(R.styleable.BarChartView_barchartHeight, 4);
        barcharMarginLeft = typedArray.getDimensionPixelSize(R.styleable.BarChartView_barcharMarginLeft, 5);
        barchartDuration = typedArray.getInt(R.styleable.BarChartView_barchartDuration, 300);
        myShape = typedArray.getResourceId(R.styleable.BarChartView_barchartShape, 0);
        barcharBackColor = typedArray.getColor(R.styleable.BarChartView_barcharBackColor, Color.RED);
        typedArray.recycle();
    }

    /**
     * add View
     */
    private void addBarView() {
        if (barchartCount <= 0) {
            return;
        }
        mViewWrapper = new ViewWrapper[3];
        ImageView childView;
        LinearLayout.LayoutParams layoutParams;
        ViewWrapper viewWrapper;
        for (int i = 0; i < barchartCount; i++) {
            childView = new ImageView(getContext());
            if (myShape != 0) {
                childView.setBackgroundResource(myShape);
            } else {
                childView.setBackgroundColor(barcharBackColor);
            }
            layoutParams = new LayoutParams(4, 100);
            layoutParams.setMargins(3, 0, 0, 0);
            childView.setLayoutParams(layoutParams);
            addView(childView);
            viewWrapper = new ViewWrapper(childView);
            mViewWrapper[i] = viewWrapper;
        }
    }

    /**
     * 开始动画
     */
    public void start() {
        if (mViewWrapper == null || mViewWrapper.length <= 0) {
            return;
        }
        startAnimtor = true;
        Random a = new Random();
        for (int i = 0; i < mViewWrapper.length; i++) {
            startAnimator(mViewWrapper[i], a.nextInt(barchartHeight));
        }
        removeCallbacks(this);
        postDelayed(this, barchartDuration);
    }

    /**
     * 停止动画
     */
    public void stop() {
        startAnimtor = false;
        for (int i = 0; i < mViewWrapper.length; i++) {
            startAnimator(mViewWrapper[i], 1);
        }
    }

    private void startAnimator(ViewWrapper viewWrapper, int height) {
        viewWrapper.mTarget.clearAnimation();
        ObjectAnimator.ofInt(viewWrapper, "height", height).setDuration(barchartDuration).start();
    }

    @Override
    public void run() {
        if (startAnimtor) {
            start();
        }
    }



    private static class ViewWrapper {
        public View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }
}
