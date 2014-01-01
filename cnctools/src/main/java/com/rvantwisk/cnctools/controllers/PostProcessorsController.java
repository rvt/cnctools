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

import com.rvantwisk.cnctools.data.CNCToolsPostProcessConfig;
import com.rvantwisk.cnctools.misc.AbstractController;
import com.rvantwisk.cnctools.controls.PostProcessorControl;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ProjectModel;
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

/**
 * Created by rvt on 12/27/13.
 */
public class PostProcessorsController extends AbstractController {
    private Mode mode;

    private CNCToolsPostProcessConfig postProcessConfig = null;


    @FXML
    private Button btnUse;
    @FXML
    private Button btnCopy;
    @FXML
    private Button btnDelete;

    public enum Mode {
        EDIT, // Mode to just close teh dialog box
        SELECT // Mode to show a close button and a 'USE' button
    }
    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.SELECT) {
            btnUse.setVisible(true);
        } else {
            btnUse.setVisible(false);
        }
    }

    @FXML
    private ListView<CNCToolsPostProcessConfig> v_postprocessorList;

    @Autowired
    @Qualifier("projectModel")
    private ProjectModel projectModel;

    @FXML
    private PostProcessorControl postProcessor; // if you are wondering why this controller is injected, it's becaused it's the <fx:id> name + Controller appeneded

    @FXML
    public void onCopy(ActionEvent actionEvent) {

    }

    @FXML
    public void onDelete(ActionEvent actionEvent) {
        if (v_postprocessorList.getSelectionModel().selectedItemProperty().get() != null) {
            MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
            dialog.setTitleText("Deleting a postprocessor");
            dialog.setMessage("Are you sure you want to delete this post processor?");
            if (dialog.show() == MonologFXButton.Type.YES) {
                CNCToolsPostProcessConfig p = v_postprocessorList.getSelectionModel().getSelectedItem();
                projectModel.postProcessorsProperty().remove(p);
                v_postprocessorList.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    public void onUse(ActionEvent actionEvent) {
        postProcessor.applyData();
        this.postProcessConfig = postProcessor.getData();
        setReturned(Result.USE);
        getDialog().close();
    }

    @FXML
    public void onApply(ActionEvent actionEvent) {
        postProcessor.applyData();
    }

    @FXML
    void onClose(ActionEvent event) {
        setReturned(Result.DISMISS);
        getDialog().close();
    }

    @FXML
    public void onNew(ActionEvent actionEvent) {
        CNCToolsPostProcessConfig tp = Factory.newPostProcessor();
        projectModel.postProcessorsProperty().add(tp);
        int i = projectModel.postProcessorsProperty().indexOf(tp);
        v_postprocessorList.getSelectionModel().selectIndices(i);
    }

    public CNCToolsPostProcessConfig getPostProcessConfig() {
        return postProcessConfig;
    }

    public void setPostProcessConfig(CNCToolsPostProcessConfig postProcessConfig) {
        this.postProcessConfig = postProcessConfig;
    }

    @FXML
    void initialize() {
        v_postprocessorList.setItems(projectModel.postProcessorsProperty());

        if (mode == Mode.SELECT) {
            btnUse.setVisible(true);
        } else {
            btnUse.setVisible(false);
        }

        //    toolConfig.con
        postProcessor.disableProperty().bind(v_postprocessorList.getSelectionModel().selectedItemProperty().isNull());
        btnCopy.disableProperty().bind(v_postprocessorList.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(v_postprocessorList.getSelectionModel().selectedItemProperty().isNull());

        // When the user selects a item in the list then update the tool panel
        v_postprocessorList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CNCToolsPostProcessConfig>() {
            @Override
            public void changed(ObservableValue<? extends CNCToolsPostProcessConfig> observable, CNCToolsPostProcessConfig oldValue, CNCToolsPostProcessConfig newValue) {
                if (v_postprocessorList.getSelectionModel().selectedItemProperty().get() == null) {
                    postProcessor.setData(null);
                    //    toolParametersController.setTool(new ToolParameter("", ToolParameter.Units.MM, new EndMill(6.0)));
                } else {
                    postProcessor.setData(newValue);
                }
            }
        });


        // Set text in ListView
        v_postprocessorList.setCellFactory(new Callback<ListView<CNCToolsPostProcessConfig>, ListCell<CNCToolsPostProcessConfig>>() {
            @Override
            public ListCell<CNCToolsPostProcessConfig> call(ListView<CNCToolsPostProcessConfig> p) {
                ListCell<CNCToolsPostProcessConfig> cell = new ListCell<CNCToolsPostProcessConfig>() {
                    @Override
                    protected void updateItem(CNCToolsPostProcessConfig t, boolean bln) {
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
}
