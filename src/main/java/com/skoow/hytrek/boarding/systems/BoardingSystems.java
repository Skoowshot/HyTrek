package com.skoow.hytrek.boarding.systems;

import com.hypixel.hytale.builtin.teleport.commands.teleport.TeleportCommand;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.protocol.packets.player.ClientTeleport;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.system.TransformSystems;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.TeleportSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.skoow.hytrek.HytrekPlugin;
import com.skoow.hytrek.boarding.component.BoardComponent;
import com.skoow.hytrek.boarding.Boarding;
import com.skoow.hytrek.boarding.component.BoardableComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class BoardingSystems {
    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    public static class TickingBoardedSystem extends EntityTickingSystem<EntityStore> {
        @Override
        public void tick(float dt, int idx, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            BoardComponent boardComponent = BoardComponent.get(archetypeChunk, idx);
            if (boardComponent == null || dt <= 0) return;

            var playerTransform = archetypeChunk.getComponent(idx, TransformComponent.getComponentType());
            Velocity boardedVelocity = archetypeChunk.getComponent(idx, Velocity.getComponentType());

            Velocity boardableVelocity = store.getComponent(boardComponent.boardable, Velocity.getComponentType());
            TransformComponent boardableTransform = store.getComponent(boardComponent.boardable, TransformComponent.getComponentType());

            if (boardedVelocity != null && boardableVelocity != null && boardableTransform != null && playerTransform != null) {

                Vector3f currentRot = boardableTransform.getRotation();
                Vector3f lastRot = boardComponent.lastBoardableOrientation;

                float totalDPitch = normalizeAngle(currentRot.x - lastRot.x);
                float totalDYaw = normalizeAngle(currentRot.y - lastRot.y);
                float totalDRoll = normalizeAngle(currentRot.z - lastRot.z);

                int subSteps = 16;
                float subDt = dt / subSteps;

                Vector3d baseMoveStep = boardableVelocity.getVelocity().clone().scale(subDt);

                Vector3d virtualPlayerPos = playerTransform.getPosition().clone();
                Vector3d virtualCenterPos = boardableTransform.getPosition().clone().subtract(boardableVelocity.getVelocity().clone().scale(dt));

                double initialRadius = virtualPlayerPos.distanceTo(boardableTransform.getPosition().clone().subtract(boardableVelocity.getVelocity().clone().scale(dt)));

                for (int i = 0; i < subSteps; i++) {
                    virtualCenterPos.add(baseMoveStep);

                    Vector3d subAngularVel = new Vector3d(totalDPitch / dt, totalDYaw / dt, totalDRoll / dt);
                    Vector3d relativePos = virtualPlayerPos.clone().subtract(virtualCenterPos);

                    Vector3d tangentialMove = subAngularVel.cross(relativePos).scale(subDt);

                    double currentDist = relativePos.length();
                    Vector3d radiusCorrection = new Vector3d(0,0,0);
                    if (currentDist > 0) {
                        double error = initialRadius - currentDist;
                        radiusCorrection = relativePos.clone().normalize().scale(error);
                    }

                    virtualPlayerPos.add(tangentialMove).add(radiusCorrection).add(baseMoveStep);
                }

                Vector3d finalVelocity = virtualPlayerPos.subtract(playerTransform.getPosition()).scale(1.0 / dt);

                boardedVelocity.addInstruction(finalVelocity, new VelocityConfig(), ChangeVelocityType.Set);

                Vector3f pRot = playerTransform.getRotation();
                playerTransform.setRotation(new Vector3f(pRot.x + totalDPitch, pRot.y + totalDYaw, pRot.z + totalDRoll));

                boardComponent.lastBoardableOrientation = currentRot.clone();
            }
        }

        private float normalizeAngle(float angle) {
            while (angle > Math.PI) angle -= (float) Math.TAU;
            while (angle < -Math.PI) angle += (float) Math.TAU;
            return angle;
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Boarding.getBoardComponentType();
        }

        @NonNullDecl
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(new SystemDependency<>(Order.BEFORE,  TickingBoardableSystem.class));
        }
    }

    public static class TickingBoardableSystem extends EntityTickingSystem<EntityStore> {
        @Override
        public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            BoardableComponent boardableComponent = BoardableComponent.get(archetypeChunk, i);
            int lastSize = boardableComponent.boarded.size();
            boardableComponent.boarded.removeIf(x -> x == null || !x.isValid() || BoardComponent.get(store, x) == null);
            int newSize = boardableComponent.boarded.size();
            if(newSize != lastSize) {
                logger.atInfo().log("Removing {} entities from boardable, as ref is invalid", lastSize - newSize);
            }
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Boarding.getBoardableComponentType();
        }
    }
}
