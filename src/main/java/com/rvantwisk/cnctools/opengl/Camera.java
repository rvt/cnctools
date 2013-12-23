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

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Camera system that can be used with gluLookAt to allow rotation of a camera and calculate correct center, eye and up vectors
 * Created by rvt on 12/12/13.
 */
public class Camera {

    private final Vector3D eye;
    private final Vector3D up;
    private final Vector3D center;
    private final Vector3D diry;

    private float fovy = 60.0f;

    public Camera() {
        eye = new Vector3D(0.0, 0.0, 100.0f);
        up = new Vector3D(0.0, 1.0, 0.0);
        center = new Vector3D(0.0, 0.0, 0.0);
        fovy = 60.0f;
        diry = up.crossProduct(center.subtract(eye)).normalize();
    }

    public Camera(Vector3D eye, Vector3D center, Vector3D up, float fovy) {
        this.eye = eye;
        this.up = up;
        this.center = center;
        this.fovy = fovy;
        diry = up.crossProduct(center.subtract(eye)).normalize();
    }

    public Camera(Vector3D eye, Vector3D center, Vector3D up) {
        this.eye = eye;
        this.up = up;
        this.center = center;
        this.fovy = 60.0f;
        diry = up.crossProduct(center.subtract(eye)).normalize();
    }


    /**
     * Zoom in/out
     *
     * @param delta
     */
    public Camera zoomView(final double delta) {
        return new Camera(center.add(eye.subtract(center).scalarMultiply(delta + 1.0f)), center, up, fovy);
    }

    public Camera rotateView(final double dx, final double dy) {
        Vector3D eye;
        eye = new Rotation(up.subtract(center), -dx * Math.PI).applyTo(this.eye);
        eye = new Rotation(diry.subtract(center), dy * Math.PI).applyTo(eye);

//        eye= rotate(center, up, this.eye,  -dx * Math.PI);
//        eye = rotate(center, up, eye,  dy * Math.PI);

        // now calculate the new up-vector
        Vector3D upCenter = center.add(up);
        upCenter = new Rotation(diry.subtract(center), dy * Math.PI).applyTo(upCenter);
//        upCenter = rotate(center, diry, upCenter,  dy * Math.PI);
        Vector3D up = upCenter.subtract(center);
        up = up.normalize();

        return new Camera(eye, center, up, fovy);

        /*
        float dx = (float)(newPos.x() - _oldMousePos.x()) / (float)_width;
        float dy = (float)(newPos.y() - _oldMousePos.y()) / (float)_height;
        _oldMousePos = newPos;
        updateDir();
        // rotate eye around center
        _eye.rotate(_center, _up, -dx * PI );
        _eye.rotate(_center, _diry, dy*PI );

        // now calculate the new up-vector
        P3<float> upCenter = _center + _up;
        upCenter.rotate( _center , _diry, dy*PI);
        _up = upCenter - _center;
        _up *= 1.0/_up.norm();

        updateGL(); */

    }

    public Camera panView(double dx, double dy, final double sdx, final double sdy) {

        double length = 2.0 * eye.subtract(center).getNorm() * Math.tan((fovy / 360.0) * Math.PI);

        Vector3D y_pan = diry.scalarMultiply(dx * length * (sdx / sdy));
        Vector3D x_pan = up.scalarMultiply(dy * length);

        return new Camera(eye.add(y_pan.add(x_pan)), center.add(y_pan.add(x_pan)), up, fovy);
    }

    /**
     * Rotation around axis
     *
     * @param origin
     * @param v
     * @param alfa
     * @return
     * @Deprecated
     */
    private Vector3D rotate(Vector3D origin, Vector3D v, Vector3D that, double alfa) {
        // rotate point p by alfa deg/rad around vector o->v
        // p = o + M*(p-o)
        double[][] M = new double[3][3];
        double c = Math.cos(alfa);
        double D = 1.0 - c;
        double s = Math.sin(alfa);
        M[0][0] = v.getX() * v.getX() * D + c;
        M[0][1] = v.getY() * v.getX() * D + v.getZ() * s;
        M[0][2] = v.getZ() * v.getX() * D - v.getY() * s;
        M[1][0] = v.getX() * v.getY() * D - v.getZ() * s;
        M[1][1] = v.getY() * v.getY() * D + c;
        M[1][2] = v.getZ() * v.getY() * D + v.getX() * s;
        M[2][0] = v.getX() * v.getZ() * D + v.getY() * s;
        M[2][1] = v.getY() * v.getZ() * D - v.getX() * s;
        M[2][2] = v.getZ() * v.getZ() * D + c;
        // matrix multiply
        double[] vector = new double[3];
        vector[0] = that.getX() - origin.getX();
        vector[1] = that.getY() - origin.getY();
        vector[2] = that.getZ() - origin.getZ();
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = 0;
            for (int j = 0; j < 3; j++)
                result[i] += vector[j] * M[i][j];
        }

        return new Vector3D(origin.getX() + result[0], origin.getY() + result[1], origin.getZ() + result[2]);
    }


    public Vector3D getEye() {
        return eye;
    }

    public Vector3D getUp() {
        return up;
    }

    public Vector3D getCenter() {
        return center;
    }

    public float getFovy() {
        return fovy;
    }
}