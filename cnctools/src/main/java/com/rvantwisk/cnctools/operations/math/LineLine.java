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
 * Date: 12/1/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineLine {

    public static Point getLineLineIntersection(final Line pLine1, final Line pLine2) {
        Point result = null;

        Double s1_x = pLine1.p2.x - pLine1.p1.x,
                s1_y = pLine1.p2.y - pLine1.p1.y,

                s2_x = pLine2.p2.x - pLine2.p1.x,
                s2_y = pLine2.p2.y - pLine2.p1.y,

                s = (-s1_y * (pLine1.p1.x - pLine2.p1.x) + s1_x * (pLine1.p1.y - pLine2.p1.y)) / (-s2_x * s1_y + s1_x * s2_y),
                t = (s2_x * (pLine1.p1.y - pLine2.p1.y) - s2_y * (pLine1.p1.x - pLine2.p1.x)) / (-s2_x * s1_y + s1_x * s2_y);

        // Collision detected
        if (!t.isInfinite()) {
            result = new Point(
                    (int) (pLine1.p1.x + (t * s1_x)),
                    (int) (pLine1.p1.y + (t * s1_y)));
        }

        return result;
    }

}
