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
import com.rvantwisk.cnctools.opengl.VBOHelper;
import com.rvantwisk.cnctools.operations.math.Point;
import com.rvantwisk.gcodeparser.*;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.gcodeparser.gcodes.MotionMode;
import gnu.trove.list.array.TFloatArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rvt on 1/19/14.
 */
public class ArrowsActor extends AbstractActor implements MachineController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static int ROWSIZE = 7;
    final TFloatArrayList data = new TFloatArrayList();
    final MachineStatusHelper machine = new MachineStatusHelper();
    // USed during rendering
    VBOHelper vboInfo = null;
    private double lastX = 0;
    private double lastY = 0;
    private double lastZ = 0;
    private double lastA = 0;
    private double[][] arrow = {
            {0, 0, 0},
            {0.4 * 4.0f, -0.1 * 4.0f, 0.0},
            {0.4 * 4.0f, 0.1 * 4.0f, 0.0}
    };

    /**
     * @param name Nam eof teh actor, so it can be found and be replaced/delete when needed
     */
    public ArrowsActor(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        vboInfo = VBOHelper.createTriangles(data.toArray(), data.size() / ROWSIZE, true);
        data.clear();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        vboInfo.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Override
    public void destroy() {
        vboInfo.destroy();
    }

    @Override
    public void startBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> block) {
        machine.setMachineStatus(machineStatus);
    }

    @Override
    public void endBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> block) throws SimException {

        switch (machine.getMotionMode()) {
            case G0:
            case G1:
                double rX = machine.getX();
                double rY = machine.getY();
                double rZ = machine.getZ();
                double rA = machine.getA();

                if (rX - lastX != 0.0 || rY - lastY != 0.0 || rZ - lastZ != 0.0) {

                    addArrow(arrow, new double[]{rX, rY, rZ}, rA, new Vector3D(lastX, lastY, lastZ), new Vector3D(rX, rY, rZ));
                }
                break;
            case G2:
            case G3:
                //   drawArc(parser, machineStatus, currentBlock);
                break;
        }

        lastX = machine.getX();
        lastY = machine.getY();
        lastZ = machine.getZ();
        lastA = machine.getA();
    }

    @Override
    public void end(GCodeParser parser, MachineStatus machineStatus) throws SimException {

    }

    private void addArrow(double[][] arrow, double[] loc, double rA, final Vector3D p1, final Vector3D p2) {

        try {
            double angleZ = Point.angleBetween2Lines(new Point(p1.getX(), p1.getY()), new Point(p2.getX(), p2.getY()), new Point(0.0, 0.0), new Point(0.0, 1.0));
//        double angleX = Point.angleBetween2Lines(new Point(lastY, lastZ), new Point(rY, rZ), new Point(0.0, 0.0), new Point(0.0, 1.0));


            double dx = p1.getX() - p2.getX();
            double dy = p1.getY() - p2.getY();
            double d = Math.sqrt(dx * dx + dy * dy);

            double angle;
            Rotation myRotation;
            if (d != 0.0) {
                angle = Point.angleBetween2Lines(new Point(0.0, 0.0), new Point(1.0, 0.0), new Point(0.0, 0.0), new Point(d, p1.getZ() - p2.getZ()));
                myRotation = new Rotation(new Vector3D(1, 0, 0.0), angle + (0.0 / 360.0 * Math.PI * 2.0));
            } else if ((p1.getZ() - p2.getZ()) < 0.0) {
                angle = (90.0 / 360.0 * Math.PI * 2.0);
                myRotation = new Rotation(new Vector3D(0, 1, 0.0), angle + (0.0 / 360.0 * Math.PI * 2.0));
            } else {
                angle = (-90.0 / 360.0 * Math.PI * 2.0);
                myRotation = new Rotation(new Vector3D(0, 1, 0.0), angle + (0.0 / 360.0 * Math.PI * 2.0));
            }
            Rotation myRotationZ = new Rotation(new Vector3D(0, 0, 1.0), angleZ + (-90.0 / 360.0 * Math.PI * 2.0));

            Rotation myRotationA = new Rotation(new Vector3D(1.0, 0.0, 0.0), (rA / 360.0 * Math.PI * 2.0));

            double[] out = new double[3];
            double[] out2 = new double[3];
            for (double[] v : arrow) {
                myRotationZ.applyTo(v, out);
                myRotation.applyTo(out, out2);

                out2[0] = out2[0] + loc[0];
                out2[1] = out2[1] + loc[1];
                out2[2] = out2[2] + loc[2];

                myRotationA.applyTo(out2, out2);

                data.add((float) (out2[0] + 0.0));
                data.add((float) (out2[1] + 0.0));
                data.add((float) (out2[2] + 0.0));
                setMotionColor(machine.getMotionMode());
            }

        } catch (Exception e) {
            // If for some reason we get a exception, we just don't add the arrow
            logger.warn("Add arrow : This should normally not happen, please report back.", e);
        }

    }

    private void setMotionColor(final MotionMode m) {
        if (m == MotionMode.G0) {
            data.add(0.87f);
            data.add(0.33f);
            data.add(0.27f);
            data.add(0.5f);
        } else {
            data.add(0.33f);
            data.add(0.27f);
            data.add(0.87f);
            data.add(.5f);
        }
    }
}
