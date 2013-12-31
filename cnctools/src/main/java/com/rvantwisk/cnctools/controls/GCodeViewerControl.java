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

import com.rvantwisk.cnctools.controls.opengl.GCodeRender;
import com.rvantwisk.cnctools.controls.opengl.OpenGLMachineImpl;
import com.rvantwisk.cnctools.controls.opengl.OpenGLMachineValidator;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.opengl.OpenGLImage;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * Created by rvt on 12/6/13.
 */
public class GCodeViewerControl extends AnchorPane {

    private final double MOUSE_SENSETIVITY = 1.0;

    @FXML
    private OpenGLImage openGLImage;


    double lastMouseX = 0.0, lastMouseY = 0.0;

    private GCodeRender gCodeRender;

    static class Messages {
        private Vector messages = new Vector();
        static final int MAXQUEUE = 5;

        private synchronized void putMessage(Object msg)
                throws InterruptedException {

            while (messages.size() == MAXQUEUE)
                wait();
            messages.addElement(msg);
            notify();
        }

        public synchronized Object getMessage()
                throws InterruptedException {
            notify();
            while (messages.size() == 0)
                wait();
            Object message = (Object) messages.firstElement();
            messages.removeElement(message);
            return message;
        }

    }

    public GCodeViewerControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GCodeViewer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void show(final InputStream in) {
        stop();

        new Thread("GCode Render") {
            public void run() {
                gCodeRender = new GCodeRender(openGLImage.getReadHandler());
                gCodeRender.run();

            }
        }.start();

    }

    @FXML
    void initialize() throws IOException {
        openGLImage.fitWidthProperty().bind(this.widthProperty());
        openGLImage.fitHeightProperty().bind(this.heightProperty());

        show(null);
        while (gCodeRender == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while waiting for renderer to come alive");
            }
        }


        this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lastMouseX = mouseEvent.getX();
                lastMouseY = mouseEvent.getY();

            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double dX = mouseEvent.getX() - lastMouseX;
                double dY = mouseEvent.getY() - lastMouseY;

                if (mouseEvent.isPrimaryButtonDown()) {
                    gCodeRender.setCamera(gCodeRender.getCamera().rotateView(dX / (MOUSE_SENSETIVITY * openGLImage.getFitWidth()), dY / (MOUSE_SENSETIVITY * openGLImage.getFitHeight())));
                } else if (mouseEvent.isSecondaryButtonDown()) {
                    gCodeRender.setCamera(gCodeRender.getCamera().panView(dX / (MOUSE_SENSETIVITY * openGLImage.getFitWidth()), dY / (MOUSE_SENSETIVITY * openGLImage.getFitHeight()), openGLImage.getFitWidth(), openGLImage.getFitHeight()));
                }

                lastMouseX = mouseEvent.getX();
                lastMouseY = mouseEvent.getY();
            }
        });
        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (!event.isInertia()) {
                    gCodeRender.setCamera(gCodeRender.getCamera().zoomView((int) event.getDeltaY() / (MOUSE_SENSETIVITY * openGLImage.getFitHeight())));
                }
                event.consume();
            }
        });

    }

    public void load(final InputStream in) throws SimException {

        // If we decide to have other implementation of a OPenGL machine to view we can create them here, or even pass then in load, or make a configuration
        gCodeRender.load(new OpenGLMachineImpl(), new OpenGLMachineValidator(), in);
    }

    public void finalize() {
        if (gCodeRender!=null)
            gCodeRender.stop();
    }

    public void stop() {
        if (gCodeRender!=null)
        gCodeRender.stop();
    }


}