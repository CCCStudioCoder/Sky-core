package com.cccstudio.sky_core;

import com.cccstudio.sky_core.altar.BlockItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SkyCore is a NeoGradle API that aims to provide a full handler of mythologics creatures and gods
 * @author CCC Studio
 * @version LTS 1.2
 */
@Mod(SkyCore.MODID)
public class SkyCore {

    public static final String MODID = "sky_core";

    public static final Logger LOGGER = LogManager.getLogger(SkyCore.MODID);

    public SkyCore(IEventBus modEventBus, ModContainer modContainer) {
        com.cccstudio.sky_core.Core.register(modEventBus);
        //NeoForge.EVENT_BUS.register(this);
    }

    public static void buildCreativeTabsContent(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == BlockItems.GODS_CREATIVE_TAB) {
            event.accept(BlockItems.ALTAR_ITEM);
            event.acceptAll(Core.INVOKERS.stream().map(DeferredItem::toStack).toList());
        }
    }

}
