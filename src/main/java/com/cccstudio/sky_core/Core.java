package com.cccstudio.sky_core;

import com.cccstudio.sky_core.api.mythical_event.IMythicalEvent;
import com.cccstudio.sky_core.api.god.God;
import com.cccstudio.sky_core.api.quest.Dialog;
import com.cccstudio.sky_core.api.quest.Quest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The Core class isn't an object at all.
 * It provides project-related static variables.
 * @see com.cccstudio.sky_core.api.quest.QuestHandler The quests util
 */
public final class Core {

    public static final Collection<God> GODS = new ArrayList<>();

    public static final HashMap<ResourceLocation, God> GOD_LOCATIONS = new HashMap<>();

    public static final Collection<Dialog> DIALOGS = new ArrayList<>();

    public static final HashMap<ResourceLocation, Quest> QUEST_LOCATIONS = new HashMap<>();

    public static final Collection<DeferredItem<Item>> INVOKERS = new ArrayList<>();

    public static final Collection<Class<IMythicalEvent>> MYTHICAL_EVENTS = new ArrayList<>();


    public static final ResourceKey<Registry<God>> GOD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "gods"));

    public static final Registry<God> GOD_REGISTRY = new RegistryBuilder<>(GOD_REGISTRY_KEY).create();

    public static final ResourceKey<Registry<Quest>> QUEST_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "quests"));

    public static final Registry<Quest> QUEST_REGISTRY = new RegistryBuilder<>(QUEST_REGISTRY_KEY).create();

    // Some core DeferredRegisters to help you
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE_REGISTER =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SkyCore.MODID);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SkyCore.MODID);

    public static final DeferredRegister.Items ITEM_REGISTER =
            DeferredRegister.createItems(SkyCore.MODID);

    public static final DeferredRegister.Blocks BLOCK_REGISTER =
            DeferredRegister.createBlocks(SkyCore.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB_REGISTER =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, SkyCore.MODID);


    public static void register(IEventBus bus) {
        ATTACHMENT_TYPE_REGISTER.register(bus);
        ENTITY_TYPE_REGISTER.register(bus);
        ITEM_REGISTER.register(bus);
        BLOCK_REGISTER.register(bus);
        CREATIVE_TAB_REGISTER.register(bus);
    }

}
