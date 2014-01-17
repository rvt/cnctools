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


import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * A Low Level GCode builder no intelligence has been added to the class so the caller is responsible to build correct G-Code
 * If you are using this class, always try to use the method for the GCode you try to use unless there is no one available.
 * <p/>
 * Final generation of  GCode depends on the actual dialect used.
 * For example G4 DWELL is called using new GCodeBuilder().G4(4.5)
 * Actual implements might convert this to G4/4.5 or G4P4.5 (LinuxCNC) or G4P4500 (Dwell time in ms, other dialect)
 */
final public class GCodeBuilder {
    private final static DecimalFormat wordFormatter = new DecimalFormat("#.#########"); // Formatting and trimming of numbers
    public static char COMMENTCHAR = ';';


    /* The idea was the that G-Code builder would 'know' how to convert it, but I abbandoned the idea, left in place just in case...
    private final Dimensions.Dim funit;
    private final Dimensions.Dim lUnit;
    private final Dimensions.Dim tUnit;

    public GCodeBuilder(final Dimensions.Dim fUnit, final Dimensions.Dim lUnit, final Dimensions.Dim tUnit) {
        if (fUnit.getType()!=Dimensions.Type.VELOCITY) {
            throw new IllegalArgumentException("fUnit must be of type VELOCITY");
        }
        if (lUnit.getType()!=Dimensions.Type.LENGTH) {
            throw new IllegalArgumentException("lUnit must be of type LENGTH");
        }
        if (tUnit.getType()!=Dimensions.Type.TIME) {
            throw new IllegalArgumentException("tUnit must be of type TIME");
        }
        this.funit = fUnit;
        this.lUnit = lUnit;
        this.tUnit = tUnit;
    } */

    final Collection<GCodeWord> block = new TreeSet<>();
//    final Collection<GCodeWord> block = new ArrayList<>();

    public static GCodeBuilder builder() {
        return new GCodeBuilder();
    }

    /**
     * Add a new GCode word
     *
     * @param word GCodeWord
     * @return
     */
    public GCodeBuilder set(final GCodeWord word) {
        block.add(word);
        return this;
    }

    /**
     * G0 rapid move
     *
     * @param args axis words in the order XYZABCUVW
     * @return
     */
    public GCodeBuilder G0(final Double... args) {
        block.add(new GCodeWord("G0", null));
        addIf("X", args, 0);
        addIf("Y", args, 1);
        addIf("Z", args, 2);
        addIf("A", args, 3);
        addIf("B", args, 4);
        addIf("C", args, 5);
        addIf("U", args, 6);
        addIf("V", args, 7);
        addIf("W", args, 8);
        return this;
    }

    /**
     * G1 Linear move
     *
     * @param args axis words in the order XYZABCUVW
     * @return
     */
    public GCodeBuilder G1(final Double... args) {
        block.add(new GCodeWord("G1", null));
        addIf("X", args, 0);
        addIf("Y", args, 1);
        addIf("Z", args, 2);
        addIf("A", args, 3);
        addIf("B", args, 4);
        addIf("C", args, 5);
        addIf("U", args, 6);
        addIf("V", args, 7);
        addIf("W", args, 8);
        return this;
    }

    /**
     * Dwell for XX seconds
     *
     * @param time Dwell time in seconds
     * @return
     */
    public GCodeBuilder G4(final Double time) {
        block.add(new GCodeWord("G4", time));
        return this;
    }

    /**
     * G2 clockwise ARC XYZIJP parameters
     * If you need to create a arc in a different plane please use the builder's functions for example new GCodeBuilder().G2().X(0.0).Z(0.0)... etc
     *
     * @param args Arguments in the order XYZIJP
     * @return
     */
    public GCodeBuilder G2XYZIJP(final Double... args) {
        block.add(new GCodeWord("G2", null));
        addIf("X", args, 0);
        addIf("Y", args, 1);
        addIf("Z", args, 2);
        addIf("I", args, 3);
        addIf("J", args, 4);
        addIf("P", args, 5);
        return this;
    }

    /**
     * G3 counter clockwise ARC XYIJZP parameters
     * If you need to create a arc in a different plane please use the builder's functions for example new GCodeBuilder().G2().X(0.0).Z(0.0)... etc
     *
     * @param args Arguments in the order XYZIJP
     * @return
     */
    public GCodeBuilder G3XYIJZP(final Double... args) {
        addIf("X", args, 0);
        addIf("Y", args, 1);
        addIf("Z", args, 2);
        addIf("I", args, 3);
        addIf("J", args, 4);
        addIf("P", args, 5);
        return this;
    }

    /**
     * Add a word not found within the class, use with caution, always try to find the correct word. If you don't find the word
     * please submit a pull request.
     * <p/>
     * This is the builder.set(New GCodeWord(...)
     *
     * @param name  Name  of the word t be added, for example P
     * @param value Value of the word to be added
     * @return
     */
    public GCodeBuilder word(final String name, final Double value) {
        block.add(new GCodeWord(name, value));
        return this;
    }

    /**
     * Add a word only if the value parameter is available
     *
     * @param word  Word to be added, for example X
     * @param value Value of the added word, as a array of Double
     * @param pos   Position of the value to test
     */
    private void addIf(final String word, final Double[] value, final int pos) {
        if (value != null && pos < value.length) {
            block.add(new GCodeWord(word, value[pos]));
        }
    }

    /**
     * Add A axis value
     *
     * @param v value for the A axis
     * @return
     */
    public GCodeBuilder A(Double v) {
        block.add(new GCodeWord("A", v));
        return this;
    }

    /**
     * Add B axis value
     *
     * @param v value for the B axis
     * @return
     */
    public GCodeBuilder B(Double v) {
        block.add(new GCodeWord("B", v));
        return this;
    }

    /**
     * Add C axis value
     *
     * @param v value for the C axis
     * @return
     */
    public GCodeBuilder C(Double v) {
        block.add(new GCodeWord("C", v));
        return this;
    }

    /**
     * Add X axis value
     *
     * @param v value for the X axis
     * @return
     */
    public GCodeBuilder X(Double v) {
        block.add(new GCodeWord("X", v));
        return this;
    }

    /**
     * Add Y axis value
     *
     * @param v value for the Y axis
     * @return
     */
    public GCodeBuilder Y(Double v) {
        block.add(new GCodeWord("Y", v));
        return this;
    }

    /**
     * Add Z axis value
     *
     * @param v value for the Z axis
     * @return
     */
    public GCodeBuilder Z(Double v) {
        block.add(new GCodeWord("Z", v));
        return this;
    }

    /**
     * Add U axis value
     *
     * @param v value for the U axis
     * @return
     */
    public GCodeBuilder U(Double v) {
        block.add(new GCodeWord("U", v));
        return this;
    }

    /**
     * Add V axis value
     *
     * @param v value for the V axis
     * @return
     */
    public GCodeBuilder V(Double v) {
        block.add(new GCodeWord("V", v));
        return this;
    }

    /**
     * Add W axis value
     *
     * @param v value for the W axis
     * @return
     */
    public GCodeBuilder W(Double v) {
        block.add(new GCodeWord("W", v));
        return this;
    }

    /**
     * G93 Inverse time mode
     * In inverse time feed rate mode, an F word means the move should be completed in [one divided by the F number] minutes.
     * For example, if the F number is 2.0, the move should be completed in half a minute.
     *
     * @param f value for the W axis
     * @return
     */
    public GCodeBuilder G93(final double f) {
        block.add(new GCodeWord("G93", null));
        F(f);
        return this;
    }

    /**
     * G92 makes the current point have the coordinates you want (without motion), where the axis words contain the axis numbers you want. All axis words are optional, except that at least one must be used. If an axis word is not used for a given axis, the coordinate on that axis of the current point is not changed.
     *
     * @return
     */
    public GCodeBuilder G92(final String axis, final Double offset) {
        block.add(new GCodeWord("G92", null));
        block.add(new GCodeWord(axis, offset));
        return this;
    }

    /**
     * Feed rate
     *
     * @param v
     * @return
     */
    public GCodeBuilder F(Double v) {
        block.add(new GCodeWord("F", v));
        return this;
    }

    /**
     * Tool selection, properly in combination with M6
     *
     * @param v
     * @return
     */
    public GCodeBuilder T(Double v) {
        block.add(new GCodeWord("T", v));
        return this;
    }

    /**
     * Spindle Speed, properly used with M3 or M4
     *
     * @param v
     * @return
     */
    public GCodeBuilder S(Double v) {
        block.add(new GCodeWord("S", v));
        return this;
    }

    /**
     * Start the spindle clockwise at the S speed.
     *
     * @param s
     * @return
     */
    public GCodeBuilder M3(final Double s) {
        block.add(new GCodeWord("M3", null));
        S(s);
        return this;
    }

    /**
     * Start the spindle counterclockwise at the S speed.
     *
     * @param s
     * @return
     */
    public GCodeBuilder M4(final Double s) {
        block.add(new GCodeWord("M4", null));
        S(s);
        return this;
    }

    /**
     * stop the spindle.
     *
     * @return
     */
    public GCodeBuilder M5() {
        block.add(new GCodeWord("M5", null));
        return this;
    }

    /**
     * End the program. Pressing cycle start will start the program at the beginning of the file, yo umight want to consider M2
     *
     * @return
     */
    public GCodeBuilder M2() {
        block.add(new GCodeWord("M2", null));
        return this;
    }

    /**
     * Exchange pallet shuttles and end the program. Pressing cycle start will start the program at the beginning of the file.
     *
     * @return
     */
    public GCodeBuilder M30() {
        block.add(new GCodeWord("M30", null));
        return this;
    }

    /**
     * Turn mist coolant on.
     *
     * @return
     */
    public GCodeBuilder M7() {
        block.add(new GCodeWord("M7", null));
        return this;
    }

    /**
     * Turn flood coolant on.
     *
     * @return
     */
    public GCodeBuilder M8() {
        block.add(new GCodeWord("M8", null));
        return this;
    }

    /**
     * Turn all coolant off.
     *
     * @return
     */
    public GCodeBuilder M9() {
        block.add(new GCodeWord("M9", null));
        return this;
    }

    /**
     * Add a line number manually
     *
     * @return
     */
    public GCodeBuilder N(final Integer n) {
        block.add(new GCodeWord("N", Double.valueOf(n)));
        return this;
    }


    /**
     * Tool Change, change to tool selected
     *
     * @param t
     * @return
     */
    public GCodeBuilder M6(Integer t) {
        block.add(new GCodeWord("M6", null));
        block.add(new GCodeWord("T", Double.valueOf(t)));
        return this;
    }

    /**
     * Add a comment to this block, if you add multiple comment's only the last one will be used (depends on dialect)
     *
     * @param comment
     * @return
     */
    public GCodeBuilder comment(final String comment) {
        block.add(new GCodeWord(COMMENTCHAR + " " + comment, null));
        return this;
    }

    @Override
    /**
     * Generate a default GCode line. You properly want to create a dialect for your machine for some of the specifics
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        String comment = null;
        for (GCodeWord b : block) {
            final char startWith = b.getWord().charAt(0);
            switch (startWith) {
                case 'G':
                case 'M':
                    if (b.getValue() != null) {
                        sb.append(b.getWord()).append("/").append(wordFormatter.format(b.getValue()));
                    } else {
                        sb.append(b.getWord());
                    }
                    break;
                default:
                    if (b.getValue() != null) {
                        sb.append(b.getWord()).append(wordFormatter.format(b.getValue()));
                    } else if (b.getWord().charAt(0) == GCodeBuilder.COMMENTCHAR) {
                        comment = b.getWord();
                    }
            }
        }
        sb.append(' ' + COMMENTCHAR + ' ' + comment);
        return sb.toString();
    }

    /**
     * Return's the current created block
     *
     * @return
     */
    public Collection<GCodeWord> getBlock() {
        return Collections.unmodifiableCollection(block);
    }
}
