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
 * Created by rvt on 1/19/14.
 */
public class GLText {


/*
    public class SpinningTextureCube {
        long lastFrame;
        int fps;
        long lastFPS;
        int windowWidth = 800;
        int windowHeight = 600;
        float xRotation = 0f;
        float yRotation = 0f;
        IntBuffer cubeTextureIDs = BufferUtils.createIntBuffer(6);
        int textureWidth = 200;
        int textureHeight = 200;
        ByteBuffer [] textureBuffers;

        public void start() {

            try {
                Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
                Display.create();
            } catch (LWJGLException e) {
                e.printStackTrace();
                System.exit(0);
            }
            initGL(); // init OpenGL
            getDelta(); // call once before loop to initialise lastFrame
            lastFPS = getTime();

            while (!Display.isCloseRequested()) {
                int delta = getDelta();

                update(delta);
                renderGL();

                Display.update();
                Display.sync(60); // cap fps to 60fps
            }

            Display.destroy();
        }


        public void renderGL() {
            // Clear The Screen And The Depth Buffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            // R,G,B,A Set The Color To Blue One Time Only
            GL11.glColor3f(0.5f, 0.5f, 1.0f);

            // draw quad
            GL11.glPushMatrix();
            GL11.glRotatef(xRotation, 1, 0, 0);
            GL11.glRotatef(yRotation, 0, 1, 0);

            // top
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(0));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-1, -1, 1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(1, -1, 1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(1, 1, 1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-1, 1, 1);
            GL11.glEnd();
            //left
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(1));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-1, -1, -1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-1, -1, 1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-1, 1, 1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-1, 1, -1);
            GL11.glEnd();
            //right
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(2));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(1, -1, -1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(1, 1, -1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(1, 1, 1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(1, -1, 1);
            GL11.glEnd();
            //front
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(3));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-1, -1, -1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(1, -1, -1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(1, -1, 1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-1, -1, 1);
            GL11.glEnd();
            //back
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(4));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(1, 1, -1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-1, 1, -1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-1, 1, 1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(1, 1, 1);
            GL11.glEnd();
            //bottom
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(5));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(1, -1, -1);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-1, -1, -1);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-1, 1, -1);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(1, 1, -1);
            GL11.glEnd();

            GL11.glPopMatrix();
        }
        public void update(int delta) {
            xRotation += 0.01 * delta;
            if (xRotation > 360f)
                xRotation = 0;
            yRotation += 0.02 * delta;
            if (yRotation > 360f)
                yRotation = 0;
            createTextures();
            updateFPS(); // update FPS Counter
        }
        public int getDelta() {
            long time = getTime();
            int delta = (int) (time - lastFrame);
            lastFrame = time;

            return delta;
        }
        public long getTime() {
            return (Sys.getTime() * 1000) / Sys.getTimerResolution();
        }
        public void updateFPS() {
            if (getTime() - lastFPS > 1000) {
                Display.setTitle("FPS: " + fps);
                fps = 0;
                lastFPS += 1000;
            }
            fps++;
        }
        public ByteBuffer createTimeTexture(int side)
        {
            String title = "";
            switch (side)
            {
                case 0:
                    title = "top";
                    break;
                case 1:
                    title = "left";
                    break;
                case 2:
                    title = "right";
                    break;
                case 3:
                    title = "front";
                    break;
                case 4:
                    title = "back";
                    break;
                case 5:
                    title = "bottom";
                    break;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            String dateString = sdf.format(Calendar.getInstance().getTime());

            ByteBuffer bb = ByteBuffer.allocateDirect(textureHeight * textureWidth * 3);
            BufferedImage bi = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = bi.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, textureWidth - 1, textureHeight - 1);
            g.setColor(new Color(0f, 0.3f, 0.8f));
            g.drawRect(0, 0, textureWidth - 1, textureHeight - 1);
            Font font = new Font("Dialog", Font.BOLD, 12);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(font);
            g.drawString(title, 5, 20);
            g.drawString(dateString, 5, 40);
            byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            bb.order(ByteOrder.nativeOrder());
            bb.put(data, 0, data.length);
            bb.flip();
            return bb;
        }
        public void createTextures()
        {
            byte [] rawData = new byte[textureHeight * textureWidth * 3];
            Random rand = new Random();
            textureBuffers = new ByteBuffer[cubeTextureIDs.limit()];
            for (int i = 0; i < cubeTextureIDs.limit(); i++)
            {
                textureBuffers[i] = createTimeTexture(i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, cubeTextureIDs.get(i));
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, textureWidth, textureHeight, 0,
                        GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffers[i]);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
            }
        }
        public void initGL() {
            GL11.glViewport(0, 0, windowWidth, windowHeight);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GLU.gluPerspective(	60.0f, (float)windowWidth/(float)windowHeight, 1f, 30f);
            GL11.glTranslatef(0, 0, -5f);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            GL11.glClearColor(0, 0, 0, 0);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            GL11.glGenTextures(cubeTextureIDs);
            createTextures();
        }
        public static void main(String [] args)
        {
            SpinningTextureCube lTest = new SpinningTextureCube();
            lTest.start();
        }
    }
    */
}
