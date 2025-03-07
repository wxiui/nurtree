package com.csstj.nurtree.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.csstj.nurtree.common.util.ColorUtil;

import java.util.concurrent.ThreadLocalRandom;

public class CircleNumberView extends View {
    private Paint circlePaint;
    private Paint textPaint;
    private int number;
    private int circleColor = ColorUtil.hex2Int("#BDBDBD"); // 圆环颜色
    private int textColor = Color.BLACK; // 数字颜色

    private String[] Colors= {"#FF0000","#00FF00","#0000FF","#FFD700","#FF00FF","#FF4500","#800080",
            "#00FFFF","#FF7F50","#FF6F00","#87CEEB","#FF1493","#7CFC00","#8B0000","#4169E1"};

    public CircleNumberView(Context context) {
        super(context);
        init();
    }

    public CircleNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int randomIntInRange = ThreadLocalRandom.current().nextInt(0, 14);
        textColor = ColorUtil.hex2Int(Colors[randomIntInRange]);
        // 初始化画笔
        circlePaint = new Paint();
        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3); // 设置圆环宽度

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(38);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    // 设置数字
    public void setNumber(int number) {
        this.number = number;
        invalidate(); // 更新视图
    }

    // 设置数字
    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate(); // 更新视图
    }

    // 设置数字
    public void setCircleColor(int color) {
        circlePaint.setColor(color);
        invalidate(); // 更新视图
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 25; // 计算圆半径，减去圆环宽度的一半

        // 绘制圆环
        canvas.drawCircle(width / 2, height / 2, radius, circlePaint);

        // 绘制数字
        String text = String.valueOf(number);
        float x = width / 2;
        float y = height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2); // 使文本垂直居中
        canvas.drawText(text, x, y, textPaint);
    }
}
