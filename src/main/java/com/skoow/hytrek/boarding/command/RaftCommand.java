package com.skoow.hytrek.boarding.command;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncWorldCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.hitboxcollision.HitboxCollision;
import com.hypixel.hytale.server.core.modules.entity.hitboxcollision.HitboxCollisionConfig;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.floating.Floating;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class RaftCommand extends AbstractAsyncWorldCommand {
    public RaftCommand() {
        super("raft","spawns a raft entity");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext, @NonNullDecl World world) {
        return CompletableFuture.runAsync(() -> {
            world.execute(() -> {
                var playerRef = commandContext.senderAsPlayerRef();

                var store = playerRef.getStore();
                var transform = store.getComponent(playerRef, TransformComponent.getComponentType());

                Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
                ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Raft");
                Model model = Model.createRandomScaleModel(modelAsset);
                TransformComponent entityTransform = new TransformComponent(transform.getPosition(), transform.getRotation());
                var boundingBox = model.getBoundingBox();
                holder.addComponent(TransformComponent.getComponentType(), entityTransform);
                holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
                holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
                holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(boundingBox));
                var collisionConfig = HitboxCollisionConfig.getAssetMap().getAsset("HardCollision");
                holder.addComponent(HitboxCollision.getComponentType(), new HitboxCollision(collisionConfig));
                holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
                holder.ensureComponent(UUIDComponent.getComponentType());
                holder.ensureComponent(Floating.getFloatComponentType());
                holder.addComponent(Velocity.getComponentType(), new Velocity());
                store.addEntity(holder, AddReason.SPAWN);
            });
        });
    }
}
