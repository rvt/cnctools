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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 12/1/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Dimensions {


    public enum Type {
        LENGTH,
        VELOCITY,
        TIME,
        RPM
    }

    public enum Dim {
        MM,
        CM,
        DEC,
        M,
        INCH,
        FOOT,
        MM_SEC,
        MM_MINUTE,
        FOOT_MINUTE,
        INCH_MINUTE,
        INCH_SEC,
        RPM,
        SEC,
        MINUTE
    }

    private static final Map<Dim, Double> cLength;
    static {
        Map<Dim, Double> aMap = new TreeMap<>();
        aMap.put(Dim.MM, 1.0);
        aMap.put(Dim.CM, 10.0);
        aMap.put(Dim.DEC, 100.0);
        aMap.put(Dim.M, 1000.0);
        aMap.put(Dim.INCH, 25.4);
        aMap.put(Dim.FOOT, 304.8);
        cLength = Collections.unmodifiableMap(aMap);
    }

    private static final Map<Dim, Double> cTime;
    static {
        Map<Dim, Double> aMap = new TreeMap<>();
        aMap.put(Dim.SEC, 1.0);
        aMap.put(Dim.MINUTE, 60.0);
        cTime = Collections.unmodifiableMap(aMap);
    }


    private static final Map<Dim, Double> cVelocity;
    static {
        Map<Dim, Double> aMap = new TreeMap<>();
        aMap.put(Dim.MM_SEC, 1.0);
        aMap.put(Dim.MM_MINUTE, 60.0);
        aMap.put(Dim.FOOT_MINUTE, 25.4);
        aMap.put(Dim.INCH_SEC, 25.4);
        aMap.put(Dim.INCH_MINUTE, 25.4*60.0);
        cVelocity = Collections.unmodifiableMap(aMap);
    }

    public final static class Item implements Map.Entry<Dim, String> {
        private final Dim key;
        private String value;

        Item(Dim key, String value) {
            this.key = key;
            this.value = value;
        }
        Item(Dim key) {
            this.key = key;
            this.value = "";
        }

        @Override
        public Dim getKey() {
            return key;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public java.lang.String getValue() {
            return value;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public java.lang.String setValue(java.lang.String value) {
            String old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Item)) return false;

            Item item = (Item) o;

            if (key != item.key) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private static final ObservableList<Item> RPMLIST = FXCollections.observableArrayList(new Item(Dim.RPM, "RPM"));


    private static final ObservableList<Item> VELOCITYLIST = FXCollections.observableArrayList(
            new Item(Dim.MM_SEC, "mm/sec"),
            new Item(Dim.MM_MINUTE, "mm/minute"),
            new Item(Dim.INCH_SEC, "inch/sec"),
            new Item(Dim.INCH_MINUTE, "inch/minute"));

    private static final ObservableList<Item> LENGTHLIST = FXCollections.observableArrayList(
            new Item(Dim.MM, "mm"),
            new Item(Dim.CM, "cm"),
            new Item(Dim.DEC, "dec"),
            new Item(Dim.M, "m"),
            new Item(Dim.INCH, "inch"),
            new Item(Dim.FOOT, "foot"));





    public static ObservableList<Item> getList(final Type dimType) {
        switch (dimType) {
            case RPM:
                return RPMLIST;
            case VELOCITY:
                return VELOCITYLIST;
            case LENGTH:
                return LENGTHLIST;
        }
        return null;
    }


    public static int getIndex(final Type dimType, Dim dimension) {
        return getList(dimType).indexOf(new Item(dimension));
    }

}
