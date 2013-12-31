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

package com.rvantwisk.cnctools.operations;

import com.rvantwisk.cnctools.operations.math.Intersect;
import com.rvantwisk.cnctools.operations.math.Line;
import com.rvantwisk.cnctools.operations.math.Point;

/**
 * Fixes a G-Code file that uses a indexer where G93 wasn't invoked
 * User: rvt
 * Date: 12/1/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstantFHelper {

    private final static double PI2 = Math.PI * 2.0;


    /**
     * Calculate teh distance between two points on a cone
     *
     * @param a1
     * @param x1
     * @param z1
     * @param a2
     * @param x2
     * @param z2
     * @return
     */
    private static double getDistance(final double a1, final double x1, final double z1, final double a2, final double x2, final double z2) {

        // Total rotation
        final double da = (a2 - a1) / 360.0;
        final double dx = x2 - x1;
        final double dz = z2 - z1;

        if (da == 0.0) { // No rotation, distance is just x over z
            return Math.sqrt(dx * dx + dz * dz);
        } else {
            if (dx == 0.0) { // angular rotation, no X movement, moves in spiral down or up
                return Math.PI * da * (z2 * 2 + z1 * 2) / 2;
            } else {
                if (dz == 0.0) { // Movement over cylinder, no Z move
                    final double dl = da * PI2 * z2;
                    return Math.sqrt(dl * dl + dx * dx);
                } else { // Movement over cone

                    Point intersection = Intersect.getLineLine(
                            new Line(new Point(x1, z1), new Point(x2, z2)),
                            new Line(new Point(0.0, 0.0), new Point(1.0, 0.0)));

                    if (intersection != null) {
                        double r1, r2;
                        if (intersection.x < x1) { // move rotates upwards
                            r1 = -intersection.x + x1;
                            r2 = -intersection.x + x2;
                        } else { // move rotates down
                            r1 = intersection.x - x1;
                            r2 = intersection.x - x2;
                        }

                        // Calculate arclength of cone segments
                        final double al1 = PI2 * z1 * da;
                        final double al2 = PI2 * z2 * da;

                        // Calculate arc angle's
                        final double arc1 = al1 / (r1 * PI2);
                        final double arc2 = al2 / (r2 * PI2);

                        // Calculate opposiate and adjacent
                        double o1 = Math.sin(Math.PI / 180.0 * (360.0 * arc1 / 2)) * r1;
                        double o2 = Math.sin(Math.PI / 180.0 * (360.0 * arc2  / 2)) * r2;

//                        double daj = aj1-r2 + x1;

                        return Math.sqrt(o1 * o1 + o2 * o2 - 2 * o1 * o2 * Math.cos(Math.PI / 180.0 * (360.0 * da / 1.0)));
                    } else {
                        throw new RuntimeException("For cone operations we expected to see a intersection.");
                    }

                }

            }


        }

    }

    public static void main(String[] args) {
        /*
        // no rotation, length is sqrt x2 +z2
        System.out.println(getDistance(
                0.0, 1.0, 10.0,
                0.0, 20.0, 20.0));

        // Rotation with constant X, spiral formula
        System.out.println(getDistance(
                0.0, 10.0, 15.0,
                90.0, 10.0, 20.0));

        // Rotation with constant X, spiral formula
        System.out.println(getDistance(
                0.0, 10.0, 20.0,
                0.0001, 10.0, 15));

        System.out.println(getDistance(
                0.0, 10.0, 20.0,
                0.0, 15.0, 20.0));

/*
        System.out.println(getDistance(
                0.0,  0.0, 10.0,
                360.0, 0.0, 20.0)); */

        System.out.println(getDistance(
                0.0, 10.1, 10.0,
                180.0, 10.0, 10.0));

        System.out.println(getDistance(
                0.0, 10.0, 10.0,
                180.0, 10.0, 10.1));

        System.out.println(getDistance(
                0.0, 10.0, 10.0,
                180, 10.1, 10.1));

        /*
        System.out.println(getDistance(
                0.0, 0.0, 20.0,
                180.0, 100.0, 10.0));
        System.out.println(getDistance(
                0.0, 0.0, 20.0,
                180.0, 100.0, 19.0));*/

    }

}

/*
The tangent of the angle = the length of the opposite side
        the length of the adjacent side - See more at: http://www.mathsrevision.net/gcse-maths-revision/trigonometry/sin-cos-and-tan#sthash.xTCdx276.dpuf
        */