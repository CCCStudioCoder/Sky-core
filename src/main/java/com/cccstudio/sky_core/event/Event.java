package com.cccstudio.sky_core.event;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.entity.GodEntity;
import com.cccstudio.sky_core.command.SkyCoreCommands;
import com.cccstudio.sky_core.cubos.CubosGod;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Objects;

@EventBusSubscriber
public class Event {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SkyCoreCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void playerEquipment(LivingEquipmentChangeEvent event) {
        if(event.getEntity() instanceof Player player && event.getSlot().equals(EquipmentSlot.BODY)
                && event.getTo().is(Items.ELYTRA)) {
            CubosGod.GET_ELYTRA.grant(player, 3, CubosGod.CUBOS);
        }
    }

    @SubscribeEvent
    public static void serverTick(ServerStartingEvent event) {
        for(God god : Core.GODS) {
            ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
            Class<? extends GodEntity> entity = god.getEntitySupplier().getEntityClass();

            assert level != null;
            boolean finding_one = false;
            for(ServerPlayer player : level.players()) {
                if (!level.getEntitiesOfClass(entity,
                        player.getBoundingBox().inflate(256)).isEmpty()) {
                    finding_one = true;
                    break;
                }
            }

            if(!finding_one) {
                ((EntityType<?>) (god.getEntitySupplier().getEntity().get())).spawn(level,
                        randomAround(Objects.requireNonNull(level.getRandomPlayer()).getOnPos(), level), MobSpawnType.TRIGGERED);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if(event.getEntity() instanceof EnderDragon && event.getSource().getEntity() instanceof Player player) {
            CubosGod.KILL_ENDERDRAGON.grant(player, 5, CubosGod.CUBOS);
        }
    }

    private static BlockPos randomAround(BlockPos pos, Level level) {
        double x = (pos.getX() - 0.5d) * 256;
        double z = (pos.getZ() - 0.5d) * 256;
        double y = -64;

        for(int i = 312; i >= -64; i--) {
            BlockState state = level.getBlockState(BlockPos.containing(x, i, z));
            if(!(state.is(Blocks.AIR) || state.is(Blocks.CAVE_AIR))) {
                y = i;
                break;
            }
        }

        return BlockPos.containing(x, y, z);
    }

}
