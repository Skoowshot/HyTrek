package com.skoow.hytrek.boarding.component;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.boarding.Boarding;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BoardComponent implements Component<EntityStore> {
    public Ref<EntityStore> boardable;
    public Vector3f lastBoardableOrientation = new Vector3f();

    public static BoardComponent get(Store<EntityStore> store, Ref<EntityStore> ref) {
        return store.getComponent(ref, Boarding.getBoardComponentType());
    }

    public static BoardComponent get(ArchetypeChunk<EntityStore> archetypeChunk, int idx) {
        return archetypeChunk.getComponent(idx, Boarding.getBoardComponentType());
    }

    public BoardComponent(Ref<EntityStore> boardable) {
        this.boardable = boardable;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new BoardComponent(boardable);
    }
}
