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

package com.rvantwisk.cnctools.data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Collections;
import java.util.Map;

/**
 * Created by rvt on 12/27/13.
 */
public class PostProcessorConfig implements com.rvantwisk.gcodegenerator.interfaces.PostProcessorConfig{

    // General
    private final StringProperty name = new SimpleStringProperty();
    private final ObservableMap<String, String> axisMapping = FXCollections.observableHashMap();
    private final ObservableMap<String, Integer> axisDecimals = FXCollections.observableHashMap();
    private final BooleanProperty hasToolChanger = new SimpleBooleanProperty();
    private final StringProperty preabmle = new SimpleStringProperty();
    private final StringProperty postamble = new SimpleStringProperty();
    private final IntegerProperty decimalsF = new SimpleIntegerProperty();
    private final IntegerProperty decimalsS = new SimpleIntegerProperty();
    private final IntegerProperty decimalsOthers = new SimpleIntegerProperty();
    private StringProperty dialect = new SimpleStringProperty();
    private final BooleanProperty spaceBetweenWords = new SimpleBooleanProperty();

    public void PostProcessorConfig() {
        dialect.setValue("RS274");
    }

    public Object readResolve() {
        dialect = new SimpleStringProperty();
        if (dialect==null) {
            dialect.setValue("RS274");
        }
        if (dialect.get()==null) {
            dialect.set("RS274");
        }
        return this;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableMap<String, String> axisMappingProperty() {
        return axisMapping;
    }

    public ObservableMap<String, Integer> axisDecimalsProperty() {
        return axisDecimals;
    }

    @Override
    public boolean getHasToolChanger() {
        return hasToolChanger.get();
    }

    public BooleanProperty hasToolChangerProperty() {
        return hasToolChanger;
    }

    public void setHasToolChanger(boolean hasToolChanger) {
        this.hasToolChanger.set(hasToolChanger);
    }

    @Override
    public String getPreabmle() {
        return preabmle.get();
    }

    public StringProperty preabmleProperty() {
        return preabmle;
    }

    public void setPreabmle(String preabmle) {
        this.preabmle.set(preabmle);
    }

    @Override
    public String getPostamble() {
        return postamble.get();
    }

    public StringProperty postambleProperty() {
        return postamble;
    }

    public void setPostamble(String postamble) {
        this.postamble.set(postamble);
    }

    @Override
    public int getDecimalsF() {
        return decimalsF.get();
    }

    public IntegerProperty decimalsFProperty() {
        return decimalsF;
    }

    public void setDecimalsF(int decimalsF) {
        this.decimalsF.set(decimalsF);
    }

    @Override
    public int getDecimalsS() {
        return decimalsS.get();
    }

    public IntegerProperty decimalsSProperty() {
        return decimalsS;
    }

    public void setDecimalsS(int decimalsS) {
        this.decimalsS.set(decimalsS);
    }

    @Override
    public int getDecimalsOthers() {
        return decimalsOthers.get();
    }

    public IntegerProperty decimalsOthersProperty() {
        return decimalsOthers;
    }

    public void setDecimalsOthers(int decimalsOthers) {
        this.decimalsOthers.set(decimalsOthers);
    }

    @Override
    public String getDialect() {
        return dialect.get();
    }

    public StringProperty dialectProperty() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect.set(dialect);
    }

    @Override
    public boolean getSpaceBetweenWords() {
        return spaceBetweenWords.get();
    }

    @Override
    public Map<String, String> getAxisMapping() {
        return Collections.unmodifiableMap(axisMapping);
    }

    @Override
    public Map<String, Integer> getAxisDecimals() {
        return Collections.unmodifiableMap(axisDecimals);
    }

    public BooleanProperty spaceBetweenWordsProperty() {
        return spaceBetweenWords;
    }

    public void setSpaceBetweenWords(boolean spaceBetweenWords) {
        this.spaceBetweenWords.set(spaceBetweenWords);
    }


}
