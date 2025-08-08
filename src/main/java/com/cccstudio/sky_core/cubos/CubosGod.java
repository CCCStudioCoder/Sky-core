package com.cccstudio.sky_core.cubos;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.SkyCore;
import com.cccstudio.sky_core.api.god.God;
import com.cccstudio.sky_core.api.god.entity.GodEntitySupplier;
import com.cccstudio.sky_core.api.quest.Quest;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CubosGod {

    public static final HashMap<Item, Float> CUBOS_OFFERINGS = new HashMap<>();

    static {
        CUBOS_OFFERINGS.put(Items.DIRT, 0.00001f);
        CUBOS_OFFERINGS.put(Items.DIAMOND, 0.2f);
    }

    public static final Collection<Quest> CUBOS_QUESTS = new ArrayList<>();

    public static final Quest KILL_ENDERDRAGON = new Quest(
            ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "kill_enderdragon"),
            null
    );

    public static final Quest GET_ELYTRA = new Quest(
            ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "get_elytra"),
            null
    );

    static {
        CUBOS_QUESTS.add(KILL_ENDERDRAGON);
    }

    public static final God CUBOS = new God(
            ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "cubos"),
            Core.ATTACHMENT_TYPE_REGISTER,
            (level) -> {},
            Component.translatable("god.sky_core.cubos.name"),
            new GodEntitySupplier<CubosEntity>(
                    "cubos_entity",
                    Core.ENTITY_TYPE_REGISTER,
                    Core.ITEM_REGISTER,
                    new Item.Properties(),
                    CubosEntity.class
            ),
            CUBOS_OFFERINGS,
            CUBOS_QUESTS
    );

}
