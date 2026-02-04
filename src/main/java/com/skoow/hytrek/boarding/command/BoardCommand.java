package com.skoow.hytrek.boarding.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.skoow.hytrek.boarding.Boarding;
import com.skoow.hytrek.boarding.component.BoardComponent;
import com.skoow.hytrek.boarding.component.BoardableComponent;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class BoardCommand extends AbstractTargetEntityCommand {
    public BoardCommand() {
        super("board","boards an entity");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl ObjectList<Ref<EntityStore>> objectList, @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        if(objectList.isEmpty()) return;
        var boardedRef = commandContext.senderAsPlayerRef();
        var ref = objectList.getFirst();
        if(BoardableComponent.get(store, ref) == null) {
            store.addComponent(ref, Boarding.getBoardableComponentType(), BoardableComponent.def(Box.horizontallyCentered(10, 10, 10)));
        }

        if(BoardComponent.get(store, boardedRef) == null) {
            Boarding.board(store, boardedRef, new BoardComponent(ref));
            commandContext.sendMessage(Message.raw("Boarded"));
        } else {
            Boarding.unboard(store, boardedRef);
            commandContext.sendMessage(Message.raw("Deboarded"));
        }
    }
}
