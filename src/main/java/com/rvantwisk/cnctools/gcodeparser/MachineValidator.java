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

import java.util.Map;
import java.util.Set;

public abstract class MachineValidator {


    public abstract void preVerify(Map<String, ParsedWord> block) throws SimException;

    public abstract void postVerify(MachineStatus machineStatus) throws SimException;

    /**
     * Helper to find multiple words in teh same block
     *
     * @param block
     * @param enumClass
     * @param <T>
     * @return
     */
    protected <T extends Enum<T>> boolean hasMultipleWords(Map<String, ParsedWord> block, Class<T> enumClass) {
        return wordCount(block, enumClass) > 1;
    }

    /**
     * Returns a word count within teh current block
     * This is usefull to find multiple the same words within a modal group in the current block
     *
     * @param block
     * @param enumClass
     * @param <T>
     * @return
     */
    protected <T extends Enum<T>> int wordCount(Map<String, ParsedWord> block, Class<T> enumClass) {
        int wordCount = 0;

        T[] items = enumClass.getEnumConstants();

        for (T item : items) {
            if (block.containsKey(item.toString())) {
                wordCount++;
            }
        }
        return wordCount;
    }

    /**
     * Returns true of the block contains any of hasAnyOfThis
     * @param block
     * @param hasAnyOfThis
     * @return
     */
    protected boolean hasAny(Map<String, Object> block, final String[] hasAnyOfThis) {
        for (final String item : hasAnyOfThis) {
            if (block.containsKey(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true of the block contains any of hasAnyOfThis
     * @param block
     * @param hasAnyOfThis
     * @return
     */
    protected boolean hasAny(Set<String> block, final String[] hasAnyOfThis) {
        for (final String item : hasAnyOfThis) {
            if (block.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
