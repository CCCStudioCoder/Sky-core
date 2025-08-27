package com.cccstudio.sky_core.event;

import com.cccstudio.sky_core.api.god.entity.renderer.GodModel;
import com.cccstudio.sky_core.api.quest.QuestHandler;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.cccstudio.sky_core.SkyCore.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(
                QuestHandler.ENGAGED_QUESTS,
                EntityType.PLAYER,
                (player, voi) -> new QuestHandler.QuestCollection()
        );
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(GodModel.LAYER_LOCATION, GodModel::createBodyLayer);
        }

    }

}
