package com.cccstudio.sky_core.altar;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.GodProperty;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;


public class AltarBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final GodProperty GOD = new GodProperty("god", String.class);

    public AltarBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(GOD, "none")
        );
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GOD);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(GOD, "none");
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState a, BlockGetter b, BlockPos c) {
        return Block.box(2,0,2,14,16,14);
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState a, BlockGetter b, BlockPos c) {
        return false;
    }

    @Override
    public void stepOn(Level a, BlockPos b, BlockState c, Entity entity) {
        if(entity instanceof ItemEntity itemEntity){
            Item item = itemEntity.getItem().getItem();
            for (God god : Core.GODS) {
                for (HashMap.Entry<Item, Float> entry : god.getOfferings().entrySet()) {
                    if (entry.getKey().equals(item) && god.rollOffering(entry.getKey())) {
                        god.addPoint((Player) itemEntity.getOwner());
                    }
                }
            }
        }
    }
}