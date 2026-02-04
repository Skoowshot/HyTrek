package com.skoow.hytrek.boarding.component;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.boarding.Boarding;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class BoardableComponent implements Component<EntityStore> {
    public final List<Ref<EntityStore>> boarded = new ObjectArrayList<>();
    public Box enterBox;
    public Box exitBox;

    public static BoardableComponent get(Store<EntityStore> store, Ref<EntityStore> ref) {
        return store.getComponent(ref, Boarding.getBoardableComponentType());
    }

    public static BoardableComponent get(ArchetypeChunk<EntityStore> archetypeChunk, int idx) {
        return archetypeChunk.getComponent(idx, Boarding.getBoardableComponentType());
    }

    public static BoardableComponent def(Box box) {
        Box exitBox = box.clone();
        exitBox.expand(1.2);
        return new BoardableComponent(box, exitBox);
    }

    public BoardableComponent(Box enterBox, Box exitBox) {
        this.enterBox = enterBox;
        this.exitBox = exitBox;
    }

    public BoardableComponent(Box enterBox, Box exitBox, List<Ref<EntityStore>> boarded) {
        this(enterBox, exitBox);
        this.boarded.addAll(boarded);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new BoardableComponent(enterBox, exitBox, boarded);
    }
}
