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

package org.nanjing.knightingal.processerlib.beans;

/**
 * @author Knightingal
 * @since v1.0
 */

public class Counter {
    private int min;

    private int max;

    private int step;

    private int index;

    private int curr;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCurr() {
        return curr;
    }

    public void setCurr(int curr) {
        this.curr = curr;
    }

    public Counter(int min, int max, int step, int index) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.index = index;
        this.curr = min;
    }

    public void inc() {
        curr += step;
        if (curr > max) {
            curr = min + curr - max;
        }
    }
}
