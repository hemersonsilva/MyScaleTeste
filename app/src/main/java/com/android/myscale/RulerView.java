package com.android.myscale;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

public class RulerView extends View{
    static int screenSize = 400;
    static private float pxmm = screenSize / 67.f;
    int width, height, midScreenPoint;
    float startingPoint = 0;
    float downpoint = 0, movablePoint = 0, downPointClone = 0;
    private float mainPoint = 0, mainPointClone = 0;
    float userStartingPoint = 0.f;
    boolean isDown = false;
    boolean isUpward = false;
    private boolean isMove;
    private onViewUpdateListener mListener;
    private Paint gradientPaint;
    private float rulersize = 0;
    private Paint rulerPaint, textPaint, goldenPaint;
    private int endPoint;
    boolean isSizeChanged = false;
    private int scaleLineSmall;
    private int scaleLineMedium;
    private int scaleLineLarge;
    private int textStartPoint;
    private int yellowLineStrokeWidth;
    boolean isFirstTime = true;
    private OverScroller mScroller;
    Scroller scroller;

    public RulerView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        yellowLineStrokeWidth = (int) getResources().getDimension(R.dimen.yellow_line_stroke_width);
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rulersize = pxmm * 10;
        rulerPaint = new Paint();
        rulerPaint.setStyle(Paint.Style.STROKE);
        rulerPaint.setStrokeWidth(1);
        rulerPaint.setAntiAlias(false);
        rulerPaint.setColor(Color.WHITE);
        textPaint = new TextPaint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
        textPaint.setTypeface(typeface);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(0);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getResources().getDimension(R.dimen.txt_size));
        textPaint.setColor(Color.WHITE);
        goldenPaint = new Paint();
        goldenPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        goldenPaint.setColor(context.getResources().getColor(R.color.yellow));
        goldenPaint.setStrokeWidth(yellowLineStrokeWidth);
        goldenPaint.setStrokeJoin(Paint.Join.ROUND);
        goldenPaint.setStrokeCap(Paint.Cap.ROUND);
        goldenPaint.setPathEffect(new CornerPathEffect(10));
        goldenPaint.setAntiAlias(true);
        scaleLineSmall = (int) getResources().getDimension(R.dimen.scale_line_small);
        scaleLineMedium = (int) getResources().getDimension(R.dimen.scale_line_medium);
        scaleLineLarge = (int) getResources().getDimension(R.dimen.scale_line_large);
        textStartPoint = (int) getResources().getDimension(R.dimen.text_start_point);
         scroller = new Scroller(context);
        mScroller = new OverScroller(context);



    }

    public void setUpdateListener(onViewUpdateListener onViewUpdateListener) {
        mListener = onViewUpdateListener;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
        screenSize = height;
        pxmm = screenSize / 167.f;
        midScreenPoint = height / 2;
        endPoint = width - 100;
        if (isSizeChanged) {
            isSizeChanged = false;
            mainPoint = midScreenPoint - (userStartingPoint * 10 * pxmm);
        }
//        gradientPaint.setShader(new LinearGradient(30, 90, width, rulersize, getResources().getColor(R.color.green),
//                getResources().getColor(R.color.transparent_white), android.graphics.Shader.TileMode.MIRROR));
    }

    @Override
    public void onDraw(Canvas canvas) {

        //canvas.drawRect(0f, midScreenPoint - (rulersize / 2), width, midScreenPoint + (rulersize / 2), gradientPaint);

        startingPoint = mainPoint;
        for (int i = 99; i > -99; --i) {
            if (startingPoint > screenSize) {
                break;
            }
            startingPoint = startingPoint + pxmm;

            int size = (i % 10 == 0) ? -scaleLineLarge : (i % 5 == 0) ? -scaleLineMedium : -scaleLineSmall;
            int size2 = scaleLineLarge;

            canvas.drawLine(endPoint - size , startingPoint, endPoint , startingPoint, rulerPaint);

            if (i % 10 == 0) {
                canvas.drawText("" + (i), endPoint + 40, startingPoint + 8, textPaint);
            }
            canvas.drawLine( size2 + 25 , startingPoint, endPoint - 10, startingPoint, rulerPaint);

//            if (i % 10 == 0) {
//                canvas.drawText("" + (i / 10), endPoint - 70, startingPoint + 8, textPaint);
//            }
        }
        //canvas.drawLine(0f, midScreenPoint, width - 10, midScreenPoint, goldenPaint);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float valor = 0;

        mainPointClone = mainPoint;

        valor = (midScreenPoint + mainPointClone) / (pxmm) - 67;

        if (mListener != null) {
            mListener.onViewUpdate(valor);
        }

        Log.i("log,","Orientation - " +event.getOrientation());

        Log.i("teste","" + mScroller.isOverScrolled());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = true;
                isDown = true;
                isUpward = false;
                downpoint = event.getY();
                downPointClone = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                movablePoint = event.getY();
                if (downPointClone > movablePoint) {
                    /**
                     * if user first starts moving downward and then upwards then
                     * this method makes it to move upward
                     */
                    if (isUpward) {
                        downpoint = event.getY();
                        downPointClone = downpoint;
                    }
                    isDown = true;
                    isUpward = false;

                    /**
                     * make this differnce of 1, otherwise it moves very fast and
                     * nothing shows clearly
                     */
                    if (valor <= -99) {
                        return true;

                    } else {

                        if (downPointClone - movablePoint > 1) {
                            mainPoint = mainPoint + (-(downPointClone - movablePoint));
                            downPointClone = movablePoint;
                            invalidate();
                        }
                    }
                } else {
                    if (isMove) {
                        /**
                         * if user first starts moving upward and then downwards,
                         * then this method makes it to move upward
                         */
                        if (isDown) {
                            downpoint = event.getY();
                            downPointClone = downpoint;
                        }
                        isDown = false;
                        isUpward = true;
                        if (valor > 99) {

                            return true;
                        } else {

                            if (movablePoint - downpoint > 1) {
                                mainPoint = mainPoint + ((movablePoint - downPointClone));
                                downPointClone = movablePoint;
                                invalidate();
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

            default:
                break;
        }
        return true;
    }

    public void setStartingPoint(float point) {
        userStartingPoint = point;
        isSizeChanged = true;
        if (isFirstTime) {
            isFirstTime = false;
            if (mListener != null) {
                mListener.onViewUpdate(point);
            }
        }
    }

}