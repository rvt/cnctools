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
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.nio.FloatBuffer;

/**
 * Created by rvt on 1/12/14.
 */
public class BeadActor extends AbstractActor {
    public static final float colorDefaultDiffuse[] = {0.55f, 0.55f, 0.55f, 1.0f}; // default diffuse color
    public static final float colorDefaultSpecular[] = {0.7f, 0.7f, 0.7f, 1.0f}; // default ambient color
    public static final float colorDefaultLight[] = {0.3f, 0.3f, 0.3f, 1.0f}; // default ambient color
    public static final float lightDefaultPos0[] = {20.0f, 20.0f, 20.0f, 0.0f}; // default ambient color
    public static final float lightDefaultPos1[] = {-20.0f, -20.0f, 20.0f, 0.0f}; // default ambient color

    // Color's
    private FloatBuffer ambient;
    private FloatBuffer diffuse;
    private FloatBuffer specular;
    private FloatBuffer shininess;
    private FloatBuffer light;
    private FloatBuffer lightPos0;
    private FloatBuffer lightPos1;

    public BeadActor() {
        super(BeadActor.class.getSimpleName());
    }

    @Override
    public void initialize() {
        ambient = allocFloats(colorDefaultDiffuse);
        diffuse = allocFloats(colorDefaultDiffuse);
        specular = allocFloats(colorDefaultSpecular);
        shininess = allocFloats(new float[]{32.0f, 0.0f, 0.0f, 0.0f});

        light = allocFloats(colorDefaultLight);
        lightPos0 = allocFloats(lightDefaultPos0);
        lightPos1 = allocFloats(lightDefaultPos1);
    }

    @Override
    public void prepare() {

    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, ambient);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, diffuse);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, specular);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);

        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, light);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, light);
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, light);

        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPos0);
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPos1);

        GL11.glColor3f(1.0f, 0.0f, 0.0f);

        Sphere s = new Sphere();
        s.setDrawStyle(GLU.GLU_FILL);
        s.setNormals(GLU.GLU_SMOOTH);
        s.draw(3.8f, 100, 100);

        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    @Override
    public void destroy() {

    }
}
