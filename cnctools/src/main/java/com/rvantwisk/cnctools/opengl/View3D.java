/*
 * Copyright (c) 2014, R. van Twisk
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

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * Created by rvt on 1/10/14.
 */
public class View3D extends AbstractView {
    protected float NEAR=0.01f;
    protected float FAR=1000000.0f;

    @Override
    public void begin() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        if (camera.isOrtho()) {
            GL11.glOrtho(-camera.getX(), camera.getX(), -camera.getY(), camera.getY(), -NEAR, FAR);
        } else {
            GLU.gluPerspective(camera.getFOVY(), camera.getWidth() / camera.getHeight(), NEAR, FAR);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }

    @Override
    public void end() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    @Override
    public void display_transform() {
        GL11.glRotatef(-90f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, camera.getY(), 0.0f);

        float f = camera.getZoom_factor();
        if (camera.isOrtho()) {
            f *= camera.getZOOM_ORTHO_ADJ();
        }
        GL11.glScalef(f, f, f);

        GL11.glTranslatef(camera.getX(), 0.0f, camera.getZ());
        GL11.glRotatef(-camera.getElevation(), 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(camera.getAzimuth(), 0.0f, 0.0f, 1.0f);

        GL11.glTranslatef(camera.getOffset_x(), camera.getOffset_y(), 0.0f);
    }


}
