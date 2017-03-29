/*
 * Copyright (C) 2011 The Android Open Source Project
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
package info.leiguo.healthmonitoring.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import info.leiguo.healthmonitoring.data.PointData;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private static final List<List<PointData>> mEating = new ArrayList<>();
    private static final List<List<PointData>> mWalking = new ArrayList<>();
    private static final List<List<PointData>> mRunning = new ArrayList<>();
    private static final float[] COLOR_RED = {1.0f, 0f, 0f, 1f};
    private static final float[] COLOR_BLUE = {0f, 0f, 1f, 1f};
    private static final float[] COLOR_GREEN = {0f, 1f, 0f, 1f};
    private static double maxValue;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setData(List<List<PointData>> eating, List<List<PointData>> walking,
                        List<List<PointData>> running){

        if(eating != null && eating.size() > 0){
            mEating.clear();
            mEating.addAll(eating);
        }
        if(walking != null && walking.size() > 0){
            mWalking.clear();
            mWalking.addAll(walking);
        }
        if(running != null && running.size() > 0){
            mRunning.clear();
            mRunning.addAll(running);
        }

    }

    public static List<Line> createLines(){
//        Log.e("MyGLSurfaceView", "Thraed id: " + android.os.Process.myTid());
        maxValue = findMaxInAction(mEating);
        maxValue = Math.max(maxValue, findMaxInAction(mWalking));
        maxValue = Math.max(maxValue, findMaxInAction(mRunning));
        List<Line> lines = createLines(mRunning, COLOR_BLUE);
        lines.addAll(createLines(mWalking, COLOR_GREEN));
        lines.addAll(createLines(mEating, COLOR_RED));
        return lines;
    }

    private static List<Line> createLines(List<List<PointData>> lists, float[] color) {
        List<Line> tmpLines = new ArrayList<>();
        if (lists == null || lists.size() == 0 || color == null || color.length != 4) {
            return tmpLines;
        }
       for (List<PointData> points : lists) {
            if (points != null && points.size() > 1) {
                float[] array = convertToArray(points);
//                float max = findMax(array);
                float max = (float)maxValue;
                array = getScaleValue(array, max);
                Line line = new Line();
                line.SetVerts(array);
                line.SetColor(color[0], color[1], color[2], color[3]);
                tmpLines.add(line);
            }
        }
        return tmpLines;
    }

    private static float[] getScaleValue(float[] array, float max){
        if(array != null && array.length > 0){
            for(int i = 0; i < array.length; i++){
                array[i] = getScaleValue(array[i], max);
            }
        }
        return array;
    }

    private static float[] convertToArray(List<PointData> points){
        if(points != null && points.size() > 0){
            float[] result = new float[points.size() * 3];
            int index = 0;
            for (PointData point : points){
                result[index++] = (float) point.x;
                result[index++] = (float) point.y;
                result[index++] = (float) point.z;
            }
            return result;
        }
        return new float[0];
    }

    private static float findMax(float[] array){
        if(array != null && array.length >= 2){
            float max = array[0];
            float min = array[0];
            for(float value : array){
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
            max = Math.max(max, Math.abs(min));
            if(max != 0f){
                return max;
            }
        }
        return 1.0f;
    }

    private static float getScaleValue(double value, double max){
        if(max == 0.0){
            max = 1.0;
        }
        float result = (float)(value / max);
        if(result > 1.0f){
            result = 1.0f;
        }
        return result;
    }

    private static double findMaxInAction(List<List<PointData>> lists){
        if(lists != null && lists.size() > 0){
            int size = lists.size();
            List<PointData> points = lists.get(0);
            double max = findMax(points);
            for (int i = 1; i < size; i++){
                points = lists.get(i);
                max = Math.max(max, findMax(points));
            }
            return max;
        }
        return 1.0;
    }

    private static double findMax(List<PointData> points){
        if (points != null && points.size() > 1) {
            PointData point = points.get(0);
            double max = point.x;
            double min = point.x;
            for (PointData data : points) {
                // find max and min
                max = Math.max(Math.max(Math.max(data.x, data.y), data.z), max);
                min = Math.min(Math.min(Math.min(data.x, data.y), data.z), min);
            }
            max = Math.max(max, Math.abs(min));
            if (max != 0.0) {
                return max;
            }
        }
        return 1.0;
    }


}
