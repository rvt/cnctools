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

package com.rvantwisk.cnctools.opengl;

/**
 * Created by rvt on 1/10/14.
 */
public class ReadonlyCamera {
    protected float width;
    protected float height;
    protected float offset_x;
    protected float offset_y;
    protected float x;
    protected float y;
    protected float z;
    protected float zoom_factor;
    protected float elevation;
    protected float azimuth;
    protected float ZOOM_MIN;
    protected float ZOOM_MAX;
    protected float FOVY;
    protected float ZOOM_ORTHO_ADJ;
    protected boolean isOrtho;

    public ReadonlyCamera() {
        ZOOM_MIN = 0.01f;
        ZOOM_MAX = 800.0f;

        width = 0.0f;
        height = 180.0f;

        x = 0.0f;
        y = 180.0f;
        offset_x = 0;
        offset_y = 0;
        z = -20f;
        FOVY = 80.0f;
        ZOOM_ORTHO_ADJ = 4.5f;
        zoom_factor = 1.0f;
        elevation = 0.0f;
        azimuth = -20f;

        isOrtho = false;
    }

    public ReadonlyCamera(float width, float height, float offset_x, float offset_y, float x, float y, float z, float zoom_factor, float elevation, float azimuth, float ZOOM_MIN, float ZOOM_MAX, float FOVY, float ZOOM_ORTHO_ADJ, float NEAR, float FAR, boolean isOrtho) {
        this.width = width;
        this.height = height;
        this.offset_x = offset_x;
        this.offset_y = offset_y;
        this.x = x;
        this.y = y;
        this.z = z;
        this.zoom_factor = zoom_factor;
        this.elevation = elevation;
        this.azimuth = azimuth;
        this.ZOOM_MIN = ZOOM_MIN;
        this.ZOOM_MAX = ZOOM_MAX;
        this.FOVY = FOVY;
        this.ZOOM_ORTHO_ADJ = ZOOM_ORTHO_ADJ;
        this.isOrtho = isOrtho;
    }

    public ReadonlyCamera(ReadonlyCamera other) {
        this.width = other.width;
        this.height = other.height;
        this.offset_x = other.offset_x;
        this.offset_y = other.offset_y;
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.zoom_factor = other.zoom_factor;
        this.elevation = other.elevation;
        this.azimuth = other.azimuth;
        this.ZOOM_MIN = other.ZOOM_MIN;
        this.ZOOM_MAX = other.ZOOM_MAX;
        this.FOVY = other.FOVY;
        this.ZOOM_ORTHO_ADJ = other.ZOOM_ORTHO_ADJ;
        this.isOrtho = other.isOrtho;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getOffset_x() {
        return offset_x;
    }

    public float getOffset_y() {
        return offset_y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getZoom_factor() {
        return zoom_factor;
    }

    public float getElevation() {
        return elevation;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getZOOM_MIN() {
        return ZOOM_MIN;
    }

    public float getZOOM_MAX() {
        return ZOOM_MAX;
    }

    public float getFOVY() {
        return FOVY;
    }

    public float getZOOM_ORTHO_ADJ() {
        return ZOOM_ORTHO_ADJ;
    }

    public boolean isOrtho() {
        return isOrtho;
    }

    public void setOrtho(boolean isOrtho) {
        if (isOrtho) {
            x = width / 2.0f;
            y = height / 2.0f;
        }
        this.isOrtho = isOrtho;
    }
}
