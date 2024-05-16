package com.example.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

public class ClockView extends View{
    private int height, width;
    private int padding = 50;  // 表盘与视图边界的距离
    private Paint paint = new Paint();

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();  // 请求重新绘制视图，调用onDraw方法
            handler.postDelayed(this, 1000);  // 每秒更新一次
        }
    };

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler.post(updateRunnable);  // 开始更新循环
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - padding;

        drawCircle(canvas, centerX, centerY, radius);
        drawNumerals(canvas, centerX, centerY, radius);
        drawScale(canvas, centerX, centerY, radius);

        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR_OF_DAY) % 12;
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        // 计算指针角度
        float hourAngle = hours * 30 + minutes * 0.5f; // 每小时30度，每分钟0.5度
        float minuteAngle = minutes * 6; // 每分钟6度
        float secondAngle = seconds * 6; // 每秒钟6度

        // 绘制指针
        drawHand(canvas, centerX, centerY, radius * 0.5f, hourAngle, 8, true);
        drawHand(canvas, centerX, centerY, radius * 0.7f, minuteAngle, 6, false);
        drawHand(canvas, centerX, centerY, radius * 0.9f, secondAngle, 4, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(updateRunnable);
    }

    private void drawScale(Canvas canvas, int centerX, int centerY, int radius) {
        int tickLength = 20; // 普通刻度长度
        int largeTickLength = 30; // 每5分钟的较大刻度长度
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6); // 每刻度6度
            int lineLength = (i % 5 == 0) ? largeTickLength : tickLength; // 每5分钟一个大刻度

            int startX = (int) (centerX + Math.cos(angle) * (radius - lineLength));
            int startY = (int) (centerY + Math.sin(angle) * (radius - lineLength));
            int endX = (int) (centerX + Math.cos(angle) * radius);
            int endY = (int) (centerY + Math.sin(angle) * radius);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    private void drawCircle(Canvas canvas, int centerX, int centerY, int radius) {
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, radius, paint);

        drawScale(canvas, centerX, centerY, radius); // 调用绘制刻度的方法
    }

    private void drawNumerals(Canvas canvas, int centerX, int centerY, int radius) {
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        for (int num = 1; num <= 12; num++) {
            String numeral = String.valueOf(num);
            double angle = Math.PI / 6 * (num - 3);
            int x = (int) (centerX + Math.cos(angle) * radius * 0.85);
            int y = (int) (centerY + Math.sin(angle) * radius * 0.85);
            Rect textBounds = new Rect();
            paint.getTextBounds(numeral, 0, numeral.length(), textBounds);
            canvas.drawText(numeral, x, y + textBounds.height() / 2, paint);
        }
    }

    private void drawHand(Canvas canvas, int cx, int cy, float length, float angle, float strokeWidth, boolean isHour) {
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(isHour ? Color.BLACK : Color.GRAY);

        // 角度调整，将角度转换为弧度并校正角度使之从12点钟方向开始
        float radian = (float) Math.toRadians(angle - 90);

        float endX = (float) (cx + Math.cos(radian) * length);
        float endY = (float) (cy + Math.sin(radian) * length);

        // 绘制指针，确保从中心点开始
        canvas.drawLine(cx, cy, endX, endY, paint);
    }

    private void drawHand(Canvas canvas, int cx, int cy, float length, float angle, float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        float endX = (float) (cx + Math.cos(Math.toRadians(angle)) * length);
        float endY = (float) (cy + Math.sin(Math.toRadians(angle)) * length);
        canvas.drawLine(cx, cy, endX, endY, paint);
    }
}
