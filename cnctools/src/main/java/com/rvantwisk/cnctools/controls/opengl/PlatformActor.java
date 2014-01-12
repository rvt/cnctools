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
    private final float width;
    private final float depth;

    private final float seps = 10.0f;

    public static final float color_grads_minor[] = {0xaf/255.0f, 0xdf / 255.0f, 0x5f / 255.0f, 0.1f}; // default ambient color
    public static final float color_grads_interm[] = {0xaf/255.0f, 0xdf / 255.0f, 0x5f / 255.0f, 0.2f}; // default ambient color
    public static final float color_grads_major[] = {0xaf/255.0f, 0xdf / 255.0f, 0x5f / 255.0f, 0.33f}; // default ambient color
    public static final float color_fill[] = {0xaf/255.0f, 0xdf / 255.0f, 0x5f / 255.0f, 0.5f}; // default ambient color

    public PlatformActor(final float width, final float depth) {
        super(PlatformActor.class.getSimpleName());
        this.width = width;
        this.depth = depth;

    }

    @Override
    public void initialize() {
    }

    @Override
    public void prepare() {

    }

    private void setColor(float i) {
        if (i%seps == 0.0f) {
            GL11.glColor4f(color_grads_major[0],color_grads_major[1],color_grads_major[2], color_grads_major[3]);
        } else if (i%(seps/2.0f) == 0.0f) {
            GL11.glColor4f(color_grads_interm[0],color_grads_interm[1],color_grads_interm[2], color_grads_interm[3]);
        } else {
            GL11.glColor4f(color_grads_minor[0],color_grads_minor[1],color_grads_minor[2], color_grads_minor[3]);
        }
    }

    @Override
    public void draw() {

        // draw the grid
        GL11.glBegin(GL11.GL_LINES);

        for (float i=-width;i<width;i++) {
            setColor(i);
            GL11.glVertex3f(i, -depth, 0.0f);
            GL11.glVertex3f(i, depth, 0.0f);
        }

        for (float i=-depth;i<depth;i++) {
            setColor(i);
            GL11.glVertex3f(-width, i, 0.0f);
            GL11.glVertex3f(width, i, 0.0f);
        }

        GL11.glColor4f(color_fill[0],color_fill[1],color_fill[2], color_fill[3]);
        GL11.glRectf(-width, -depth, width, depth);
        GL11.glEnd();
    }

    @Override
    public void destroy() {

    }
}
