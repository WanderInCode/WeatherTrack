package com.example.zhanyh.weathertrack.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.zhanyh.weathertrack.model.WeatherInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanyh on 15-11-7.
 */
public class CartesianCoordinates extends View {

    private Paint mAxis = new Paint();
    private Paint mFrame = new Paint();
    private Paint mText = new Paint();
    private Paint mDot = new Paint();
    private List<WeatherInfo> trackData;
    private float mWidth;
    private float mHeight;
    private float intervalX;
    private float intervalY;
    private int clickDotPosition;
    private int xIndex;
    private boolean isClick = false;
    private int radius = 15;
    private float clickRadius = 40f;
    private int drawTrackDataLength = 10;
    private List<Float> dotsPosition = new ArrayList<>(drawTrackDataLength * 2);
    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public CartesianCoordinates(Context context, AttributeSet attr) {
        super(context, attr, 0);
    }

    public void setTrackData(List<WeatherInfo> data) {
        trackData = data;
    }

//    public boolean checkInAxis(MotionEvent event) {
//        float eventX = event.getX();
//        float eventY = event.getY();
//        Log.d("View Left", getX() + "");
//        Log.d("Event Left", eventX + "");
//        if (eventX >= 60f && eventX <= mWidth - 60f && eventY >= 50f && eventY <= mHeight - 50f) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean checkInBox(MotionEvent event) {
        if (intervalX <= 2 * clickRadius) {
            return false;
        }
        isClick = true;
        float eventX = event.getX();
        float eventY = event.getY();
        xIndex = (int) ((eventX - 90f) / intervalX);
        if (xIndex >= drawTrackDataLength || xIndex < 0) {
            isClick = false;
            return false;
        }
        if (xIndex == 0) {
            if ((eventX-dotsPosition.get(0))<=clickRadius ||
                    (dotsPosition.get(0)-eventX)<=clickRadius) {
                if ((eventY-dotsPosition.get(1))<=clickRadius && (eventY-dotsPosition.get(1))>= 0 ||
                        (dotsPosition.get(1)-eventY)<=clickRadius && (dotsPosition.get(1)-eventY)>= 0) {
                    clickDotPosition = 0;
                    return true;
                }
            }
        } else if (xIndex == drawTrackDataLength - 1) {
            if ((eventX-dotsPosition.get(xIndex*2))<=clickRadius) {
                if ((eventY-dotsPosition.get(xIndex*2+1))<=clickRadius && (eventY-dotsPosition.get(xIndex*2+1))>= 0 ||
                        (dotsPosition.get(xIndex*2+1)-eventY)<=clickRadius && (dotsPosition.get(xIndex*2+1)-eventY)>= 0) {
                    clickDotPosition = xIndex;
                    return true;
                }
            }
        } else {
            if ((eventX-dotsPosition.get(xIndex*2))<=clickRadius) {
                if ((eventY-dotsPosition.get(xIndex*2+1))<=clickRadius && (eventY-dotsPosition.get(xIndex*2+1)) >= 0 ||
                        (dotsPosition.get(xIndex*2+1)-eventY)<=clickRadius && (dotsPosition.get(xIndex*2+1)-eventY) >= 0) {
                    clickDotPosition = xIndex;
                    return true;
                }
            } else if ((dotsPosition.get((xIndex+1)*2)-eventX)<=clickRadius) {
                if ((eventY-dotsPosition.get((xIndex+1)*2+1))<=clickRadius && (eventY-dotsPosition.get((xIndex+1)*2+1)) >= 0 ||
                        (dotsPosition.get((xIndex+1)*2+1)-eventY)<=clickRadius && (dotsPosition.get((xIndex+1)*2+1)-eventY) >= 0) {
                    clickDotPosition = xIndex + 1;
                    return true;
                }
            }
        }
        isClick = false;
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (trackData == null) {
            drawAxis(canvas);
        } else if (isClick) {
            drawAxis(canvas);
            drawTemperatureLine(canvas);
            drawNotice(canvas, 60);
            isClick = false;
        } else {
            drawAxis(canvas);
            drawTemperatureLine(canvas);
        }
    }

    private void drawAxis(Canvas canvas) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mAxis.setColor(Color.BLACK);
        mAxis.setStyle(Paint.Style.FILL);
        mAxis.setStrokeWidth(4);
        canvas.drawLine(60, mHeight - 50, 60, 50, mAxis);
        intervalY = (mHeight - 100) / 6;
        float start = 50 + intervalY / 2;
        int startNum = 40;
        float textSize = 30f;
        mText.setColor(Color.BLACK);
        mText.setStyle(Paint.Style.FILL);
        mText.setStrokeWidth(1);
        mText.setFlags(Paint.ANTI_ALIAS_FLAG);
        mText.setTextAlign(Paint.Align.RIGHT);
        mText.setTextSize(textSize);
        String numToString;
        float textBaseLine;
        for (int i = 0; i < 7; i++) {
            canvas.drawLine(60, start, 75, start, mAxis);
            numToString = Integer.toString(startNum, 10);
            Paint.FontMetrics fontMetrics = mText.getFontMetrics();
            textBaseLine = start - (fontMetrics.bottom + fontMetrics.top) / 2;
            canvas.drawText(numToString, 55, textBaseLine, mText);
            start += intervalY;
            startNum -= 10;
        }
        canvas.drawLine(60, 50 + (mHeight - 100f) * 3 / 4 , mWidth - 60, 50 + (mHeight - 100f) * 3 / 4, mAxis);
    }

    private void drawTemperatureLine(Canvas canvas) {
        List<WeatherInfo> drawTrackData;
        dotsPosition.clear();
        int trackDataLength = trackData.size();
        if (trackDataLength < 1) {
            return;
        } else if (trackDataLength > drawTrackDataLength) {
            drawTrackData = trackData.subList(0, drawTrackDataLength);
        } else {
            drawTrackData = trackData;
            drawTrackDataLength = trackDataLength;
        }
        mDot.setColor(Color.RED);
        mDot.setStyle(Paint.Style.FILL);
        //两边各向内缩进30px
        intervalX = (mWidth - 120 - 60) / (drawTrackDataLength - 1);
        float xStart = 90f;
        float yStart = 50 + intervalY / 2;
        float dotX;
        float dotY;
        for (int i = 0; i < drawTrackDataLength; i++) {
            WeatherInfo dot = drawTrackData.get(i);
            dotY = yStart + (40f - dot.tempDigit) * intervalY / 10;
            dotX = xStart;
            canvas.drawCircle(dotX, dotY, radius, mDot);
            dotsPosition.add(dotX);
            dotsPosition.add(dotY);
            xStart = dotX + intervalX;
        }
        for (int j = 0; j < dotsPosition.size() - 2; j+=2) {
            canvas.drawLine(dotsPosition.get(j), dotsPosition.get(j + 1),
                    dotsPosition.get(j + 2), dotsPosition.get(j + 3), mAxis);
        }


    }

    private void drawNotice(Canvas canvas, int yAxis) {
        float t = 15f;
        float p = 10f;
        float originX = dotsPosition.get(clickDotPosition * 2);
        float originY = dotsPosition.get(clickDotPosition * 2 + 1);
        float textSize = 40f;
        String time = dateFormat.format(trackData.get(clickDotPosition).getLast_update());
        mText.setColor(Color.BLACK);
        mText.setStyle(Paint.Style.FILL);
        mText.setStrokeWidth(1);
        mText.setFlags(Paint.ANTI_ALIAS_FLAG);
        mText.setTextAlign(Paint.Align.CENTER);
        mText.setTextSize(textSize);
        float textWidth = mText.measureText(time);
        float leftBorder = originX + t - p - textWidth;
        Paint.FontMetrics fontMetrics = mText.getFontMetrics();
        /**
         * better see http://blog.csdn.net/hursing/article/details/18703599
         */
        float textBaseline = originY - t - p - (textSize + fontMetrics.top + fontMetrics.bottom) / 2;
        Path path = new Path();
        mFrame.setStyle(Paint.Style.STROKE);
        mFrame.setStrokeWidth(1);
        mFrame.setColor(Color.BLACK);
        if (leftBorder <= yAxis) {
            path.moveTo(originX, originY);
            path.lineTo(originX - t, originY - t);
            path.lineTo(originX - t - p, originY - t);
            path.lineTo(originX - t - p, originY - t - textSize - 2 * p);
            path.lineTo(originX - t + p + textWidth, originY - t - textSize - 2 * p);
            path.lineTo(originX - t + p + textWidth, originY - t);
            path.lineTo(originX + t, originY - t);
            path.close();
            canvas.drawPath(path, mFrame);
            canvas.drawText(time, originX - t + textWidth / 2, textBaseline, mText);
        } else {
            path.moveTo(originX, originY);
            path.lineTo(originX + t, originY - t);
            path.lineTo(originX + t + p, originY - t);
            path.lineTo(originX + t + p, originY - t - textSize - 2 * p);
            path.lineTo(originX + t - p - textWidth, originY - t - textSize - 2 * p);
            path.lineTo(originX + t - p - textWidth, originY - t);
            path.lineTo(originX - t, originY - t);
            path.close();
            canvas.drawPath(path, mFrame);
            canvas.drawText(time, originX + t - textWidth / 2, textBaseline, mText);
        }
    }

}
