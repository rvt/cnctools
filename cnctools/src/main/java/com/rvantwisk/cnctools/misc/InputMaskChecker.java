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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/10/13
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InputMaskChecker implements ChangeListener<String> {

    public static final String NOTEMPTY      = "^.+$";
    public static final String NUMERIC      = "^[0-9]*$";
    public static final String TEXTONLY     = "^\\w*$";
    public static final String PASSWORD     = "^[\\w\\+\\!\\?\\-\\$\\&\\%£]+$";
    public static final String DATASOURCE   = "^([a-zA-Z]+:){3}@([a-zA-Z0-9]+:)+[a-zA-Z0-9]+$";
    public static final String TCPPORT      = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[2-9]\\d{3}|1[1-9]\\d{2}|10[3-9]\\d|102[4-9])$";

    private static final String STYLE = "-fx-effect: dropshadow(gaussian, red, 4, 0.0, 0, 0);";

    public final BooleanProperty erroneous = new SimpleBooleanProperty(false);

    private final String mask;
    private final int max_lenght;
    private final TextField control;


    public InputMaskChecker(String mask, TextField control) {
        this.mask = mask;
        this.max_lenght = 0;
        this.control = control;
    }

    public InputMaskChecker(String mask, int max_lenght, TextField control) {
        this.mask = mask;
        this.max_lenght = max_lenght;
        this.control = control;
    }


    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        erroneous.setValue(!newValue.matches(mask) || ((max_lenght > 0) ? newValue.length() > max_lenght : false) || newValue.length() == 0);
        control.setStyle( erroneous.get() ? STYLE : "-fx-effect: null;");
    }
}