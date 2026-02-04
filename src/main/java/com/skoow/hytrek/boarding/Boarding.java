package com.skoow.hytrek.boarding;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.HytrekPlugin;
import com.skoow.hytrek.boarding.command.BoardCommand;
import com.skoow.hytrek.boarding.component.BoardComponent;
import com.skoow.hytrek.boarding.component.BoardableComponent;
import com.skoow.hytrek.boarding.systems.BoardingSystems;

public class Boarding {
    private static ComponentType<EntityStore, BoardComponent> boardComponentType;
    private static ComponentType<EntityStore, BoardableComponent> boardableComponentType;

    public static void setup() {
        var esr = HytrekPlugin.get().getEntityStoreRegistry();

        boardComponentType = esr.registerComponent(BoardComponent.class, () -> {
            throw new UnsupportedOperationException("Cannot default construct BoardComponent");
        });
        boardableComponentType = esr.registerComponent(BoardableComponent.class, () -> {
            throw new UnsupportedOperationException("Cannot default construct BoardableComponent");
        });

        HytrekPlugin.get().getCommandRegistry().registerCommand(new BoardCommand());

        esr.registerSystem(new BoardingSystems.TickingBoardableSystem());
        esr.registerSystem(new BoardingSystems.TickingBoardedSystem());
    }

    public static ComponentType<EntityStore, BoardComponent> getBoardComponentType() {
        return boardComponentType;
    }

    public static ComponentType<EntityStore, BoardableComponent> getBoardableComponentType() {
        return boardableComponentType;
    }

    public static void board(Store<EntityStore> store, Ref<EntityStore> boarded, BoardComponent boardComponent) {
        var boardable = boardComponent.boardable;
        var boardableComponent = store.getComponent(boardable, getBoardableComponentType());
        TransformComponent transformComponent = store.getComponent(boardable, TransformComponent.getComponentType());
        assert transformComponent != null;
        boardComponent.lastBoardableOrientation = transformComponent.getRotation();

        store.putComponent(boarded, getBoardComponentType(), boardComponent);

        if(boardableComponent != null && !boardableComponent.boarded.contains(boarded))
            boardableComponent.boarded.add(boarded);
    }

    public static void unboard(Store<EntityStore> store, Ref<EntityStore> boarded) {
        var boardedComponent = store.getComponent(boarded, getBoardComponentType());
        if(boardedComponent == null) return;
        store.tryRemoveComponent(boarded, getBoardComponentType());
        if(boardedComponent.boardable == null || !boardedComponent.boardable.isValid()) return;
        var boardableComponent = store.getComponent(boardedComponent.boardable, getBoardableComponentType());
        if(boardableComponent != null)
            boardableComponent.boarded.remove(boarded);
    }
}
