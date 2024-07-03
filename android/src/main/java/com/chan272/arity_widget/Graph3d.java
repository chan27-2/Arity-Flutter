// Copyright (C) 2009-2010 Mihai Preda

package com.chan272.arity_widget;

import android.content.Context;
import android.graphics.Color;

import org.javia.arity.Function;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

class Graph3d {
    private final int N = CalculatorView.resolution3D;
    private ShortBuffer verticeIdx;
    private FloatBuffer vertexBuf;
    private ByteBuffer colorBuf;
    private int vertexVbo, colorVbo, vertexElementVbo;
    private boolean useVBO;
    private int nVertex;
    private static float centerX = 0;
    private static float centerY = 0;
    private int colorXYplane;

    public static void setCenterX(float x) {
        centerX = x;
    }

    public static void setCenterY(float y) {
        centerY = y;
    }

    Graph3d(Context context, GL11 gl) {
        colorXYplane = Util.getThemeColor(context, com.google.android.material.R.attr.colorPrimary);
        short[] b = new short[N * N];
        int p = 0;
        for (int i = 0; i < N; i++) {
            short v = 0;
            for (int j = 0; j < N; v += N + N, j += 2) {
                b[p++] = (short) (v + i);
                b[p++] = (short) (v + N + N - 1 - i);
            }
            v = (short) (N * (N - 2));
            i++;
            for (int j = N - 1; j >= 0; v -= N + N, j -= 2) {
                b[p++] = (short) (v + N + N - 1 - i);
                b[p++] = (short) (v + i);
            }
        }
        verticeIdx = buildBuffer(b);

        String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        useVBO = extensions.indexOf("vertex_buffer_object") != -1;
        CalculatorView.log("VBOs support: " + useVBO + " version " + gl.glGetString(GL10.GL_VERSION));

        if (useVBO) {
            int[] out = new int[3];
            gl.glGenBuffers(3, out, 0);
            vertexVbo = out[0];
            colorVbo = out[1];
            vertexElementVbo = out[2];
        }
    }

    private static FloatBuffer buildBuffer(float[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 2);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer sb = bb.asFloatBuffer();
        sb.put(b);
        sb.position(0);
        return sb;
    }

    private static ShortBuffer buildBuffer(short[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 1);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(b);
        sb.position(0);
        return sb;
    }

    private static ByteBuffer buildBuffer(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 1);
        bb.order(ByteOrder.nativeOrder());
        bb.put(b);
        bb.position(0);
        return bb;
    }

    public void update(GL11 gl, Function f, float zoom) {
        final int NTICK = CalculatorView.useSmoothShading3D ? 5 : 0;
        final float size = 4 * zoom;
        final float minX = -size, maxX = size, minY = -size, maxY = size;

        CalculatorView.log("update VBOs " + vertexVbo + ' ' + colorVbo + ' ' + vertexElementVbo);
        nVertex = N * N + 6 + 8 + NTICK * 6 + 14; // 14 = 7*2 = "x,y,z" labels
        int nFloats = nVertex * 3;
        float vertices[] = new float[nFloats];
        byte colors[] = new byte[nVertex << 2];
        if (f != null) {
            CalculatorView.log("Graph3d update");
            float sizeX = maxX - minX;
            float sizeY = maxY - minY;
            float stepX = sizeX / (N - 1);
            float stepY = sizeY / (N - 1);
            int pos = 0;
            double sum = 0;
            float y = minY;
            float x = minX - stepX;
            int nRealPoints = 0;
            for (int i = 0; i < N; i++, y += stepY) {
                float xinc = (i & 1) == 0 ? stepX : -stepX;
                x += xinc;
                for (int j = 0; j < N; ++j, x += xinc, pos += 3) {
                    float z = (float) f.eval(x - centerX, y - centerY);
                    vertices[pos] = x;
                    vertices[pos + 1] = y;
                    vertices[pos + 2] = z;
                    if (z == z) { // not NAN
                        sum += z * z;
                        ++nRealPoints;
                    }
                }
            }
            float maxAbs = (float) Math.sqrt(sum / nRealPoints);
            maxAbs *= .9f;
            maxAbs = Math.min(maxAbs, 15);
            maxAbs = Math.max(maxAbs, .001f);

            final int limitColor = N * N * 4;
            for (int i = 0, j = 2; i < limitColor; i += 4, j += 3) {
                float z = vertices[j];
                if (z == z) {
                    final float a = z / maxAbs;
                    final float abs = a < 0 ? -a : a;
                    colors[i] = floatToByte(a);
                    colors[i + 1] = floatToByte(1 - abs * .3f);
                    colors[i + 2] = floatToByte(-a);
                    colors[i + 3] = (byte) 255;
                } else {
                    vertices[j] = 0;
                    z = 0;
                    colors[i] = 0;
                    colors[i + 1] = 0;
                    colors[i + 2] = 0;
                    colors[i + 3] = 0;
                }
            }
        }
        int base = N * N * 3;
        int colorBase = N * N * 4;
        int p = base;
        final int baseSize = 2;
        for (int i = -baseSize; i <= baseSize; i += 2 * baseSize) {
            vertices[p] = i;
            vertices[p + 1] = -baseSize;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = i;
            vertices[p + 1] = baseSize;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = -baseSize;
            vertices[p + 1] = i;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = baseSize;
            vertices[p + 1] = i;
            vertices[p + 2] = 0;
            p += 3;
        }
        for (int i = colorBase; i < colorBase + 8 * 4; i += 4) { // x-y plane
            colors[i] = (byte) Color.red(colorXYplane);
            colors[i + 1] = (byte) Color.green(colorXYplane);
            colors[i + 2] = (byte) Color.blue(colorXYplane);
            colors[i + 3] = (byte) Color.alpha(colorXYplane);
        }
        base += 8 * 3;
        colorBase += 8 * 4;

        final float unit = 2;
        final float axis[] = {
                0, 0, 0,
                unit, 0, 0,
                0, 0, 0,
                0, unit, 0,
                0, 0, 0,
                0, 0, unit,
        };
        System.arraycopy(axis, 0, vertices, base, 6 * 3);
        for (int i = colorBase; i < colorBase + 6 * 4; i += 4) { // x y z axis
            colors[i] = (byte) 255;
            colors[i + 1] = (byte) 255;
            colors[i + 2] = (byte) 255;
            colors[i + 3] = (byte) 255;
        }
        base += 6 * 3;
        colorBase += 6 * 4;

        p = base;
        final float tick = 0f;
        final float offset = .04f;
        for (int i = 1; i <= NTICK; ++i) {
            vertices[p] = i - tick;
            vertices[p + 1] = -offset;
            vertices[p + 2] = -offset;

            vertices[p + 3] = i + tick;
            vertices[p + 4] = offset;
            vertices[p + 5] = offset;
            p += 6;

            vertices[p] = -offset;
            vertices[p + 1] = i - tick;
            vertices[p + 2] = -offset;

            vertices[p + 3] = offset;
            vertices[p + 4] = i + tick;
            vertices[p + 5] = offset;
            p += 6;

            vertices[p] = -offset;
            vertices[p + 1] = -offset;
            vertices[p + 2] = i - tick;

            vertices[p + 3] = offset;
            vertices[p + 4] = offset;
            vertices[p + 5] = i + tick;
            p += 6;

        }
        for (int i = colorBase + NTICK * 3 * 2 * 4 - 1; i >= colorBase; --i) { // 3 (x/y/z) lines (=2 coordinates) per
                                                                               // tick, 4 color values
            colors[i] = (byte) 255;
        }

        colorBase += NTICK * 3 * 2 * 4;
        // "X label (2 lines)"
        vertices[p] = 1.5f - offset;
        vertices[p + 1] = 0;
        vertices[p + 2] = -offset;

        vertices[p + 3] = 1.5f + offset;
        vertices[p + 4] = 0;
        vertices[p + 5] = -3 * offset;
        p += 6;

        vertices[p] = 1.5f + offset;
        vertices[p + 1] = 0;
        vertices[p + 2] = -offset;

        vertices[p + 3] = 1.5f - offset;
        vertices[p + 4] = 0;
        vertices[p + 5] = -3 * offset;
        p += 6;

        // "Y label (2 lines)"
        vertices[p] = 0;
        vertices[p + 1] = 1.5f - offset;
        vertices[p + 2] = -offset;

        vertices[p + 3] = 0;
        vertices[p + 4] = 1.5f;
        vertices[p + 5] = -2 * offset;
        p += 6;

        vertices[p] = 0;
        vertices[p + 1] = 1.5f + offset;
        vertices[p + 2] = -offset;

        vertices[p + 3] = 0;
        vertices[p + 4] = 1.5f - offset;
        vertices[p + 5] = -3 * offset;
        p += 6;

        // "Z label (3 lines)"
        vertices[p] = offset;
        vertices[p + 1] = 0;
        vertices[p + 2] = 1.5f + offset;

        vertices[p + 3] = 2 * offset;
        vertices[p + 4] = 0;
        vertices[p + 5] = 1.5f + offset;
        p += 6;

        vertices[p] = 2 * offset;
        vertices[p + 1] = 0;
        vertices[p + 2] = 1.5f + offset;

        vertices[p + 3] = offset;
        vertices[p + 4] = 0;
        vertices[p + 5] = 1.5f - offset;
        p += 6;

        vertices[p] = offset;
        vertices[p + 1] = 0;
        vertices[p + 2] = 1.5f - offset;

        vertices[p + 3] = 2 * offset;
        vertices[p + 4] = 0;
        vertices[p + 5] = 1.5f - offset;
        p += 6;

        for (int i = colorBase + 7 * 2 * 4 - 1; i >= colorBase; --i) { // 7 lines: 2 + 2 + 3
            colors[i] = (byte) 255;
        }

        vertexBuf = buildBuffer(vertices);
        colorBuf = buildBuffer(colors);

        if (useVBO) {
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBuf.capacity() * 4, vertexBuf, GL11.GL_STATIC_DRAW);
            vertexBuf = null;

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
            gl.glBufferData(GL11.GL_ARRAY_BUFFER, colorBuf.capacity(), colorBuf, GL11.GL_STATIC_DRAW);
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
            colorBuf = null;

            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, vertexElementVbo);
            gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, verticeIdx.capacity() * 2, verticeIdx, GL11.GL_STATIC_DRAW);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    private byte floatToByte(float v) {
        return (byte) (v <= 0 ? 0 : v >= 1 ? 255 : (int) (v * 255));
    }

    public void draw(GL11 gl) {
        if (useVBO) {
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, 0);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
            // gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, N*N);

            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, vertexElementVbo);
            gl.glDrawElements(GL10.GL_LINE_STRIP, N * N, GL10.GL_UNSIGNED_SHORT, 0);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuf);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, colorBuf);
            gl.glDrawElements(GL10.GL_LINE_STRIP, N * N, GL10.GL_UNSIGNED_SHORT, verticeIdx);
        }
        final int N2 = N * N;
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, N2);
        gl.glDrawArrays(GL10.GL_LINES, N2, nVertex - N2);
    }
}
