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
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimParsingException;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GCode parser
 * Parses a GCode file and send's the parsed blocks to the MachineController
 * The machine controller can then decide to support some commands or, throw a exception or whatever.
 * The machine controller can else decide to convert some codes, for example G2 to linear X/Y/Z steps.
 * Or, if you wish just drive some graphics. This all depends on the implementation of the Machine Controller
 * <p/>
 * The GCode parser will also test for correctness of the GCode file, for example it dis-allows F0 with G1 motions
 * <p/>
 * User: rvt
 * Date: 12/3/13
 * Time: 8:34 AM
 * <p/>
 * TODO: Change in such a way that the capabilities can easily be extended.
 */
public class GCodeParser {

    private final MachineStatus machineStatus = new MachineStatus();        // kept's track of machine status after end of block
    private final MachineStatus intermediateStatus = new MachineStatus();   // Keeps tracking of machine status during block processing
    private final MachineController machineController;                      // A machine controller to send parsed block's + machine status into
    private DecimalFormat wordFormatter = new DecimalFormat("#.#########"); // Formatting and trimming of numbers
    private final AbstractMachineValidator machineValidator;                      // A machine controller to send parsed block's + machine status into
    private final Pattern GCODEPATTERN = Pattern.compile("([GXYZABCDFHIJKLMNPQRSTUVW]o?)\\s*([0-9.+-]+)?(\\s*/?\\s*)([0-9.+-]+)?");
    private final Pattern COMMENTS1 = Pattern.compile("\\(.*\\)"); // Comment between ()
    private final Pattern COMMENTS2 = Pattern.compile("\\;.*"); // comment after ;

    private String currentLine = ""; // Hold's the current line between begin and endblock calls
    private int currentLineNumber=1;

    public GCodeParser(final MachineController machineController, final AbstractMachineValidator machineValidator, final InputStream input) throws SimException {
        this.machineController = machineController;
        this.machineValidator = machineValidator;
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                currentLine = line;
                parseLine();
                currentLineNumber++;
            }
        } catch (IOException x) {
            throw new SimParsingException("Unable to read stream", x);
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine() throws SimException {
        // Remove comments between () and all comments after ;
        final StringBuilder parsedLine = new StringBuilder(COMMENTS2.matcher(COMMENTS1.matcher(currentLine).replaceAll("")).replaceAll(""));

        // A map that holds all parsed codes
        Map<String, ParsedWord> block = new HashMap<>(10);

        // Hold's the current parsed word
        ParsedWord thisWord;
        while ((thisWord = findWordInBlock(parsedLine)) != null) {

            final int pos = parsedLine.indexOf(thisWord.asRead);
            parsedLine.replace(pos, pos + thisWord.asRead.length(), "");

            // We can have multiple G/M words within a block, so we move them to the 'key'
            String blockKey = thisWord.word;
            if (blockKey.equals("G") || blockKey.equals("M")) {
                blockKey = thisWord.parsed.replace('.', '_'); // Store gwords with a . as _
            }

            if (block.containsKey(blockKey)) {
                throw new SimValidationException("Multiple " + thisWord.word + " words on one line.");
            } else {
                block.put(blockKey, thisWord);
            }
        }

        // First verify if the block itself is valid before we process it
        if (machineValidator != null) machineValidator.preVerify(block);

        // Copy to intermediate status to ensure our machine status is always valid
        intermediateStatus.copyFrom(machineStatus);
        // Notify the controller that we are about to start a new block, the block itself is valid, for example there we be no G1's and G0 on one line
        machineController.startBlock(this, intermediateStatus, Collections.unmodifiableMap(block));
        intermediateStatus.startBlock();

        // Copy the block to the machine
        intermediateStatus.setBlock(block);

        // Block en, no more data will come in for this block
        intermediateStatus.endBlock();

        // Verify machine's state, for example if a R was found, do we also have a valid G to accompany with it?
        if (machineValidator != null) machineValidator.postVerify(intermediateStatus);

        // Notify the controller that everything was ok, now teh controller start 'running' the data
        machineController.endBlock(this, intermediateStatus, Collections.unmodifiableMap(block));

        // setup new and valid machine status
        machineStatus.copyFrom(intermediateStatus);
    }

    /**
     * Find the next gcode in the current block
     *
     * @param GCodeBLock
     * @return TODO: Parse commands
     */
    public ParsedWord findWordInBlock(final StringBuilder GCodeBLock) {
        Matcher myMatcher = GCODEPATTERN.matcher(GCodeBLock);

        if (myMatcher.find()) {
            try {
                final String g0 = myMatcher.group(0);
                final String g1 = myMatcher.group(1);
                final String g2 = myMatcher.group(2);
                final String g4 = myMatcher.group(4);
                final Double v = Double.valueOf(g2);
                final String value = wordFormatter.format(v);

                // public ParsedWord(String word, String parsed, Double value, String asRead) {..}
                // When a G block was found and a G 'value' use that examples G4/10 or G4 10
                if (g1 == "G") {
                    if (g4 != null && !StringUtils.isEmpty(g4)) {
                        return new ParsedWord(g1, g1 + value, Double.valueOf(g4), g0);
                    } else {
                        return new ParsedWord(g1, g1 + value, null, g0);
                    }
                } else {
                    return new ParsedWord(g1, g1 + value, v, g0);
                }

            } catch (NumberFormatException e) {
                // Assume word wasn't found
            }
        }
        return null;
    }

    /**
     * Test of the current command holds a specific word
     * If the word contains more then 1 character we check the complete word, else we check the letter
     *
     * @param currentBlock Block
     * @param word         A word examples G94, A, B, G0 G30.1, G30_1
     * @return
     */

    private boolean hasWord(final Map<String, ParsedWord> currentBlock, final String word) {

        return currentBlock.containsKey(word);

    }

    /**
     * Returns a word count within teh current block
     * This is usefull to find multiple the same words within a modal group in the current block
     *
     * @param currentBlock
     * @param enumClass
     * @param <T>
     * @return
     */
    private <T extends Enum<T>> int wordCount(Map<String, ParsedWord> currentBlock, Class<T> enumClass) {
        int wordCount = 0;

        T[] items = enumClass.getEnumConstants();

        for (T item : items) {
            if (hasWord(currentBlock, item.toString())) {
                wordCount++;
            }
        }
        return wordCount;
    }

    /**
     * Replace a word within a GCODE block
     *
     * @param GCodeBLock
     * @param replaceWith
     * @return
     * @throws Exception
     */
    protected String replaceWord(final String GCodeBLock, final String replaceWith) throws SimValidationException {
        if (replaceWith.startsWith("M") || replaceWith.startsWith("G")) {
            throw new SimValidationException("M and G words cannot be replaced at this moment.");
        }

        final ParsedWord word = findWordInBlock(new StringBuilder(GCodeBLock));
        if (word == null) {
            return GCodeBLock + replaceWith;
        } else {
            return GCodeBLock.replace(word.asRead, replaceWith);
        }
    }

    /**
     * Helper to find multiple words in teh same block
     *
     * @param currentBlock
     * @param enumClass
     * @param <T>
     * @return
     */
    private <T extends Enum<T>> boolean hasMultipleWords(Map<String, ParsedWord> currentBlock, Class<T> enumClass) {
        return wordCount(currentBlock, enumClass) > 1;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }
}
