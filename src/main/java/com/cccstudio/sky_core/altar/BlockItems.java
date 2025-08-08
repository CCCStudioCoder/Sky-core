package com.cccstudio.sky_core.altar;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.SkyCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockItems {

    public static final DeferredBlock<Block> ALTAR = Core.BLOCK_REGISTER.register(
            "altar", () -> new AltarBlock(BlockBehaviour.Properties.of())
    );

    public static final DeferredItem<BlockItem> ALTAR_ITEM = Core.ITEM_REGISTER.registerSimpleBlockItem(ALTAR);

    public static final Supplier<CreativeModeTab> GODS_CREATIVE_TAB = Core.CREATIVE_TAB_REGISTER.register(
            "gods", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tabs.sky_core.gods"))
                    .icon(() -> new ItemStack(ALTAR_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ALTAR_ITEM);
                    })
                    .build()
    );


}
