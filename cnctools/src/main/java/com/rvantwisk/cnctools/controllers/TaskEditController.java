/*
 * Copyright (c) 2014, R. van Twisk
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

import com.rvantwisk.cnctools.data.Project;
import com.rvantwisk.cnctools.data.TaskRunnable;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.misc.AbstractController;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by rvt on 1/2/14.
 */
public class TaskEditController extends AbstractController {

    enum ViewAs {
        CLOSE,
        CANCELSAVE
    }

    private MillTaskController tasksController;
    private TaskRunnable currentTaskRunnable;
    private ViewAs viewAs;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private AnchorPane taskPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label lbHeader;


    @FXML
    void onCancel(ActionEvent event) {
        tasksController.destroy();
        setReturned(Result.CANCEL);
        getDialog().close();
    }

    @FXML
    void onClose(ActionEvent event) {
        tasksController.destroy();
        setReturned(Result.CLOSE);
        getDialog().close();
    }

    @FXML
    void onSave(ActionEvent event) {
        tasksController.destroy();
        setReturned(Result.SAVE);
        getDialog().close();
    }

    @FXML
    void initialize() {
        assert btnCancel != null : "fx:id=\"btnCancel\" was not injected: check your FXML file 'TaskEdit.fxml'.";
        assert btnSave != null : "fx:id=\"btnSave\" was not injected: check your FXML file 'TaskEdit.fxml'.";
        assert lbHeader != null : "fx:id=\"lbHeader\" was not injected: check your FXML file 'TaskEdit.fxml'.";

        setViewAs(ViewAs.CANCELSAVE);
    }

    public void setTask(final Project project, final TaskRunnable taskRunnable) {
        currentTaskRunnable = taskRunnable;
        tasksController = (MillTaskController) applicationContext.getBean(taskRunnable.getClassName());

        // Ensure we supply a non-null model to the operation
        if (taskRunnable.getMilltaskModel()==null) {
            TaskModel m = tasksController.createNewModel();
            taskRunnable.setMilltaskModel(m);
            tasksController.setModel(m);
        } else {
            tasksController.setModel(taskRunnable.getMilltaskModel());
        }
        tasksController.setProject(project);

        // get the resource path of the main class for this MillTask
        List<String> path = new ArrayList<>(Arrays.asList(taskRunnable.getClassName().split("\\.")));
        path.remove(path.size() - 1);

        // verify the fxml location
        final String tpackage = StringUtils.join(path, "/") + "/" + taskRunnable.getFxmlFileName();
        if (tpackage.contains("../") || tpackage.contains("http:") || tpackage.contains("https:")) {
            throw new RuntimeException("Resource for ApplicapableMillTask with name [" + taskRunnable.getName() + "] is invalid.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(tasksController.getClass().getResource(taskRunnable.getFxmlFileName()));
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    return tasksController;
                }
            });

            lbHeader.setText(taskRunnable.getName());
            AnchorPane aPane = (AnchorPane) loader.load();
            taskPane.getChildren().add(aPane);
            taskPane.setBottomAnchor(aPane, 0.0);
            taskPane.setTopAnchor(aPane, 0.0);
            taskPane.setRightAnchor(aPane, 0.0);
            taskPane.setLeftAnchor(aPane, 0.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TaskRunnable getTask() {
        tasksController.getModel();
        return currentTaskRunnable;
    }

    public ViewAs getViewAs() {
        return viewAs;
    }

    public void setViewAs(ViewAs viewAs) {
        if (btnClose!=null) {
            if (viewAs == ViewAs.CLOSE) {
                btnClose.setVisible(true);
                btnCancel.setVisible(false);
                btnSave.setVisible(false);
            } else {
                btnClose.setVisible(false);
                btnCancel.setVisible(true);
                btnSave.setVisible(true);
            }
        }
        this.viewAs = viewAs;
    }
}
