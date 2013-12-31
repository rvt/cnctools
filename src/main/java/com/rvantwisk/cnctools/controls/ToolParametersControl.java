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

import com.rvantwisk.cnctools.data.BallMill;
import com.rvantwisk.cnctools.data.EndMill;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.misc.FXMLDialog;
import com.rvantwisk.cnctools.misc.InputMaskChecker;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/10/13
 * Time: 7:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolParametersControl extends AnchorPane {

    ObservableList tools = FXCollections.observableArrayList(
            "EndMill", "BallMill"
    );

    private FXMLDialog dialog;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TextField iName;
    @FXML
    private DimensionControl iDiameter;
    @FXML
    private TextField iToolnumber;
    @FXML
    private DimensionControl iAxialDepth;
    @FXML
    private DimensionControl iRadialDepth;
    @FXML
    private DimensionControl iSpindleSpeed;
    @FXML
    private DimensionControl iFeedRate;
    @FXML
    private DimensionControl iPlungeRate;
    @FXML
    private CheckBox iCoolant;
    @FXML
    private RadioButton iSPindleCW;
    @FXML
    private RadioButton iSPindleCCW;
    @FXML
    private ChoiceBox<String> ddNumFlutes;

    @FXML
    private ChoiceBox cbToolType;

    private ToolParameter tool;

    public ToolParametersControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ToolParameters.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    @FXML
    void initialize() {
        assert iDiameter != null : "fx:id=\"iDiameter\" was not injected: check your FXML file 'ToolParameters.fxml'.";
        assert iName != null : "fx:id=\"iName\" was not injected: check your FXML file 'ToolParameters.fxml'.";


        final InputMaskChecker listener1 = new InputMaskChecker(InputMaskChecker.NOTEMPTY, iName);
        // final InputMaskChecker listener2 = new InputMaskChecker(InputMaskChecker.NOTEMPTY, iDiameter);

        iName.textProperty().addListener(listener1);
        //iDiameter.textProperty().addListener(listener2);

        cbToolType.setItems(tools);

        final BooleanBinding binding = new BooleanBinding() {
            {
                //      super.bind(listener1.erroneous, listener2.erroneous);
            }

            @Override
            protected boolean computeValue() {
                return true; // (listener1.erroneous.get() || listener2.erroneous.get());
            }
        };

        binding.invalidate();

    }

    public ToolParameter getTool() {
        applyToolParameters();
        return tool;
    }

    public void setTool(ToolParameter tool) {
        if (tool==null) return;
        this.tool = tool;

        iName.textProperty().setValue(tool.getName());
        iSpindleSpeed.dimensionProperty().set(tool.spindleSpeedProperty());
        iAxialDepth.dimensionProperty().set(tool.axialDepthProperty());
        iRadialDepth.dimensionProperty().set(tool.radialDepthProperty());
        iSpindleSpeed.dimensionProperty().set(tool.spindleSpeedProperty());
        iFeedRate.dimensionProperty().set(tool.feedRateProperty());
        iPlungeRate.dimensionProperty().set(tool.plungeRateProperty());
        iCoolant.setSelected(tool.getCoolant());
        iToolnumber.textProperty().setValue(String.valueOf(tool.getToolNumber()));
        iSPindleCW.setSelected(tool.getSpindleDirection() == ToolParameter.SpindleDirection.CW);
        iSPindleCCW.setSelected(tool.getSpindleDirection() != ToolParameter.SpindleDirection.CW);

        ddNumFlutes.getSelectionModel().select(tool.getNumberOfFlutes() != null ? tool.getNumberOfFlutes().intValue() : 0);

        if (tool.getToolType() instanceof EndMill) {
            EndMill em = tool.getToolType();
            iDiameter.dimensionProperty().set(em.diameterProperty());

        } else if (tool.getToolType() instanceof BallMill) {
            BallMill em = tool.getToolType();
            iDiameter.dimensionProperty().set(em.diameterProperty());
        }
        cbToolType.getSelectionModel().select(tool.getToolType().getClass().getSimpleName());
    }

    public void applyToolParameters() {
        if (tool == null) return;

        tool.nameProperty().setValue(iName.getText());
        tool.spindleSpeedProperty().set(iSpindleSpeed.dimensionProperty());
        tool.axialDepthProperty().set(iAxialDepth.dimensionProperty());
        tool.radialDepthProperty().set(iRadialDepth.dimensionProperty());
        tool.feedRateProperty().set(iFeedRate.dimensionProperty());
        tool.plungeRateProperty().set(iPlungeRate.dimensionProperty());
        tool.setToolNumber(Integer.valueOf(iToolnumber.getText()));
        tool.setCoolant(iCoolant.isSelected());
        tool.setSpindleDirection(iSPindleCW.isSelected() ? ToolParameter.SpindleDirection.CW : ToolParameter.SpindleDirection.CCW);

        String numFlutes = ddNumFlutes.selectionModelProperty().get().getSelectedItem();
        tool.setNumberOfFlutes(StringUtils.isEmpty(numFlutes)?null:Integer.valueOf(numFlutes));

        if (tool.getToolType() instanceof EndMill) {
            EndMill em = tool.getToolType();
            em.diameterProperty().set(iDiameter.dimensionProperty());
        } else if (tool.getToolType() instanceof BallMill) {
            BallMill em = tool.getToolType();
            em.diameterProperty().set(iDiameter.dimensionProperty());
        }

    }

}
