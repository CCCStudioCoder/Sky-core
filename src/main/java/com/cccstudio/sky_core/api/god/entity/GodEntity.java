package com.cccstudio.sky_core.api.god.entity;

import com.cccstudio.sky_core.api.god.God;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * All {@link God} will need a {@link GodEntitySupplier} that need a {@code Class<? extends GodEntity>}
 * So you need to create a {@code public class MyGodEntity extends GodEntity}
 */
public class GodEntity extends LivingEntity {
    /**
     * Can be 0: humanoid (wandering trader or villager).
     * Or 1: god
     */
    public static final EntityDataAccessor<Integer> ASPECT = SynchedEntityData.defineId(
            GodEntity.class, EntityDataSerializers.INT
    );

    /**
     * Can be 0: angry; 1: neutral or 2: happy. Random through the time.
     */
    public static final EntityDataAccessor<Integer> MOOD = SynchedEntityData.defineId(
            GodEntity.class, EntityDataSerializers.INT
    );


    public GodEntity(EntityType<? extends GodEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(ASPECT, 0);
        builder.define(MOOD, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {

    }

}
