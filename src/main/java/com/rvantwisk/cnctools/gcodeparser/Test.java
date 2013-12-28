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

package com.rvantwisk.cnctools.gcodeparser;

import com.rvantwisk.cnctools.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.gcodeparser.exceptions.UnsupportedSimException;
import com.rvantwisk.cnctools.gcodeparser.gcodes.FeedRateMode;
import com.rvantwisk.cnctools.gcodeparser.gcodes.MotionMode;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by rvt on 12/4/13.
 */
public class Test {

    // http://www.java-gaming.org/topics/javafx-3d-api-and-opengl/28797/view.html

    private static final String LE = System.lineSeparator();
    private final static double PI2 = Math.PI * 2.0;

    private Double pA;
    private Double pX;
    private Double pY;
    private Double pZ;

    DecimalFormat df2 = new DecimalFormat("0");
    DecimalFormat dd2 = new DecimalFormat("0.00");
    FeedRateMode lastFeedrateMode = FeedRateMode.G94;

    public static void main2(String[] args) throws SimException, UnsupportedSimException {


    }

    public static void main(String[] args) throws SimException, UnsupportedSimException {
/*

        double eyeX = 1.0;
        double eyeY = 1.0;
        double eyeZ = 1.0;

        Vector3D around = new Vector3D(-eyeY, eyeX, 0.0);

        Vector3D ups = new Vector3D(eyeX, eyeY, eyeZ);
        Rotation r = new Rotation(around, Math.PI/2.0);

        Vector3D nups = r.applyTo(ups);

        System.out.println(nups.toString());
*/

        /*

        System.out.print(GCodes.Group.bar.bar.i);
        System.out.print(GCodes.Group.bar.foo.i);
        System.out.print(GCodes.groups.get(GCodes.Group.FeedRateModes));

*/



        long startTime = System.currentTimeMillis();

        Test t = new Test();
        t.processFile();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);

    }


    private void processFile() throws SimException, UnsupportedSimException {
        try {

            final MachineStatus2 machine = new MachineStatus2();
            final BufferedWriter br = Files.newBufferedWriter(new File("/Volumes/out.tap").toPath(),
                    Charset.forName("UTF-8"),
                    new OpenOption[]{StandardOpenOption.CREATE});

            MachineController machineController = new MachineController() {
                @Override
                public void startBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> currentBlock) {
                    machine.setMachineStatus(machineStatus);
                    // System.out.println("Start Block");
                    pA = machine.getA();
                    pX = machine.getX();
                    pY = machine.getY();
                    pZ = machine.getZ();
                }

                @Override
                public void endBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> currentBlock) {
                    String currentLine = parser.getCurrentLine();
                    //System.out.println("Move End");
                    try {
                        boolean useSpace = false;
                        if (parser.getCurrentLine().contains(" ")) {
                            useSpace = true;
                        }

                        final StringBuffer line = new StringBuffer();
                        Double newFeedRate = 0.0;
                        // FIND A word and replace
                        if (parser.findWordInBlock(new StringBuilder(currentLine)) != null && machine.getMotionMode() == MotionMode.G1) {
                            final Double F = machine.getFeedrate();
                            final Double dX = machine.getX() - pX;
                            final Double dY = machine.getY() - pY;
                            final Double dZ = machine.getZ() - pZ;
                            final Double da = ((machine.getA() - pA) / 360.0) * (PI2 * machine.getZ());
                            final Double D = Math.sqrt(da * da + dX * dX + dY * dY + dZ * dZ);
                            newFeedRate = machine.getFeedrate() / D;
                        }

                        if (newFeedRate != 0.0) {
                            line.append(parser.replaceWord(currentLine, "F" + df2.format(newFeedRate)));
                            if (lastFeedrateMode != FeedRateMode.G93) {
                                if (useSpace) line.append(" ");
                                line.append("G93");
                                lastFeedrateMode = FeedRateMode.G93;
                            }
                            //if (useSpace) line.append(" ");
                            //line.append("(D=" + dd2.format(newFeedRate) + ")");
                        } else {
                            if (lastFeedrateMode != FeedRateMode.G94) {
                                currentLine = parser.replaceWord(currentLine, "F" + df2.format(machine.getFeedrate()));
                                line.append("G94");
                                if (useSpace) line.append(" ");
                                lastFeedrateMode = FeedRateMode.G94;
                            }
                            line.append(currentLine);
                        }

                        line.append(LE);
                        br.write(line.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SimException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            File file = new File("/Volumes/bottle.tap");
            GCodeParser parser = new GCodeParser(machineController, null, new BufferedInputStream(new FileInputStream(file)));

            br.flush();
            br.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}


                        /*
                        System.out.println(
                                " F:" + machineStatus.getFeedrate() +
                                        " G:" + machineStatus.getMotionMode() +
                                        " A:" + machineStatus.getAxis().get(MachineStatus.AxisWords.A) +
                                        " X:" + machineStatus.getAxis().get(MachineStatus.AxisWords.X) +
                                        " Y:" + machineStatus.getAxis().get(MachineStatus.AxisWords.Y) +
                                        " Z:" + machineStatus.getAxis().get(MachineStatus.AxisWords.Z) +
                                        " Line:" + currentLine +
                                        " Block:" + currentBlock +
                                        " findWord:"+this.findWordInBlock(currentLine, "F")
                        ); */


