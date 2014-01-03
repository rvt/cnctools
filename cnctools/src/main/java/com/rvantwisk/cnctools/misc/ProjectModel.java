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

import com.dooapp.xstreamfx.*;
import com.rvantwisk.cnctools.data.*;
import com.rvantwisk.cnctools.data.tools.BallMill;
import com.rvantwisk.cnctools.data.tools.EndMill;
import com.sun.javafx.collections.ObservableListWrapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/6/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProjectModel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String SEPARATOR = System.getProperty("line.separator");

    private static final String PROJECTS_XML = "projects.xml";
    private static final String TOOLS_XML = "tools.xml";
    private static final String POSTPROCESSORS_XML = "postprocessors.xml";
    final private ObservableList<Project> projectsProperty = FXCollections.observableArrayList();
    final private ObservableList<ToolParameter> toolDBProperty = FXCollections.observableArrayList();
    final private ObservableList<CNCToolsPostProcessConfig> postProcessors = FXCollections.observableArrayList();

    private transient ToolDBManager toolDBManager;

    public ToolDBManager getToolDBManager() {
        if (toolDBManager==null) {
            toolDBManager = new ToolDBManager(toolDBProperty);
        }
        return toolDBManager;
    }

    public ObservableList<Project> projectsProperty() {
        return projectsProperty;
    }

    public ObservableList<ToolParameter> toolDBProperty() {
        return toolDBProperty;
    }
    public ObservableList<CNCToolsPostProcessConfig> postProcessorsProperty() {
        return postProcessors;
    }

    public void addProject(String projectname, String description) {
        Project project = new Project(projectname, description);
        projectsProperty.add(project);
    }

    public void addProject(final Project p) {
        projectsProperty.add(p);
    }

    private static Converter lookupTypeConverter(XStream xStream, Class clazz) {
        return xStream.getConverterLookup().lookupConverterForType(clazz);
    }

    private static XStream getXStream() {

        XStream xStream = new XStream(new XppDomDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                CdataWrapper cdw = new CdataWrapper(out);
                cdw.setToWrap(new String[]{"gcode"});
                return cdw;
            }
        }
        );

        xStream.registerConverter(new StringPropertyConverter(xStream.getMapper()));
        xStream.registerConverter(new BooleanPropertyConverter(xStream.getMapper()));
//        xStream.registerConverter(new ObjectPropertyConverter(xStream.getMapper()));
        xStream.registerConverter(new DoublePropertyConverter(xStream.getMapper()));
        xStream.registerConverter(new LongPropertyConverter(xStream.getMapper()));
        xStream.registerConverter(new IntegerPropertyConverter(xStream.getMapper()));
//        xStream.registerConverter(new ObservableListConverter(xStream.getMapper()));

//        xStream.registerConverter(new ConverterWrapper(lookupTypeConverter(xStream, List.class), ObservableListWrapper.class));
//        xStream.registerConverter(new ConverterWrapper(lookupTypeConverter(xStream, List.class), ObservableList.class));
//        xStream.registerConverter(new ConverterWrapper(lookupTypeConverter(xStream, Map.class), ObservableMap.class));


        xStream.omitField(ObservableListWrapper.class, "listenerHelper");

    //    FXConverters.configure(xstream);

        // JavaFX aliases
        xStream.alias("IntProp", SimpleIntegerProperty.class);
        xStream.alias("StrProp", SimpleStringProperty.class);
        xStream.alias("DblProp", SimpleDoubleProperty.class);
        xStream.alias("Boolprop", SimpleBooleanProperty.class);
        xStream.alias("ObjProp", SimpleObjectProperty.class);
        xStream.alias("LongProp", SimpleLongProperty.class);
        xStream.alias("OListWrapper", ObservableListWrapper.class);

        // Program properties aliases
        xStream.alias("Task", Task.class);
        xStream.alias("Project", Project.class);
        xStream.alias("StockToolParameter", StockToolParameter.class);
        xStream.alias("EndMill", EndMill.class);
        xStream.alias("BallMill", BallMill.class);
        xStream.alias("AvailableTask", TaskTemplate.class);
        xStream.alias("ToolParameter", ToolParameter.class);

        String contents = "<tag>my data conents</tag>";
        String xml = "              <gcode class=\"StrProp\"><![CDATA[asd\n" +
                "asd\n" +
                "asd\n" +
                "]]></gcode>";
        SimpleStringProperty results = (SimpleStringProperty) xStream.fromXML(xml);

        return xStream;
    }

    public void saveProjects() {
        XStream xstream = getXStream();

        File file = new File(PROJECTS_XML);
        try {
            String xml = xstream.toXML(new ArrayList<>(projectsProperty));
            FileUtil.saveFile(xml, file);
        } catch (Exception e) { // catches ANY exception
            logger.error("saveProjects", e);
        }
    }

    public void saveToolDB() {
        XStream xstream = getXStream();
        File file = new File(TOOLS_XML);
        try {
            String xml = xstream.toXML(new ArrayList<>(toolDBProperty));
            FileUtil.saveFile(xml, file);
        } catch (Exception e) { // catches ANY exception
            logger.error("saveToolDB", e);
        }
    }

    public void savePostProcessors() {
        XStream xstream = getXStream();
        File file = new File(POSTPROCESSORS_XML);
        try {
            String xml = xstream.toXML(new ArrayList<>(postProcessors));
            FileUtil.saveFile(xml, file);
        } catch (Exception e) { // catches ANY exception
            logger.error("savePostProcessors", e);
        }
    }


    public void loadToolsFromDB () {
        XStream xstream = getXStream();
        try {
            File file = new File(TOOLS_XML);
            ArrayList<ToolParameter> tools = (ArrayList<ToolParameter>) xstream.fromXML(readFileIntoString(file).toString());
            toolDBProperty.clear();
            toolDBProperty.addAll(tools);
        } catch (Exception e) { // catches ANY exception
            logger.error("Error loading tools DB", e);
        }
    }

    public void loadProjectsFromDB() {
        XStream xstream = getXStream();
        try {

            File file = new File(PROJECTS_XML);
            ArrayList<Project> projects = (ArrayList<Project>) xstream.fromXML(readFileIntoString(file).toString());
            projectsProperty.clear();
            projectsProperty.addAll(projects);
        } catch (Exception e) { // catches ANY exception
            logger.error("Error tools projects DB", e);
        }
    }

    public void loadPostProcessors() {
        XStream xstream = getXStream();
        try {

            File file = new File(POSTPROCESSORS_XML);
            ArrayList<CNCToolsPostProcessConfig> data = (ArrayList<CNCToolsPostProcessConfig>) xstream.fromXML(readFileIntoString(file).toString());
            postProcessors.clear();
            postProcessors.addAll(data);
        } catch (Exception e) { // catches ANY exception
            logger.error("Error loading post processors from DB", e);
        }
    }

    /**
     * Read a file into a String
     * @param file
     * @return
     */
    public static String readFileIntoString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath())))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine).append(SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     * Create a deep copy of a java bean
     * WARNING: Unesure you are doing this on beans only!
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends Object> T deepCopy(final Object obj){
        return (T)getXStream().fromXML(getXStream().toXML(obj));
    }


}