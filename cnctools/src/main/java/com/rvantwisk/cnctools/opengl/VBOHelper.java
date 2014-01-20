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

package com.rvantwisk.cnctools.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by rvt on 12/10/13.
 */
public abstract class VBOHelper {
    protected static final int SIZE_FLOAT = Float.SIZE / Byte.SIZE;
    protected int vbID = -1;
    protected int vbRows = 0;
    protected float colorR = 1.0f;
    protected float colorG = 1.0f;
    protected float colorB = 1.0f;
    protected float colorA = 1.0f;
    protected boolean hasOwnColor = false;
    protected float[] data;

    /**
     * Cretae a VBO array with
     *
     * @param data Array of float
     * @param i    Number of rows
     * @param b    set if it has it's own color
     * @return
     */
    public static VBOHelper createLines(final float[] data, int i, boolean b) {
        VBOHelper vbo = new GLLines();
        vbo.vbRows = i;
        vbo.hasOwnColor = b;
        vbo.data = data;

        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer);
        vbo.vbID = buffer.get(0);

        FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vbo.data.length);
        vertex_buffer_data.put(vbo.data);
        vertex_buffer_data.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.vbID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);
        vbo.data = null;

        return vbo;
    }

    public static VBOHelper createTriangles(final float[] data, int i, boolean b) {
        VBOHelper vbo = new GLTriangles();
        vbo.vbRows = i;
        vbo.hasOwnColor = b;
        vbo.data = data;

        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer);
        vbo.vbID = buffer.get(0);

        FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vbo.data.length);
        vertex_buffer_data.put(vbo.data);
        vertex_buffer_data.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.vbID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);
        vbo.data = null;

        return vbo;
    }

    public void destroy() {
        GL15.glDeleteBuffers(vbID);
    }

    abstract public void draw();


    // LineStrip VBO
    public static class GLLines extends VBOHelper {

        @Override
        public void draw() {
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbID);
            if (hasOwnColor) {
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 7 * SIZE_FLOAT, 0);
                GL11.glColorPointer(3, GL11.GL_FLOAT, 7 * SIZE_FLOAT, 3 * SIZE_FLOAT);
                GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, vbRows);
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            } else {
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 3 * SIZE_FLOAT, 0);
                GL11.glColor4f(colorR, colorG, colorB, colorA);
                GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, vbRows);
            }
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        }
    }

    // LineStrip VBO
    public static class GLTriangles extends VBOHelper {

        @Override
        public void draw() {
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbID);
            if (hasOwnColor) {
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 7 * SIZE_FLOAT, 0);
                GL11.glColorPointer(3, GL11.GL_FLOAT, 7 * SIZE_FLOAT, 3 * SIZE_FLOAT);
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vbRows);
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            } else {
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 3 * SIZE_FLOAT, 0);
                GL11.glColor4f(colorR, colorG, colorB, colorA);
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vbRows);
            }
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        }
    }

    public float getColorR() {
        return colorR;
    }

    public void setColorR(float colorR) {
        this.colorR = colorR;
    }

    public float getColorG() {
        return colorG;
    }

    public void setColorG(float colorG) {
        this.colorG = colorG;
    }

    public float getColorB() {
        return colorB;
    }

    public void setColorB(float colorB) {
        this.colorB = colorB;
    }

    public float getColorA() {
        return colorA;
    }

    public void setColorA(float colorA) {
        this.colorA = colorA;
    }
}
