/*
 * Copyright 2020 MovingBlocks
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
package org.terasology.logic.characters.movement;

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.AffectJumpForceEvent;
import org.terasology.logic.characters.AffectMultiJumpEvent;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.CharacterStateEvent;
import org.terasology.logic.characters.events.JumpEvent;
import org.terasology.math.JomlUtil;

public class VanillaGroundMovement implements MovementMechanics {

    private static final float GRAVITY = 28.0f;
    private static final float TERMINAL_VELOCITY = 64.0f;

    @Override
    public Vector3f step(CharacterStateEvent state, CharacterMoveInputEvent input, EntityRef entity) {
        CharacterMovementComponent movementComponent = entity.getComponent(CharacterMovementComponent.class);

        Vector3f desiredVelocity = new Vector3f(JomlUtil.from(input.getMovementDirection()));
        float lengthSquared = desiredVelocity.lengthSquared();
        if (lengthSquared > 1) {
            desiredVelocity.normalize();
        }
        desiredVelocity.mul(movementComponent.speedMultiplier);
        float maxSpeed = 5f;

        if (movementComponent.grounded && desiredVelocity.y != 0) {
            float speed = desiredVelocity.length();
            desiredVelocity.y = 0;
            if (desiredVelocity.x != 0 || desiredVelocity.z != 0) {
                desiredVelocity.normalize().mul(speed);
            }
        }
        desiredVelocity.mul(maxSpeed);

        // Modify velocity towards desired, up to the maximum rate determined by friction
        Vector3f velocityDiff = new Vector3f(desiredVelocity)
                .sub(JomlUtil.from(state.getVelocity()))
                .mul(Math.min(movementComponent.mode.scaleInertia * input.getDelta(), 1.0f));

        Vector3f endVelocity = new Vector3f(JomlUtil.from(state.getVelocity()));
        endVelocity.x += velocityDiff.x;
        endVelocity.z += velocityDiff.z;
        if (movementComponent.mode.scaleGravity == 0) {
            // apply the velocity without gravity
            endVelocity.y += velocityDiff.y;
        } else if (movementComponent.mode.applyInertiaToVertical) {
            endVelocity.y += Math.max(-TERMINAL_VELOCITY, velocityDiff.y - (GRAVITY * movementComponent.mode.scaleGravity) * input.getDelta());
        } else {
            endVelocity.y = Math.max(-TERMINAL_VELOCITY, state.getVelocity().y - (GRAVITY * movementComponent.mode.scaleGravity) * input.getDelta());
        }

        if (input.isJumpRequested()) {
            if (state.isGrounded()) {
                jump(input, entity, movementComponent, endVelocity);
                state.setGrounded(false);
            } else if(movementComponent.numberOfJumpsLeft > 0) {
                jump(input, entity, movementComponent, endVelocity);
            }
        }

        return endVelocity;
    }

    private void jump(CharacterMoveInputEvent input, EntityRef entity, CharacterMovementComponent movementComponent, Vector3f endVelocity) {
        // Send event to allow for other systems to modify the jump force.
        AffectJumpForceEvent affectJumpForceEvent = new AffectJumpForceEvent(movementComponent.jumpSpeed);
        entity.send(affectJumpForceEvent);
        endVelocity.y = affectJumpForceEvent.getResultValue();
        if (input.isFirstRun()) {
            entity.send(new JumpEvent());
        }

        // Send event to allow for other systems to modify the max number of jumps.
        AffectMultiJumpEvent affectMultiJumpEvent = new AffectMultiJumpEvent(movementComponent.baseNumberOfJumpsMax);
        entity.send(affectMultiJumpEvent);
        movementComponent.numberOfJumpsMax = (int) affectMultiJumpEvent.getResultValue();

        movementComponent.numberOfJumpsLeft--;
    }
}
