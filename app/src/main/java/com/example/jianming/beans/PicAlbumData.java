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

package com.example.jianming.beans;

import org.nanjing.knightingal.processerlib.beans.CounterBean;

/**
 * @author Knightingal
 * @since v1.0
 */

public class PicAlbumData {
    private CounterBean processData;

    public PicAlbumBean getPicAlbumData() {
        return picAlbumData;
    }

    public void setPicAlbumData(PicAlbumBean picAlbumData) {
        this.picAlbumData = picAlbumData;
    }

    private PicAlbumBean picAlbumData;

    public CounterBean getProcessData() {
        return processData;
    }

    public void setProcessData(CounterBean processData) {
        this.processData = processData;
    }
}
