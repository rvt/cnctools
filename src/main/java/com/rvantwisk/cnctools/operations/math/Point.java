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

package com.rvantwisk.cnctools.operations.math;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/4/13
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Point {
    public double x, y;

    public static final Point zero = new Point(0.0, 0.0);

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point normalize() {
        return new Point(x / getLength(), y / getLength());
    }

    public Point add(final Point B) {
        return new Point(x + B.x, y + B.y);
    }

    public Point sub(final Point B) {
        return new Point(x - B.x, y - B.y);
    }

    public Point mul(final Point B) {
        return new Point(x * B.x, y * B.y);
    }

    public Point mul(final Double m) {
        return new Point(x * m, y * m);
    }

    public Point center() {
        return new Point(x / 2.0, y / 2.0);
    }

    public Point rot90(boolean positive) {
        if (positive) {
            return new Point(y, -x);
        } else {
            return new Point(-y, x);
        }
    }

    public static Boolean isLeft(Point a, Point b, Point c) {
        return ((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)) > 0;
    }


    public static double angleBetween2Lines(Point a, Point b, Point c, Point d) {
        double angle1 = Math.atan2(a.y - b.y,
                a.x - b.x);
        double angle2 = Math.atan2(c.y - d.y,
                c.x - d.x);
        return angle1 - angle2;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", l=" + getLength() +
                '}';
    }
}