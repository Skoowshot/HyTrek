package com.skoow.hytrek.floating;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.HytrekPlugin;

public class Floating {
    private static ComponentType<EntityStore, FloatComponent> floatComponentType;
    public static void setup() {
        var esr = HytrekPlugin.get().getEntityStoreRegistry();

        floatComponentType = esr.registerComponent(FloatComponent.class, FloatComponent::new);
        esr.registerSystem(new FloatSystems.FloatInWaterSystem());
    }

    public static ComponentType<EntityStore, FloatComponent> getFloatComponentType() {
        return floatComponentType;
    }

}
