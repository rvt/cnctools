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

package com.rvantwisk.gcodegenerator;

import com.rvantwisk.gcodegenerator.dialects.RS274;
import com.rvantwisk.gcodegenerator.dialects.RS274PostProcessorConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by rvt on 12/31/13.
 */
public class GCodeBuilderTest {
    private static String SEPARATOR = System.getProperty("line.separator");


    RS274 generator;
    ByteArrayOutputStream os;
    RS274PostProcessorConfig ppc;

    @Before
    public void newGenerator() {
        ppc = new RS274PostProcessorConfig();

        generator = new RS274(ppc);
        os = new ByteArrayOutputStream();
        generator.setOutput(new PrintStream(os));
    }

    private String asString(final InputStream input) throws IOException {
        StringBuffer sb = new StringBuffer(10);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!StringUtils.isEmpty(line)) {
                    sb.append(line).append(SEPARATOR);
                }
            }
        }
        return sb.toString().trim();
    }

    @Test
    public void testM6T2() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().M6(2));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("M6 T2"));
    }

    @Test
    public void testG0A() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G0().A(123.456));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G0 A123.456"));
    }

    @Test
    public void testG1AF() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G1().A(123.456123).F(123.4));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G1 A123.4561 F123.400"));
    }

    @Test
    public void testPreamblePostamble() throws IOException {
        ppc.setPreamble("F100 G0");
        ppc.setPostamble("M30");
        generator.startProgram();
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("F100 G0" + SEPARATOR + "M30"));
    }

    @Test
    public void testSmallProgram() throws IOException {
        ppc.setPreamble("(start)");
        ppc.setPostamble("M30");
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G0().Z(0.0).A(0.0).X(0.0).Y(0.0));
        generator.addBlock(GCodeBuilder.builder().F(100.0).G1().X(10.0));
        generator.addBlock(GCodeBuilder.builder().Y(10.0).comment("Moved Y Up"));
        generator.addBlock(GCodeBuilder.builder().X(0.0));
        generator.addBlock(GCodeBuilder.builder().Y(0.0));
        generator.comment("Nearly there!");
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("(start)" + SEPARATOR +
                "G0 X0 Y0 Z0 A0" + SEPARATOR +
                "G1 X10 F100.000" + SEPARATOR +
                "Y10; Moved Y Up" + SEPARATOR +
                "X0" + SEPARATOR +
                "Y0" + SEPARATOR +
                "(Nearly there!)" + SEPARATOR +
                "M30"));
    }

    @Test
    public void testG4() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G4(10.0));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G4 P10"));
    }

    @Test
    public void testGXWithValue() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().word("G12", 12.12345678));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G12/12.123"));
    }

    @Test
    public void testSmallComment() throws IOException {
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().word("F", 12.987654321).comment("Set feedrate"));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("F12.988; Set feedrate"));
    }

    @Test
    public void testAxisRemap() throws IOException {
        ppc.getAxisMapping().put("A", "AXIS_A");
        generator.rebuildSetup();
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G0().A(10.12));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G0 AXIS_A10.12"));
    }

    @Test
    public void testAxisDecimals() throws IOException {
        ppc.getAxisDecimals().put("A", 6);
        generator.rebuildSetup();
        generator.startProgram();
        generator.addBlock(GCodeBuilder.builder().G0().A(10.12345678).X(10.12345678));
        generator.endProgram();

        String out = asString(new ByteArrayInputStream(os.toByteArray()));
        assertTrue(out.equals("G0 X10.1235 A10.123457"));
    }

}
