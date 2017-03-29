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
        List<Line> lines = createLines(mEating, COLOR_RED);
        lines.addAll(createLines(mWalking, COLOR_GREEN));
        lines.addAll(createLines(mRunning, COLOR_BLUE));
        return lines;
    }

    public static List<Line> createLines(List<List<PointData>> lists, float[] color) {
        List<Line> tmpLines = new ArrayList<>();
        if (lists == null || lists.size() == 0 || color == null || color.length != 4) {
            return tmpLines;
        }
       for (List<PointData> points : lists) {
            if (points != null && points.size() > 1) {
                double max = findMax(points);
                // create lines
                PointData prePoint = null;
                for (PointData point : points) {
                    if (prePoint != null && point != null) {
                        Line line = new Line();
                        line.SetVerts(getScaleValue(prePoint.x, max), getScaleValue(prePoint.y, max),
                                getScaleValue(prePoint.z, max), getScaleValue(point.x, max),
                                getScaleValue(point.y, max), getScaleValue(point.z, max));
                        line.SetColor(color[0], color[1], color[2], color[3]);
                        tmpLines.add(line);
                    }
                    prePoint = point;
                }
            }
        }
        return tmpLines;
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
