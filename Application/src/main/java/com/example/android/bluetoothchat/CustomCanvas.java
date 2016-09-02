package com.example.android.bluetoothchat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
public class CustomCanvas extends View {
    Paint mPaint;

    public CustomCanvas(Context context, AttributeSet attrs) {  //コンストラクタ
        super(context, attrs);
        mPaint = new Paint();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // 背景、半透明
        canvas.drawColor(Color.BLACK);

        // 円
        mPaint.setColor(Color.argb(255, 68, 125, 255));
        mPaint.setStrokeWidth(30);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        // (x1,y1,r,paint) 中心x1座標, 中心y1座標, r半径
        canvas.drawCircle(50, 50, 20, mPaint);
    }

}
