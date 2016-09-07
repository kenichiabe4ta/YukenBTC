package com.example.android.bluetoothchat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
public class CustomCanvas extends View {
    private Paint mPaint;
    private int mCanvas_w,mCanvas_h;
    private float[] mWavedt=new float[4096];

    public CustomCanvas(Context context, AttributeSet attrs) {  //コンストラクタ
        super(context, attrs);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas_w=getWidth();
        mCanvas_h=getHeight();
        //Log.d("canvas_w,canvas_h=",String.valueOf(mCanvas_w)+","+String.valueOf(mCanvas_h));
        //Log.d("canvas_root_w,h=",String.valueOf(getRootView().getWidth())+","+String.valueOf(getRootView().getHeight()));
        // 背景
        canvas.drawColor(Color.BLACK);
        // 枠線描画(10分割)
        drawFrame(canvas);
        // 波形描画
        drawWave(canvas);
    }
    private void drawFrame(Canvas canvas){
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(1);
        float h=mCanvas_h/10.0f;
        float w=mCanvas_w/10.0f;
        for (int i=0; i<10; i++){
            canvas.drawLine(0, h*i, mCanvas_w, h*i, mPaint);// 横線   drawline(始点x,y,終点x,y,Paint)
            canvas.drawLine(w*i, 0, w*i, mCanvas_h, mPaint);// 縦線   drawline(始点x,y,終点x,y,Paint)
        }
    }
    private void drawWave(Canvas canvas){
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        //drawLines (float[] pts,Paint paint)
        //pts 	float: Array of points to draw [x0 y0 x1 y1 x2 y2 ...]
        canvas.drawLines(mWavedt, mPaint);
    }
    public void setWavedt(int dt[]){
        for(int i=0;i<dt.length;i++){
            mWavedt[i*2]=i*mCanvas_w/dt.length;             //x軸 データ総数/100%幅
            mWavedt[i*2+1]=mCanvas_h-dt[i]*mCanvas_h/1023;  //y軸 10bit幅/100%高さ
        }
        // 再描画 invalidate→onDraw→drawFrame→drawWaveの順番で波形表示
        //invalidate();
    }
}
