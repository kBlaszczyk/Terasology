package org.terasology.math;

import org.joml.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.lang.Math;
import java.nio.FloatBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixUtilsTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void testMatrix3ToFloatBuffer() {
        Matrix3f matrix = new Matrix3f(
            1f, 2f, 3f,
            4f, 5f, 6f,
            7f, 8f, 9f
        );

        FloatBuffer buffer = MatrixUtils.matrixToFloatBuffer(matrix);

        assertEquals(1f, buffer.get());
        assertEquals(2f, buffer.get());
        assertEquals(3f, buffer.get());
        assertEquals(4f, buffer.get());
        assertEquals(5f, buffer.get());
        assertEquals(6f, buffer.get());
        assertEquals(7f, buffer.get());
        assertEquals(8f, buffer.get());
        assertEquals(9f, buffer.get());

        Assert.assertEquals(0, buffer.remaining());
    }

    @Test
    public void testMatrix4ToFloatBuffer() {
        Matrix4f matrix = new Matrix4f(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        );

        FloatBuffer buffer = MatrixUtils.matrixToFloatBuffer(matrix);

        assertEquals(1f, buffer.get());
        assertEquals(2f, buffer.get());
        assertEquals(3f, buffer.get());
        assertEquals(4f, buffer.get());

        assertEquals(5f, buffer.get());
        assertEquals(6f, buffer.get());
        assertEquals(7f, buffer.get());
        assertEquals(8f, buffer.get());

        assertEquals(9f, buffer.get());
        assertEquals(10f, buffer.get());
        assertEquals(11f, buffer.get());
        assertEquals(12f, buffer.get());

        assertEquals(13f, buffer.get());
        assertEquals(14f, buffer.get());
        assertEquals(15f, buffer.get());
        assertEquals(16f, buffer.get());

        assertEquals(0, buffer.remaining());
    }

    @Test
    public void testViewMatrixCreation() {
        Vector3f eye = new Vector3f(0, 10, 10);
        Vector3f center = new Vector3f();
        Vector3f up = new Vector3f(0, 1, 0);

        Matrix4f expected = new Matrix4f(
            0.9999f, 0f, 0f, 0f,
            0f, 0.7071f, -0.7071f, 0f,
            0f, 0.7071f, 0.7071f, -14.1421f,
            0f, 0f, 0f, 1f
        ).transpose();

        assertMatricesAreRoughlyEqual(expected, MatrixUtils.createViewMatrix(eye, center, up));
        assertMatricesAreRoughlyEqual(expected, MatrixUtils.createViewMatrix(
            eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z
        ));
    }

    @Test
    public void testOrthogonalProjectionMatrixCreation() {
        Matrix4f expected = new Matrix4f(
            0.002f, 0f, 0f, 0f,
            0f, 0.002f, 0f, 0f,
            0f, 0f, -0.001f, 0f,
            0f, 0f, 0f, 1f
        );

        assertMatricesAreRoughlyEqual(expected, MatrixUtils.createOrthogonalProjectionMatrix(
            -500, 500, 500, -500, -1000, 1000
        ));
    }

    @Test
    public void testPerspectiveProjectionMatrixCreation() {
        Matrix4f expected = new Matrix4f(
            0.9743f, 0f, 0f, 0f,
            0f, 1.732f, 0f, 0f,
            0f, 0f, -1f, -0.2f,
            0f, 0f, -1f, 0f
        ).transpose();

        Matrix4f actual = MatrixUtils.createPerspectiveProjectionMatrix(
                (float) Math.toRadians(60.0), 16f / 9f, 0.1f, 5000f
        );

        assertMatricesAreRoughlyEqual(expected, actual);
    }

    @Test
    public void testNormalMatrixCreation() {
        Matrix3f expectedNormalMatrix = new Matrix3f(
            0f, 0.3536f, -0.3536f,
            0f, 0.3536f, 0.3536f,
            0.5f, 0f, 0f
        );

        Matrix4f model = new Matrix4f().translationRotateScale(
            new Vector3f(1f, 2f, 3f),
            new Quaternionf().rotateY((float) Math.toRadians(90.0)),
            2f
        );

        Matrix4fc view = new Matrix4f().setLookAt(
            0f, 10f, 10f,
            0f, 0f, 0f,
            0f, 1f, 0f
        );

        Matrix4f modelView = view.mul(model, new Matrix4f());

        assertMatricesAreRoughlyEqual(
            expectedNormalMatrix, MatrixUtils.calcNormalMatrix(modelView)
        );
    }

    private void assertMatricesAreRoughlyEqual(Matrix4f expected, Matrix4f actual) {
        assertTrue(expected.equals(actual, EPSILON));
    }

    private void assertMatricesAreRoughlyEqual(Matrix3f expected, Matrix3f actual) {
        assertTrue(expected.equals(actual, EPSILON));
    }
}