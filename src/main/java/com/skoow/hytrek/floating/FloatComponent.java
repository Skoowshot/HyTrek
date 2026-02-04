package com.skoow.hytrek.floating;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.boarding.Boarding;
import com.skoow.hytrek.boarding.component.BoardComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FloatComponent implements Component<EntityStore> {
    public static FloatComponent get(Store<EntityStore> store, Ref<EntityStore> ref) {
        return store.getComponent(ref, Floating.getFloatComponentType());
    }

    public static FloatComponent get(ArchetypeChunk<EntityStore> archetypeChunk, int idx) {
        return archetypeChunk.getComponent(idx, Floating.getFloatComponentType());
    }
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new FloatComponent();
    }
}
