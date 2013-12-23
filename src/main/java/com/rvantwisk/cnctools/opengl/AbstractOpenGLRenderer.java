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

package com.rvantwisk.cnctools.opengl;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.stream.RenderStream;
import org.lwjgl.util.stream.StreamHandler;
import org.lwjgl.util.stream.StreamUtil;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.opengl.AMDDebugOutput.glDebugMessageCallbackAMD;
import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageCallbackARB;

/**
 * Abstract class to simple create openGL rendering screens in javafx
 * This wasn't design to get you the optimal speed, but it's here to make it easer
 * to implement openGL views in JavaFX
 */
public abstract class AbstractOpenGLRenderer {

    private final ConcurrentLinkedQueue<Runnable> pendingRunnables;

    private final CountDownLatch runningLatch = new CountDownLatch(1);

    private final Pbuffer pbuffer;

    private final ReadOnlyIntegerWrapper fps;

    private StreamUtil.RenderStreamFactory renderStreamFactory;
    private RenderStream renderStream;
    private int transfersToBuffer = 3;

    private boolean vsync = false;

    private int samples = 4;

    public AbstractOpenGLRenderer(final StreamHandler readHandler) {
        this.pendingRunnables = new ConcurrentLinkedQueue<Runnable>();

        this.fps = new ReadOnlyIntegerWrapper(this, "fps", 0);

        if ((Pbuffer.getCapabilities() & Pbuffer.PBUFFER_SUPPORTED) == 0)
            throw new UnsupportedOperationException("Support for pbuffers is required.");

        try {
            pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
            pbuffer.makeCurrent();
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }

        final ContextCapabilities caps = GLContext.getCapabilities();
        if (caps.GL_ARB_debug_output) {
            glDebugMessageCallbackARB(new ARBDebugOutputCallback());
        } else if (caps.GL_AMD_debug_output) {
            glDebugMessageCallbackAMD(new AMDDebugOutputCallback());
        }

        this.renderStreamFactory = StreamUtil.getRenderStreamImplementation();
        this.renderStream = renderStreamFactory.create(readHandler, 1, transfersToBuffer);
    }


    public void setTransfersToBuffer(final int transfersToBuffer) {
        if (this.transfersToBuffer == transfersToBuffer)
            return;

        this.transfersToBuffer = transfersToBuffer;
        resetStreams();
    }

    public void setSamples(final int samples) {
        if (this.samples == samples)
            return;

        this.samples = samples;
        resetStreams();
    }

    private void resetStreams() {
        pendingRunnables.offer(new Runnable() {
            public void run() {
                renderStream.destroy();

                renderStream = renderStreamFactory.create(renderStream.getHandler(), samples, transfersToBuffer);
            }
        });
    }


    private void drainPendingActionsQueue() {
        Runnable runnable;

        while ((runnable = pendingRunnables.poll()) != null)
            runnable.run();
    }

    private void _init() {
        init();
    }

    /**
     * Main rendering loop
     *
     * @param running
     */
    protected void _loop(final CountDownLatch running) {
        final long FPS_UPD_INTERVAL = 1 * (1000L * 1000L * 1000L);

        long nextFPSUpdateTime = System.nanoTime() + FPS_UPD_INTERVAL;
        int frames = 0;

        long lastTime = System.nanoTime();
        double timeDelta = 0.0;

        while (0 < running.getCount()) {
            drainPendingActionsQueue();
            renderStream.bind();

            loop();

            renderStream.swapBuffers();

            if (isVsync())
                Display.sync(10); // run at 10 fps

            final long currentTime = System.nanoTime();
            timeDelta = (currentTime - lastTime) / 1000000.0;
            lastTime = currentTime;

            frames++;
            if (nextFPSUpdateTime <= currentTime) {
                long timeUsed = FPS_UPD_INTERVAL + (currentTime - nextFPSUpdateTime);
                nextFPSUpdateTime = currentTime + FPS_UPD_INTERVAL;

                final int fpsAverage = (int) (frames * (1000L * 1000L * 1000L) / (timeUsed));
                Platform.runLater(new Runnable() {
                    public void run() {
                        fps.set(fpsAverage);
                    }
                });
                frames = 0;
            }
        }
    }


    /**
     * Destroys the OpenGL render context
     */
    private void _destroy() {
        destroygl();
        renderStream.destroy();
        pbuffer.destroy();
    }


    /**
     * Main execution loop
     */
    public void run() {
        _init();
        _loop(runningLatch);
        _destroy();
    }

    public ReadOnlyIntegerProperty fpsProperty() {
        return fps.getReadOnlyProperty();
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public void stop() {
        runningLatch.countDown();
    }


    /**
     * called when the openGL context need to be initialised
     */
    protected abstract void init();

    /**
     * Called for each OpenGL loop
     */
    protected abstract void loop();

    /**
     * Called when the OpenGL context need to be destroyed
     */
    protected abstract void destroygl();

    public RenderStream getRenderStream() {
        return renderStream;
    }
}
