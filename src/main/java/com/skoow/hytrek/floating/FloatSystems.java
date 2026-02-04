package com.skoow.hytrek.floating;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.util.PositionProbeWater;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FloatSystems {
    public static class FloatInWaterSystem extends EntityTickingSystem<EntityStore> {
        final PositionProbeWater positionProbeWater = new PositionProbeWater();
        @Override
        public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            FloatComponent floatComponent = FloatComponent.get(archetypeChunk, i);
            TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
            BoundingBox boundingBox = archetypeChunk.getComponent(i, BoundingBox.getComponentType());
            assert boundingBox != null;
            var ref = archetypeChunk.getReferenceTo(i);
            positionProbeWater.probePosition(ref, boundingBox.getBoundingBox(), transformComponent.getPosition(), new CollisionResult(),
                    0, store);
            if(positionProbeWater.isInWater()) {
                Velocity velocity = store.getComponent(ref, Velocity.getComponentType());
                velocity.addInstruction(new Vector3d(0, 0.05, 0), new VelocityConfig(), ChangeVelocityType.Add);
            }
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Floating.getFloatComponentType();
        }
    }
}
