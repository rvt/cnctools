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
import com.rvantwisk.cnctools.data.AvailableTask;
import com.rvantwisk.cnctools.data.Task;
import com.rvantwisk.cnctools.data.Project;
import com.rvantwisk.cnctools.misc.DialogBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/6/13
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddMillTaskController extends DialogController {

    @Autowired
    private DialogBuilder dialogBuilder;

    @FXML TableView tbl_assignedMillTasks;
    @FXML TextField tv_taskName;
    @FXML Button bt_add;

    private Project currentProject;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        tbl_assignedMillTasks.setItems(dialogBuilder.getApplicapableMillTasks());
        tv_taskName.setText("");
        bt_add.disableProperty().bind(tbl_assignedMillTasks.getSelectionModel().selectedItemProperty().isNull());

    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    @FXML
    public void add() {
        if (tbl_assignedMillTasks.getSelectionModel().selectedItemProperty().get()!=null) {
            AvailableTask mt = (AvailableTask)tbl_assignedMillTasks.getSelectionModel().getSelectedItem();
            currentProject.millTasksProperty().add(new Task(tv_taskName.getText(), mt.getDescription(), mt.getClassName(), mt.getFxmlFileName()));
            getDialog().close();
        }
    }

    @FXML
    public void cancel() {
        getDialog().close();
    }
}
