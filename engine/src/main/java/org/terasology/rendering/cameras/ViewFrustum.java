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
package org.terasology.rendering.cameras;

import org.joml.AABBf;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.terasology.math.AABB;
import org.terasology.math.JomlUtil;
import org.terasology.math.geom.Vector3f;

/**
 * View frustum usable for frustum culling.
 *
 */
public class ViewFrustum {

    private final FrustumIntersection jomlFrustum = new FrustumIntersection();
    private final Matrix4f viewProjectionMatrix = new Matrix4f();

    /**
     * Updates the view frustum using the currently active modelview and projection matrices.
     */
    public void updateFrustum(Matrix4fc viewMatrix, Matrix4fc projectionMatrix) {
        viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix);
        jomlFrustum.set(viewProjectionMatrix);
    }

    /**
     * Returns true if the given point intersects the view frustum.
     */
    public boolean intersects(Vector3fc position) {
        return jomlFrustum.testPoint(position);
    }

    /**
     * Returns true if this view frustum intersects the given AABB.
     *
     * @deprecated This is scheduled for removal in an upcoming version method will be replaced with JOML implementation
     *     {@link #intersects(AABBf)}.
     */
    public boolean intersects(AABB aabb) {
        return jomlFrustum.testAab(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
    }

    /**
     * Returns true if this view frustum intersects the given AABB.
     */
    public boolean intersects(AABBf aabb) {
        return jomlFrustum.testAab(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    /**
     * Returns true if the given sphere intersects the view frustum.
     *
     * @deprecated This is scheduled for removal in an upcoming version method will be replaced with JOML implementation
     *     {@link #intersects(Vector3fc, float)}.
     */
    public boolean intersects(Vector3f position, float radius) {
        return intersects(JomlUtil.from(position), radius);
    }

    /**
     * Returns true if the given sphere intersects the view frustum.
     */
    public boolean intersects(Vector3fc position, float radius) {
        return jomlFrustum.testSphere(position, radius);
    }
}
