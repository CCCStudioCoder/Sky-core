package com.cccstudio.sky_core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(SkyCore.MODID)
public class SkyCore {

    public static final String MODID = "sky_core";

    public SkyCore(IEventBus modEventBus, ModContainer modContainer) {
        com.cccstudio.sky_core.Core.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

}
