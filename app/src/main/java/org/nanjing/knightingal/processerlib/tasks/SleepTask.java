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

package org.nanjing.knightingal.processerlib.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.nanjing.knightingal.processerlib.TaskNotifier;

/**
 * @author Knightingal
 * @since v1.0
 */

public class SleepTask extends AbsTask<Integer, Void, Integer> {


    private static final String TAG = "SleepTask";
    private final TaskNotifier taskNotifier;

    public SleepTask(TaskNotifier taskNotifier) {
        this.type = "SubActivity";
        this.taskNotifier = taskNotifier;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        for (int i = 0; i < 300; i++) {
            taskNotifier.onTaskComplete(this, params[0]);
            Log.d(TAG, "start sleep index " + params[0]);
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "end sleep index " + params[0]);
        }
        return null;
    }

    @Override
    public int getTaskSize(int index) {
        return 300;
    }
}
