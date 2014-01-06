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

package com.rvantwisk.cnctools.operations.facing;

import math.geom3d.Point3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * Created by rvt on 1/6/14.
 */
public class AffineTransform3DFixed extends AffineTransform3D {


    public AffineTransform3DFixed(double[] coefficients) {
        super(coefficients);
    }

    @Override
    public Point3D[] transformPoints(Point3D[] src, Point3D[] dst) {
        if (dst==null)
            dst = new Point3D[src.length];
        if (dst[0]==null)
            for (int i = 0; i<src.length; i++)
                dst[i] = new Point3D();

        double coef[] = coefficients();

        for (int i = 0; i < src.length; i++) {
            dst[i] = new Point3D(
                    src[i].getX() * coef[0] + src[i].getY() * coef[1] + src[i].getZ() * coef[2] + coef[3],
                    src[i].getX() * coef[4] + src[i].getY() * coef[5] + src[i].getZ() * coef[6] + coef[7],
                    src[i].getX() * coef[8] + src[i].getY() * coef[9] + src[i].getZ() * coef[10] + coef[11]);
        }
        return dst;
    }

    @Override
    public Point3D transformPoint(Point3D src) {
        double coef[] = coefficients();
        return new Point3D(src.getX()*coef[0]+src.getY()*coef[1]
                +src.getZ()*coef[2]+coef[3], src.getX()*coef[4]+src.getY()
                *coef[5]+src.getZ()*coef[6]+coef[7], src.getX()*coef[8]
                +src.getY()*coef[9]+src.getZ()*coef[10]+coef[11]);
    }
}
