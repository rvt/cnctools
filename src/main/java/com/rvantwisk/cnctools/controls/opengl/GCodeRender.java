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

package com.rvantwisk.cnctools.controls.opengl;

import com.rvantwisk.cnctools.gcodeparser.AbstractMachineValidator;
import com.rvantwisk.cnctools.gcodeparser.GCodeParser;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.gcodeparser.exceptions.UnsupportedSimException;
import com.rvantwisk.cnctools.opengl.AbstractOpenGLRenderer;
import com.rvantwisk.cnctools.opengl.Camera;
import com.rvantwisk.cnctools.opengl.VBOHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.stream.StreamHandler;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

/**
 * Class to render GCode
 */
final public class GCodeRender extends AbstractOpenGLRenderer {

    private VBOHelper vboHelper = new VBOHelper();

    private Camera camera = new Camera();
    private static final int FLOAT = Float.SIZE / Byte.SIZE;


    private final LinkedList<Message> messageQueue = new LinkedList<>();

    public GCodeRender(StreamHandler readHandler) {
        super(readHandler);
    }


    protected void init() {
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
        GLU.gluPerspective(camera.getFovy(), 1.0f, 0.0001f, 1000000.0f);


        // CLear
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        GLU.gluLookAt(
                (float) camera.getEye().getX(), (float) camera.getEye().getY(), (float) camera.getEye().getZ(),
                (float) camera.getCenter().getX(), (float) camera.getCenter().getY(), (float) camera.getCenter().getZ(),
                (float) camera.getUp().getX(), (float) camera.getUp().getY(), (float) camera.getUp().getZ()
        );
    }

    protected void loop() {
        pumpQueue();

        Camera localCamera = getCamera();

        // Call this only when aspect ratio of screen changes
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float aspectRatio = (float) getRenderStream().getHandler().getWidth() / (float) getRenderStream().getHandler().getHeight();
        GLU.gluPerspective(localCamera.getFovy(), aspectRatio, 0.0001f, 1000000.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        GLU.gluLookAt(
                (float) localCamera.getEye().getX(), (float) localCamera.getEye().getY(), (float) localCamera.getEye().getZ(),
                (float) localCamera.getCenter().getX(), (float) localCamera.getCenter().getY(), (float) localCamera.getCenter().getZ(),
                (float) localCamera.getUp().getX(), (float) localCamera.getUp().getY(), (float) localCamera.getUp().getZ()
        );


        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (VBOHelper.VBOInfo vboInfo : vboHelper.getVboList()) {
            if (vboInfo.draw) {

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboInfo.vbID);

                GL11.glPushMatrix();
                if (vboInfo.hasOwnColor) {
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 7 * FLOAT, 0);
                    GL11.glColorPointer(3, GL11.GL_FLOAT, 7 * FLOAT, 3 * FLOAT);
                } else {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 3 * FLOAT, 0);
                    GL11.glColor4f(vboInfo.colorR, vboInfo.colorG, vboInfo.colorB, vboInfo.colorA);
                }

                GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, vboInfo.vbRows);
                GL11.glPopMatrix();
            }
        }
    }

    protected void destroygl() {
        vboHelper.destroy();
    }

    public void load(final OpenGLMachineController machine, final AbstractMachineValidator machineValidator, final InputStream file) throws SimException, UnsupportedSimException {
        GCodeParser parser = new GCodeParser(machine, machineValidator, file);
        VBOHelper.VBOInfo vboInfo = VBOHelper.createVBO(machine.getVBOData().toArray(), machine.getNumWords(), true);
        getMessageQueue().add(new Message("CLEARVBO", null));
        getMessageQueue().add(new Message("NEWVBO", vboInfo));
    }


    synchronized public Camera getCamera() {
        return camera;
    }

    synchronized public void setCamera(Camera camera) {
        this.camera = camera;
    }


    private void pumpQueue() {
        while (messageQueue.size() > 0) {
            final Message m = messageQueue.removeFirst();

            switch (m.getName()) {
                case "NEWVBO":
                    final VBOHelper.VBOInfo vbo = (VBOHelper.VBOInfo) m.getData();

                    IntBuffer buffer = BufferUtils.createIntBuffer(1);
                    GL15.glGenBuffers(buffer);
                    vbo.vbID = buffer.get(0);

                    FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vbo.data.length);
                    vertex_buffer_data.put(vbo.data);
                    vertex_buffer_data.rewind();

                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.vbID);
                    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);
                    vbo.data = null;
                    vboHelper.add(vbo);
                    break;
                case "CLEARVBO":
                    vboHelper.removeAll();
                    break;
            }
        }
    }

    void newVBOData(final float[] data) {
        synchronized (messageQueue) {
            messageQueue.add(new Message("NEWVBO", data));
        }
    }

    void clearVBO() {
        synchronized (messageQueue) {
            messageQueue.add(new Message("CLEARVBO", null));
        }
    }

    public LinkedList<Message> getMessageQueue() {
        return messageQueue;
    }

    public static class Message {
        private final String name;
        private final Object data;

        public Message(String name, Object data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public Object getData() {
            return data;
        }
    }
}