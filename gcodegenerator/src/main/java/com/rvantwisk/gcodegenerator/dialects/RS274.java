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

package com.rvantwisk.gcodegenerator.dialects;

import com.rvantwisk.gcodegenerator.GCodeBuilder;
import com.rvantwisk.gcodegenerator.GCodeWord;
import com.rvantwisk.gcodegenerator.interfaces.GCodeGenerator;
import com.rvantwisk.gcodegenerator.interfaces.PostProcessorConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

public class RS274 implements GCodeGenerator {

    private final HashMap<String, DecimalFormat> formattersCache = new HashMap<>();
    private static final String OTHER_FORMAT = "_OTHER_"; // Cache entry name for any other formatter not found
    private static final String NOSPACE = "";
    private static final String SPACE = " ";
    private static final String separator = System.getProperty("line.separator");

    private PrintStream out;
    private final PostProcessorConfig postProcessorConfig;

    private boolean addSpaceBetweenWords = true;

    public RS274(PostProcessorConfig pc) {
        postProcessorConfig = pc;
        rebuildSetup();
    }

    @Override
    public void setOutput(final PrintStream out) {
        this.out = out;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void comment(final String comment) {
        out.append("(").append(comment.trim()).append(")").append(separator);
    }

    public void commentLarge(final String comment) {
        out.append("(").append(StringUtils.rightPad("---", 50, "-")).append(")").append(separator);
        out.append("(").append(comment.trim()).append(")").append(separator);
        out.append("(").append(StringUtils.rightPad("---", 50, "-")).append(")").append(separator);
    }

    public void addBlock(final GCodeBuilder gCodeBuilder) {
        String comment = null;

        // Decide to add a spacer between words or not
        String spacer = NOSPACE;
        if (addSpaceBetweenWords) {
            spacer = SPACE;
        }

        Iterator<GCodeWord> iter = gCodeBuilder.getBlock().iterator();
        while (iter.hasNext()) {
            GCodeWord b=iter.next();
            final char startWith = b.getWord().charAt(0);

            if (!iter.hasNext()) {
                spacer = NOSPACE;
            }

            switch (startWith) {
                case 'G':
                case 'M':
                    if (b.getValue() != null) {
                        if (b.getWord().equals("G4")) {
                            out.append(b.getWord()).append(SPACE).append("P").append(formatWord("P", b.getValue()));
                        } else {
                            // Linux CNC might not support this??
                            out.append(b.getWord()).append("/").append(formatWord(b.getWord(), b.getValue()));
                        }
                    } else {
                        out.append(b.getWord()).append(spacer);
                    }
                    break;
                default:
                    if (b.getValue() != null) {
                        String word = b.getWord();
                        if (isAxisWord(word)) {
                            word = reMapWord(word);
                        }
                        out.append(word).append(formatWord(word, b.getValue())).append(spacer);
                    } else if (b.getWord().charAt(0) == GCodeBuilder.COMMENTCHAR) {
                        comment = b.getWord().trim();
                    }
            }
        }

        if (comment != null) {
            out.append(comment);
        }

        out.append(separator);
    }

    /**
     * Rebuild the setup, it takes in the configuration and rebuild's what's needed to beable to generate the code
     */
    public void rebuildSetup() {
        String[] axiss = {"A", "B", "C", "U", "V", "W", "X", "Y", "Z"};
        for (String axis : axiss) {
            Integer numDec = postProcessorConfig.getAxisDecimals().get(axis);
            formattersCache.put(axis, getDecimalFormat(numDec, true));
        }
        Integer numDec = postProcessorConfig.getDecimalsF();
        formattersCache.put("F", getDecimalFormat(numDec, false));

        numDec = postProcessorConfig.getDecimalsS();
        formattersCache.put("S", getDecimalFormat(numDec, true));

        numDec = postProcessorConfig.getDecimalsOthers();
        formattersCache.put(OTHER_FORMAT, getDecimalFormat(numDec, true));
    }

    /**
     * Create a decimal formatter to format the words
     *
     * @param numDecimals
     * @param stripZeros
     * @return
     */
    private DecimalFormat getDecimalFormat(Integer numDecimals, final boolean stripZeros) {
        if (numDecimals == null) {
            numDecimals = 4;
        }
        String padder = "0";
        if (stripZeros) {
            padder = "#";
        }
        final String format = StringUtils.rightPad("#.", numDecimals + 2, padder);
        return new DecimalFormat(format);
    }

    /**
     * Format a specific word to the required sizes
     *
     * @param word
     * @param value
     * @return
     */
    private String formatWord(final String word, Double value) {
        if (formattersCache.get(word) != null) {
            return formattersCache.get(word).format(value);
        }
        return formattersCache.get(OTHER_FORMAT).format(value);
    }

    /**
     * Remap from one word to a other word
     * @param word
     * @return
     */
    private String reMapWord(final String word) {
        if (postProcessorConfig.getAxisMapping().get(word) != null) {
            return postProcessorConfig.getAxisMapping().get(word);
        }
        return word;
    }

    /**
     * Returns true of the given word is a AXIS word
     * @param word
     * @return
     */
    private boolean isAxisWord(final String word) {
        return postProcessorConfig.getAxisMapping().get(word) != null;
    }

    @Override
    public void addRaw(final String s) {
        out.append(s.trim());
    }

    @Override
    public void startProgram() {
        out.append(postProcessorConfig.getPreamble().trim()).append(separator);
    }

    @Override
    public void endProgram() {
        out.append(postProcessorConfig.getPostamble().trim()).append(separator);
    }

    public <T extends PostProcessorConfig> T getPostProcessorConfig() {
        return (T) postProcessorConfig;
    }
}
