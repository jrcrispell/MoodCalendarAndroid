//package com.wordpress.jrcrispell.moodcalendar;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLUtils;
//import android.util.AttributeSet;
//
//import java.nio.FloatBuffer;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//
//public class DayCalendarSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
//
//    int texture[] = new int[1];
//
//    public DayCalendarSurfaceView(Context context) {
//        super(context);
//
//        // Create an OpenGL ES 2.0 context
//        setEGLContextClientVersion(2);
//
//        // Set the Renderer for drawing on the GLSurfaceView
//        setRenderer(this);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        setBackgroundResource(R.drawable.cosmic);
//        texture[0] = R.drawable.hole;
//    }
//
//    // Renderer
//    @Override
//    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        gl10.glGenTextures(1, texture, 0);
//        gl10.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
//        float texCoords[] = {
//                0.0f, 0.0f,
//                1.0f, 0.0f
//        };
//        FloatBuffer texcoords = FloatBuffer.wrap(texCoords);
//
//
//        // None of this makes any sense to me.
//
//        //gl10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);
//
//        Bitmap hole = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.hole);
//
//        // Set texture image
////        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, hole, 0);
//
//        //Enable texture related flags (Important)
////        gl10.glEnable(GL10.GL_TEXTURE_2D);
////        gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl10, int width, int height) {
//        GLES20.glViewport(0, 0, width, height);
//
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl10) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//
//    }
//}
