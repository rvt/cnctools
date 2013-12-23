/*
 * Copyright (c) 2013, R. van Twisk
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.rvantwisk.cnctools;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;


// http://www.khronos.org/files/opengl-quick-reference-card.pdf
public class SimpleInterleavedVboExample {
    private static final int FLOAT = Float.SIZE / Byte.SIZE;

    public void start() {
        // create our display window
        try {
            Display.setTitle("Simple Interleaved Vbo Example");
            Display.setDisplayMode(new DisplayMode(600, 600));
            Display.create();
        } catch (Exception e) {
            System.err.println(e.toString());

            System.exit(1);
        }

        // set up OpenGL
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);



        // set up lighting
        //GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_LIGHT0);

        //GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        //GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 25.0f);

        //GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, floatBuffer(-5.0f, 5.0f, 15.0f, 0.0f));

        //GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        //GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));

        //GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, floatBuffer(0.1f, 0.1f, 0.1f, 1.0f));

        // set up the camera
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, 1.0f, 0.1f, 100.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        GLU.gluLookAt(
                0.0f,
                0.0f,
                5.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f,
                0.0f
        );

        // create our vertex buffer objects
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer);

        IntBuffer buffer2 = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer2);

        int vertex_buffer_id = buffer.get(0);
        int vertex_buffer_id2 = buffer2.get(0);

        float[] vertex_data_array = {
                //   x      y      z      nx     ny     nz     r      g      b      a
                // back quad
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f
        };

        float[] vertex_data_array2 = {
                //   x      y      z
                // back quad
                0.0f, 0.0f, 0.0f,
                1.5f, 0.0f, 0.0f,
                1.5f, 1.5f, 0.0f,
                0.0f, 1.5f, 0.0f,
                0.0f, 0.0f, 0.0f
        };

        FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vertex_data_array.length);
        vertex_buffer_data.put(vertex_data_array);
        vertex_buffer_data.rewind();

        FloatBuffer vertex_buffer_data2 = BufferUtils.createFloatBuffer(vertex_data_array2.length);
        vertex_buffer_data2.put(vertex_data_array2);
        vertex_buffer_data2.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id2);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data2, GL15.GL_STATIC_DRAW);


        // set up frame rate counter stuff
        int framerate_count = 0;
        long framerate_timestamp = new Date().getTime();
        double rotate_x, rotate_y, rotate_z;

        while (!Display.isCloseRequested()) {
            // increment frame rate counter, and display current frame rate
            // if it is time to do so
            framerate_count++;

            Date d = new Date();
            long this_framerate_timestamp = d.getTime();

            if ((this_framerate_timestamp - framerate_timestamp) >= 1000) {
                System.err.println("Frame Rate: " + framerate_count);

                framerate_count = 0;
                framerate_timestamp = this_framerate_timestamp;
            }

            // clear the display
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // perform rotation transformations
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id);
            //GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);
            GL11.glPushMatrix();

            rotate_x = ((double) this_framerate_timestamp / 300.0) % 360.0;
            rotate_y = ((double) this_framerate_timestamp / 200.0) % 360.0;
            rotate_z = ((double) this_framerate_timestamp / 100.0) % 360.0;

            GL11.glRotated(
                    rotate_x,
                    1.0,
                    0.0,
                    0.0
            );

            GL11.glRotated(
                    rotate_y,
                    0.0,
                    1.0,
                    0.0
            );

            GL11.glRotated(
                    rotate_z,
                    0.0,
                    0.0,
                    1.0
            );

            // render the cube

            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 7 * FLOAT, 0);
            //GL11.glNormalPointer(GL11.GL_FLOAT, 40, 12);
            GL11.glColorPointer(4, GL11.GL_FLOAT, 7 * FLOAT, 3 * FLOAT);
            GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, vertex_data_array.length / 7); //7 floats per... line

            // restore the matrix to pre-transformation values
            GL11.glPopMatrix();

            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id2);
//            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data2, GL15.GL_STATIC_DRAW);
            GL11.glPushMatrix();
            GL11.glRotated(
                    rotate_z,
                    0.5,
                    0.5,
                    0.5
            );
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 3 * FLOAT, 0);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);//Change the object color to red
            GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, vertex_data_array2.length / 3); //7 floats per... line
            GL11.glPopMatrix();
            // update the display
            Display.update();

            Display.sync(15);
        }

        // clean things up
        Display.destroy();
    }

    public FloatBuffer floatBuffer(float a, float b, float c, float d) {
        float[] data = new float[]{a, b, c, d};
        FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
        fb.put(data);
        fb.flip();
        return fb;
    }

    public static void main(String[] args) {
        SimpleInterleavedVboExample example = new SimpleInterleavedVboExample();
        example.start();
    }
}