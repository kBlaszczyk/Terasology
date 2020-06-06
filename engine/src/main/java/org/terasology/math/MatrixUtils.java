/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.math;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Collection of matrix utilities.
 *
 */
public final class MatrixUtils {

    private MatrixUtils() {
    }

    public static FloatBuffer matrixToFloatBuffer(Matrix4fc m) {
        return m.getTransposed(BufferUtils.createFloatBuffer(16));
    }

    public static Matrix4f createOrthogonalProjectionMatrix(float left, float right, float top, float bottom, float near, float far) {
        return new Matrix4f().setOrtho(left, right, bottom, top, near, far);
    }

    public static Matrix4f createPerspectiveProjectionMatrix(float fovY, float aspectRatio, float zNear, float zFar) {
        return new Matrix4f().setPerspective(fovY, aspectRatio, zNear, zFar);
    }

    public static Matrix4f calcViewProjectionMatrix(Matrix4f view, Matrix4f projection) {
        return new Matrix4f(projection).mul(view);
    }

    public static Matrix4f calcModelViewMatrix(Matrix4f model, Matrix4f view) {
        return new Matrix4f(model).mul(view);
    }

    public static Matrix3f calcNormalMatrix(Matrix4f m) {
        return m.normal(new Matrix3f());
    }
}
