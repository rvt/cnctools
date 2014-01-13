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

import com.rvantwisk.cnctools.opengl.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.stream.StreamHandler;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class to render GCode
 */
final public class OpenGLRenderer extends AbstractOpenGLRenderer {

    private final View2D viewAxis = new View2D();
    private final View3D viewModel = new View3D();
    private final List<AbstractActor> actors = new ArrayList<>();
    private final Map<String, AbstractActor> activeActors = new TreeMap<>();
    Font awtFont;
    TrueTypeFont font;
    private ReadonlyCamera camera = new ReadonlyCamera();

    public OpenGLRenderer(StreamHandler readHandler) {
        super(readHandler);
    }

    protected void init() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1.0);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //   Font awtFont = new Font("Times New Roman", Font.ITALIC, 24);
        //   font = new TrueTypeFont(awtFont, false);
    }

    protected void loop() {
        final ReadonlyCamera localCam;
        synchronized (this) {
            localCam = this.camera;
            if (actors.size() > 0) {
                for (AbstractActor actor : actors) {
                    // CHeck if a existing axctor already exists, if so destroy it
                    if (activeActors.containsKey(actor.getName())) {
                        final AbstractActor existingActor = activeActors.get(actor.getName());
                        existingActor.destroy();
                    }
                    // Add a new actor
                    actor.initialize();
                    activeActors.put(actor.getName(), actor);
                }
                actors.clear();
            }
        }

        viewAxis.setCamera(localCam);
        viewModel.setCamera(localCam);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        // Draw axis in lower left corner
        viewAxis.begin();
        drawAxis(25f);
        viewAxis.end();

        // Prepare actors for next drawing sequence
        for (final AbstractActor actor : activeActors.values()) {
            actor.prepare();
        }

        viewModel.begin();
        viewModel.display_transform();

        // Draw the actor
        for (final AbstractActor actor : activeActors.values()) {
            actor.draw();
        }

        viewModel.end();
    }

    protected void drawAxis(float length) {
        GL11.glPushMatrix();
        this.viewModel.ui_transform(length + length / 2.0f);

        float[][] axis = {{length, 0.0f, 0.0f}, {0.0f, -length, 0.0f}, {0.0f, 0.0f, length}};
        float[][] colors = {{1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.5f, 1.0f}};
        String[] names = {"X", "Y", "Z"};

        GL11.glBegin(GL11.GL_LINES);
        for (int i = 0; i < 3; i++) {
            GL11.glColor3f(colors[i][0], colors[i][1], colors[i][2]);
            GL11.glVertex3f(0.0f, 0.0f, 0.0f);
            GL11.glVertex3f(axis[i][0], axis[i][1], axis[i][2]);
        }
        GL11.glEnd();

        // TODO optmize text rendering, lack of glutBitmapCharacter forces me to do it differently.
        for (int i = 0; i < 3; i++) {
            GL11.glPushMatrix();
            GL11.glColor3f(colors[i][0], colors[i][1], colors[i][2]);
            GL11.glTranslatef(Math.round(axis[i][0]), Math.round(axis[i][1]), Math.round(axis[i][2]));
            GL11.glRotatef(camera.getElevation() + 90f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-camera.getAzimuth(), 0.0f, 1.0f, 0.0f);
            SimpleText.drawString(names[i], 0.0f, 0.0f, 0.0f);
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    protected void destroygl() {
        for (AbstractActor actor : activeActors.values()) {
            actor.destroy();
        }
        for (AbstractActor actor : actors) {
            actor.destroy();
        }
        activeActors.clear();
        actors.clear();
    }

    public void addActor(final AbstractActor actor) {
        synchronized (this) {
            actors.add(actor);
        }
    }

    synchronized public ReadonlyCamera getCamera() {
        return camera;
    }

    synchronized public void setCamera(ReadonlyCamera camera) {
        this.camera = camera;
    }

}


