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

package com.rvantwisk.cnctools.controllers;

import com.rvantwisk.cnctools.controllers.interfaces.DialogController;
import com.rvantwisk.cnctools.controls.ToolParametersControl;
import com.rvantwisk.cnctools.data.StockToolParameter;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ProjectModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/10/13
 * Time: 8:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolConfigurationsController implements DialogController {

    private final StringProperty closeButtonText = new SimpleStringProperty();

    private Result returned;

    private FXMLDialog dialog;

    @Autowired
    @Qualifier("projectModel")
    private ProjectModel projectModel;

    @FXML
    private ToolParametersControl toolParameters; // if you are wondering why this controller is injected, it's becaused it's the <fx:id> name + Controller appeneded

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<ToolParameter> v_toolsList;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnCopy;
    @FXML
    private Button btnNew;
    @FXML
    private Button bntApply;


    @FXML
    void onDelete(ActionEvent event) {
        if (v_toolsList.getSelectionModel().selectedItemProperty().get() != null) {
            MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
            dialog.setTitleText("Deleting a Tool");
            dialog.setMessage("Are you sure you want to delete this tool?");
            if (dialog.showDialog() == MonologFXButton.Type.YES) {
                ToolParameter p = v_toolsList.getSelectionModel().getSelectedItem();
                projectModel.toolDBProperty().remove(p);
                v_toolsList.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML

    void onNew(ActionEvent event) {
        StockToolParameter tp = Factory.newStockTool();
        projectModel.toolDBProperty().add(tp);
        int i = projectModel.toolDBProperty().indexOf(tp);
        v_toolsList.getSelectionModel().selectIndices(i);
    }

    @FXML
    void onApply(ActionEvent event) {
        toolParameters.applyToolParameters();
    }

    @FXML
    void onCopy(ActionEvent event) {
    }

    public ToolParameter getTool() {
        return toolParameters.getTool();
    }

    @FXML
    void onClose(ActionEvent event) {
        returned = Result.DISMISS;
        dialog.close();
    }

    @FXML
    void onUse(ActionEvent event) {
        returned = Result.USEMODIFIED;
        dialog.close();
    }

    public Result getReturned() {
        return returned;
    }

    @FXML
    void initialize() {
        v_toolsList.setItems(projectModel.toolDBProperty());

        //    toolConfig.con

        toolParameters.disableProperty().bind(v_toolsList.getSelectionModel().selectedItemProperty().isNull());
        btnCopy.disableProperty().bind(v_toolsList.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(v_toolsList.getSelectionModel().selectedItemProperty().isNull());

        // When the user selects a item in the list then update the tool panel
        v_toolsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToolParameter>() {
            @Override
            public void changed(ObservableValue<? extends ToolParameter> observable, ToolParameter oldValue, ToolParameter newValue) {
                if (v_toolsList.getSelectionModel().selectedItemProperty().get() == null) {
                    toolParameters.setTool(null);
                //    toolParametersController.setTool(new ToolParameter("", ToolParameter.Units.MM, new EndMill(6.0)));
                } else {
                    toolParameters.setTool(newValue);
                }
            }
        });


        // Set text in ListView
        v_toolsList.setCellFactory(new Callback<ListView<ToolParameter>, ListCell<ToolParameter>>() {
            @Override
            public ListCell<ToolParameter> call(ListView<ToolParameter> p) {
                ListCell<ToolParameter> cell = new ListCell<ToolParameter>() {
                    @Override
                    protected void updateItem(ToolParameter t, boolean bln) {
                        super.updateItem(t, bln);
                        this.textProperty().unbind();
                        if (t != null) {
                            textProperty().bind(t.nameProperty());
                        } else {
                            textProperty().setValue("");
                        }
                    }
                };
                return cell;
            }
        });
    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    public StringProperty closeButtonTextProperty() {
        return closeButtonText;
    }

    public void setCloseButtonText(String closeButtonText) {
        this.closeButtonText.set(closeButtonText);
    }
}