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

package com.rvantwisk.cnctools.controls;

import com.rvantwisk.cnctools.data.PostProcessorConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by rvt on 12/27/13.
 */
public class PostProcessorControl extends AnchorPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField axisA;

    @FXML
    private TextField axisB;

    @FXML
    private TextField axisC;

    @FXML
    private TextField axisU;

    @FXML
    private TextField axisV;

    @FXML
    private TextField axisW;

    @FXML
    private TextField axisX;

    @FXML
    private TextField axisY;

    @FXML
    private TextField axisZ;

    @FXML
    private TextField decA;

    @FXML
    private TextField decB;

    @FXML
    private TextField decC;

    @FXML
    private TextField decF;

    @FXML
    private TextField decOther;

    @FXML
    private TextField decS;

    @FXML
    private TextField decU;

    @FXML
    private TextField decV;

    @FXML
    private TextField decW;

    @FXML
    private TextField decX;

    @FXML
    private TextField decY;

    @FXML
    private TextField decZ;

    @FXML
    private TextField name;

    @FXML
    private TextArea preamble;

    @FXML
    private TextArea postamble;

    @FXML
    private CheckBox toolchange;


    @FXML
    void initialize() {
        assert axisA != null : "fx:id=\"axisA\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisB != null : "fx:id=\"axisB\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisC != null : "fx:id=\"axisC\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisU != null : "fx:id=\"axisU\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisV != null : "fx:id=\"axisV\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisW != null : "fx:id=\"axisW\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisX != null : "fx:id=\"axisX\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisY != null : "fx:id=\"axisY\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert axisZ != null : "fx:id=\"axisZ\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decA != null : "fx:id=\"decA\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decB != null : "fx:id=\"decB\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decC != null : "fx:id=\"decC\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decF != null : "fx:id=\"decF\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decOther != null : "fx:id=\"decOther\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decS != null : "fx:id=\"decS\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decU != null : "fx:id=\"decU\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decV != null : "fx:id=\"decV\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decW != null : "fx:id=\"decW\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decX != null : "fx:id=\"decX\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decY != null : "fx:id=\"decY\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert decZ != null : "fx:id=\"decZ\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert name != null : "fx:id=\"name\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
        assert toolchange != null : "fx:id=\"toolchange\" was not injected: check your FXML file 'PostProcessorConfig.fxml'.";
    }

    private PostProcessorConfig data;

    public PostProcessorControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PostProcessorConfig.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setData(PostProcessorConfig data) {
        this.data = data;

        if (data ==null) return;

        name.setText(data.getName());

        decF.setText(data.decimalsFProperty().getValue().toString());
        decS.setText(data.decimalsSProperty().getValue().toString());
        decOther.setText(data.decimalsOthersProperty().getValue().toString());

        toolchange.selectedProperty().set(data.hasToolChangerProperty().get());

        decA.setText(data.axisDecimalsProperty().get("A").toString());
        decB.setText(data.axisDecimalsProperty().get("B").toString());
        decC.setText(data.axisDecimalsProperty().get("C").toString());
        decU.setText(data.axisDecimalsProperty().get("U").toString());
        decV.setText(data.axisDecimalsProperty().get("V").toString());
        decW.setText(data.axisDecimalsProperty().get("W").toString());
        decX.setText(data.axisDecimalsProperty().get("X").toString());
        decY.setText(data.axisDecimalsProperty().get("Y").toString());
        decZ.setText(data.axisDecimalsProperty().get("Z").toString());

        axisA.setText(data.axisMappingProperty().get("A"));
        axisB.setText(data.axisMappingProperty().get("B"));
        axisC.setText(data.axisMappingProperty().get("C"));
        axisU.setText(data.axisMappingProperty().get("U"));
        axisV.setText(data.axisMappingProperty().get("V"));
        axisW.setText(data.axisMappingProperty().get("W"));
        axisX.setText(data.axisMappingProperty().get("X"));
        axisY.setText(data.axisMappingProperty().get("Y"));
        axisZ.setText(data.axisMappingProperty().get("Z"));

        preamble.setText(data.getPreabmle());
        postamble.setText(data.getPostamble());
    }

    public PostProcessorConfig getData() {
        applyData();

        return data;
    }


    public void applyData() {
        data.setName(name.getText());

        data.setDecimalsF(Integer.valueOf(decF.getText()));
        data.setDecimalsS(Integer.valueOf(decS.getText()));
        data.setDecimalsOthers(Integer.valueOf(decOther.getText()));

        data.setHasToolChanger(toolchange.selectedProperty().get());

        data.axisDecimalsProperty().put("A", Integer.valueOf(decA.getText()));
        data.axisDecimalsProperty().put("B", Integer.valueOf(decB.getText()));
        data.axisDecimalsProperty().put("C", Integer.valueOf(decC.getText()));
        data.axisDecimalsProperty().put("U", Integer.valueOf(decU.getText()));
        data.axisDecimalsProperty().put("V", Integer.valueOf(decV.getText()));
        data.axisDecimalsProperty().put("W", Integer.valueOf(decW.getText()));
        data.axisDecimalsProperty().put("X", Integer.valueOf(decX.getText()));
        data.axisDecimalsProperty().put("Y", Integer.valueOf(decY.getText()));
        data.axisDecimalsProperty().put("Z", Integer.valueOf(decZ.getText()));

        data.axisMappingProperty().put("A", axisA.getText());
        data.axisMappingProperty().put("B", axisB.getText());
        data.axisMappingProperty().put("C", axisC.getText());
        data.axisMappingProperty().put("U", axisU.getText());
        data.axisMappingProperty().put("V", axisV.getText());
        data.axisMappingProperty().put("W", axisW.getText());
        data.axisMappingProperty().put("X", axisX.getText());
        data.axisMappingProperty().put("Y", axisY.getText());
        data.axisMappingProperty().put("Z", axisZ.getText());

        data.preabmleProperty().set(preamble.getText());
        data.postambleProperty().set(postamble.getText());
    }
}
