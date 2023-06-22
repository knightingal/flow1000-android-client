/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanjing.knightingal.processerlib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author Knightingal
 * @since v1.0
 */

public class ProcessBar extends View{
    private static final String TAG = "ProcessBar";
    public ProcessBar(Context context) {
        super(context);
        paint = new Paint();
    }

    public ProcessBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public ProcessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    private int percent = 0;
    public void setPercent(int percent) {
        this.percent = percent;
    }
    public int getPercent() {
        return percent;
    }

    private Paint paint;

    int length = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GRAY);
        canvas.drawRect(0, 0, this.width, 10, paint);

        paint.setColor(Color.GREEN);
        this.length = this.width * this.percent / 100;
//        Log.d(TAG, "draw length = " + this.length);
        canvas.drawRect(0, 0, this.length, 10, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }
    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 10;
        }

        return result;
    }

    private int width = 0;
    private int stepCount = 0;
    private int currCount = 0;
    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 10;
        }
        this.width = result;
        return result;
    }
}
