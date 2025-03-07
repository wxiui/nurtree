package com.csstj.nurtree.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleCountdownView extends View {
    private Paint circlePaint; // 圆形背景画笔
    private Paint progressPaint; // 进度条画笔
    private Paint textPaint; // 文本画笔
    private RectF rectF; // 用于绘制圆形的矩形区域

    private float progress = 0; // 当前进度（0-100）
    private String text = "0"; // 显示的文本
    private int mins = 0; // 显示分钟

    private int backgroundColor = Color.GREEN; // 背景圆环颜色
    private int progressColor = Color.WHITE; // 进度圆环颜色
    private int strokeWidth = 30; // 圆环宽度

    private int textColor = Color.BLACK; // 文字颜色
    private float textSize = 120f; // 文字大小

    private int warnColor = Color.RED;

    public CircleCountdownView(Context context) {
        super(context);
        init();
    }

    public CircleCountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 初始化画笔和参数
    private void init() {
        // 圆形背景画笔
        circlePaint = new Paint();
        //圆环背景色
        circlePaint.setColor(backgroundColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        //圆环宽度
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setAntiAlias(true);

        // 进度条画笔
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setAntiAlias(true);

        // 文本画笔
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        // 用于绘制圆形的矩形区域
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mins<20){
            circlePaint.setColor(warnColor);
            textPaint.setColor(warnColor);
        }else {
            circlePaint.setColor(backgroundColor);
            textPaint.setColor(textColor);
        }

        // 计算圆形区域
        //float padding = 10;
        float padding = strokeWidth / 2f;
        rectF.set(padding, padding, getWidth() - padding, getHeight() - padding);

        // 绘制背景圆
        canvas.drawOval(rectF, circlePaint);

        // 绘制进度条
        float sweepAngle = 360 * (progress / 100);
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        // 绘制文本
        float x = getWidth() / 2f;
        float y = getHeight() / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(text, x, y, textPaint);
    }

    // 设置进度
    public void setProgress(float progress) {
        this.progress = progress;
        this.text = (int) progress + "%"; // 更新文本
        invalidate(); // 重绘视图
    }

    // 设置文本
    public void setText(String text) {
        this.text = text;
        invalidate(); // 重绘视图
    }

    public void setMins(int mins) {
        this.mins = mins;
        this.text = mins+"";
        invalidate(); // 重绘视图
    }
}