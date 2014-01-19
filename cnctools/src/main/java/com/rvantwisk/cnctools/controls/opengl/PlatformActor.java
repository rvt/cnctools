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

package com.rvantwisk.cnctools.controls.opengl;

import com.rvantwisk.cnctools.opengl.AbstractActor;
import org.lwjgl.opengl.GL11;

/**
 * generates a platform at Z=0.0 to show where Z=0.0 is located
 * Created by rvt on 1/12/14.
 */
public class PlatformActor extends AbstractActor {
    public static final float color_grads_minor[] = {0xaf / 255.0f, 0xdf / 255.0f, 0x5f / 255.0f, .1f};
    public static final float color_grads_interm[] = {0xaf / 255.0f, 0xdf / 255.0f, 0x5f / 255.0f, .2f};
    public static final float color_grads_major[] = {0xaf / 255.0f, 0xdf / 255.0f, 0x5f / 255.0f, .33f};
    public static final float color_fill[] = {0xaf / 255.0f, 0xdf / 255.0f, 0x5f / 255.0f, 1.0f};
    private static final float ZPOS = 0.0f;
    private final int xneg;
    private final int yneg;
    private final int xpos;
    private final int ypos;
    private final int seps;
    private final int openNess = 5;
    private int display_list;

    public PlatformActor(final float xneg, final float yneg, final float xpos, final float ypos, final boolean metric) {
        super(PlatformActor.class.getSimpleName());
        this.xneg = (int) Math.floor(xneg / (openNess*openNess)) * openNess*openNess;
        this.yneg = (int) Math.floor(yneg / (openNess*openNess)) * openNess*openNess;
        this.xpos = (int) Math.ceil(xpos / (openNess*openNess)) * openNess*openNess;
        this.ypos = (int) Math.ceil(ypos / (openNess*openNess)) * openNess*openNess;

        seps = (metric ? 10 * openNess : 8 * openNess);

    }

    @Override
    public void initialize() {

        display_list = GL11.glGenLists(1);

        GL11.glNewList(display_list, GL11.GL_COMPILE);

        // draw the grid
        GL11.glBegin(GL11.GL_LINES);

        for (int i = xneg; i <= xpos; i += openNess) {
            setColor(i);
            GL11.glVertex3f(i, yneg, ZPOS);
            GL11.glVertex3f(i, ypos, ZPOS);
        }

        for (int i = yneg; i <= ypos; i += openNess) {
            setColor(i);
            GL11.glVertex3f(xneg, i, ZPOS);
            GL11.glVertex3f(xpos, i, ZPOS);
        }

        GL11.glColor4f(color_fill[0], color_fill[1], color_fill[2], color_fill[3]);
        GL11.glRectf(xneg, yneg, xpos, ypos);
        GL11.glEnd();

        GL11.glEndList();
    }

    @Override
    public void prepare() {
    }

    /**
     * Decide what color to use
     *
     * @param i
     */
    private void setColor(int i) {
        if (i % seps == 0) {
            GL11.glColor4f(color_grads_major[0], color_grads_major[1], color_grads_major[2], color_grads_major[3]);
        } else if (i % (seps / 2) == 0) {
            GL11.glColor4f(color_grads_interm[0], color_grads_interm[1], color_grads_interm[2], color_grads_interm[3]);
        } else {
            GL11.glColor4f(color_grads_minor[0], color_grads_minor[1], color_grads_minor[2], color_grads_minor[3]);
        }
    }

    @Override
    public void draw() {
        GL11.glCallList(display_list);
    }

    @Override
    public void destroy() {
        GL11.glDeleteLists(display_list, 1);
    }
}
