package com.skoow.hytrek;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.boarding.Boarding;
import com.skoow.hytrek.floating.Floating;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class HytrekPlugin extends JavaPlugin {
    private static HytrekPlugin instance;

    public static HytrekPlugin get() {
        return instance;
    }

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public HytrekPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        HytrekPlugin.instance = this;
        LOGGER.atInfo().log("Setting up plugin " + getName());
        var esr = getEntityStoreRegistry();
        Floating.setup();
        Boarding.setup();
    }
}