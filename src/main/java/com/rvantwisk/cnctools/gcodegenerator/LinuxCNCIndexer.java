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

package com.rvantwisk.cnctools.gcodegenerator;

import java.text.DecimalFormat;

public class LinuxCNCIndexer implements GCodeGenerator {

    private static DecimalFormat NUMBERFORMAT = new DecimalFormat("#.####");
    final String separator = System.getProperty("line.separator");
    private StringBuilder b;

    private Double G93 = null;
    private Double G94 = null;

    @Override
    public void setOutput(StringBuilder b) {
        this.b = b;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rapidMove(Double a, Double x, Double y, Double z) {
        move(a, x, y, z, true);
    }

    public void move(Double a, Double x, Double y, Double z) {
        move(a, x, y, z, false);
    }

    private void move(Double a, Double x, Double y, Double z, Boolean rapid) {
        String line = "";
        if (rapid) {
            line = line + "G0";
        } else {
            if (G93 != null) {
                line += "G1F" + NUMBERFORMAT.format(G93);
            } else {
                line = line + "G1";
            }
        }

        if (a != null) {
            line += "A" + NUMBERFORMAT.format(a);
        }
        if (x != null) {
            line += "X" + NUMBERFORMAT.format(x);
        }
        if (y != null) {
            line += "Y" + NUMBERFORMAT.format(y);
        }
        if (z != null) {
            line += "Z" + NUMBERFORMAT.format(z);
        }

        b.append(line).append(separator);
    }

    public void setFeedRate(Double f) {
        String line = "";

        if (f != null) {
            line += "G94F" + NUMBERFORMAT.format(f);;
        }
        G94=f;
        b.append(line).append(separator);
    }

    @Override
    public void comment(String comment) {
        b.append("(");
        b.append(comment).append(")").append(separator);
    }

    public void commentLarge(String comment) {
        b.append("(.................................)").append(separator);
        b.append("(");
        b.append(comment).append(")").append(separator);
        b.append("(.................................)").append(separator);
    }

    @Override
    public void enableG93(Double f) {
        String line = "";

        if (f != null) {
            line += "G93F" + NUMBERFORMAT.format(f);
        }

        b.append(line).append(separator);

        G93 = f;
        G94=null;
    }

    @Override
    public void enableG94(Double f) {
        G93=null;
        setFeedRate(f);
    }

    @Override
    public void addRaw(String rawGCode) {
        b.append(rawGCode);
    }


}
