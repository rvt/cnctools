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

package com.rvantwisk.cnctools.misc;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by rvt on 12/29/13.
 */
public class DimensionsTest {


    @Test
    public void testTime() throws Exception {
        double out;
        out = Dimensions.convert(60.0, Dimensions.Dim.SEC, Dimensions.Dim.MINUTE);
        assertTrue(Math.abs(out - 1.0) < 0.0001);

        out = Dimensions.convert(3600.0, Dimensions.Dim.SEC, Dimensions.Dim.HOUR);
        assertTrue(Math.abs(out - 1.0) < 0.0001);
    }

    @Test
    public void testDistance() throws Exception {
        double out;
        out = Dimensions.convert(1.0, Dimensions.Dim.CM, Dimensions.Dim.DEC);
        assertTrue(Math.abs(out - 0.1) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.FOOT, Dimensions.Dim.CM);
        assertTrue(Math.abs(out - 30.48) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.INCH, Dimensions.Dim.CM);
        assertTrue(Math.abs(out - 2.54) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.INCH, Dimensions.Dim.FOOT);
        assertTrue(Math.abs(out - 0.08333333333333333) < 0.0001);
    }

    @Test
    public void testVelocity() throws Exception {
        double out;
        out = Dimensions.convert(60.0, Dimensions.Dim.MM_MINUTE, Dimensions.Dim.MM_SEC);
        assertTrue(Math.abs(out - 1.0) < 0.0001);

        out = Dimensions.convert(25.4, Dimensions.Dim.MM_SEC, Dimensions.Dim.INCH_SEC);
        assertTrue(Math.abs(out - 1.0) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.MM_SEC, Dimensions.Dim.INCH_MINUTE);
        assertTrue(Math.abs(out - 2.362204724409449) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.FOOT_MINUTE, Dimensions.Dim.MM_SEC);
        assertTrue(Math.abs(out - 5.08) < 0.0001);

        out = Dimensions.convert(1.0, Dimensions.Dim.INCH_SEC, Dimensions.Dim.FOOT_MINUTE);
        assertTrue(Math.abs(out - 5) < 0.0001);
    }


}
