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

import com.rvantwisk.cnctools.data.AvailableTask;
import com.rvantwisk.cnctools.data.Project;
import com.rvantwisk.cnctools.data.Task;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/7/13
 * Time: 8:59 PM
 */
public class DialogBuilder {

    final private Stage primaryStage;
    final private ApplicationContext context;

    public DialogBuilder(Stage primaryStage, ApplicationContext context) {
        this.primaryStage = primaryStage;
        this.context = context;
    }

    Map<String, Boolean> registeredbeans = new HashMap<>();

    public ObservableList<AvailableTask> getApplicapableMillTasks() {
        ObservableList<AvailableTask> allOperations = FXCollections.observableArrayList();

        //THis is currently hard coded, but needs to move into a register base
        allOperations.add(new AvailableTask("Create round stock", "Take's from a square material round stock on your indexer.", "com.rvantwisk.cnctools.operations.createRoundStock.Controller", "CreateRoundStock.fxml"));
        allOperations.add(new AvailableTask("Custom G-Code", "Let's you create your own G-Code.", "com.rvantwisk.cnctools.operations.customgcode.Controller", "CustomGCode.fxml"));
        return allOperations;
    }


    /**
     * Add's a new bean to Spring's context
     * @param name
     */
    private void registerBean (final String name) {
        if (registeredbeans.containsKey(name)) {
            return;
        }
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(name);
        beanDefinition.setAutowireCandidate(true);
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        registry.registerBeanDefinition(name, beanDefinition);
        factory.autowireBeanProperties(this,
                AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        registeredbeans.put(name, Boolean.TRUE);
    }

    /**
     * Create a new OPeration's dialog
     * @param project
     * @param toolDBManager
     * @param task
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public FXMLDialog getOperationDialog(final Project project, final ToolDBManager toolDBManager, final Task task) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        registerBean(task.getClassName());
        MillTaskController instance = (MillTaskController) context.getBean(task.getClassName());

        // Init dialog with needed parameters so it can operate on this project
        instance.setTask(task);
        instance.setProject(project);
        instance.setToolDBManager(toolDBManager);

        // get the resource path of the main class for this MillTask
        List<String> path = new ArrayList<>(Arrays.asList(task.getClassName().split("\\.")));
        path.remove(path.size() - 1);

        // get the fxml location
        final String tpackage = "/" + StringUtils.join(path, "/") + "/" + task.getFxmlFileName();
        if (tpackage.contains("../") || tpackage.contains("http:") || tpackage.contains("https:")) {
            throw new RuntimeException("Resource for ApplicapableMillTask with name ["+ task.getName()+"] is invalid.");
        }

        return new FXMLDialog(instance, getClass().getResource(tpackage), primaryStage);
    }






}
