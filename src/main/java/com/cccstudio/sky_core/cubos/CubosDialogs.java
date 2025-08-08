package com.cccstudio.sky_core.cubos;

import com.cccstudio.sky_core.api.quest.Dialog;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

public class CubosDialogs {

    public static final Dialog CUBOS_DIALOG_BASIC = Dialog.create();

    private static final Dialog.Branch MAIN = CUBOS_DIALOG_BASIC.createBranch(
            Component.translatable("dialog.cubos.basic.main"),
            ctx -> 1, null
    );

    static {
        MAIN.createBranch(Component.translatable("dialog.cubos.basic.main.first"), ctx -> 2, null);
        MAIN.createBranch(Component.translatable("dialog.cubos.basic.main.second"), ctx -> 1,
                ctx -> EntityType.SILVERFISH.spawn((ServerLevel) ctx.getLevel(), ctx.getPlayer().getOnPos(), MobSpawnType.EVENT));
    }

}
