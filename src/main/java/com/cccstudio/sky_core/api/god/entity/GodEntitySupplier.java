package com.cccstudio.sky_core.api.god.entity;

import com.cccstudio.sky_core.api.god.God;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GodEntitySupplier<E extends GodEntity> {

    private final DeferredHolder<EntityType<?>, EntityType<GodEntity>> SUPPLIER;

    private final Class<E> ENTITY_CLASS;

    private final DeferredItem<Item> INVOKER;

    /**
     * This class aim to divide {@link God} instantiation with his entity.
     * It's passed as argument to {@link God#God}.
     * @param name
     * The registry name of all related objects like {@link EntityType} or {@link DeferredItem}.
     * @param entityRegister
     * The {@link DeferredRegister} used for registering the {@code EntityType<GodEntity>}.
     * @param itemRegister
     * The same as {@code entityRegister}, but for {@link Item}.
     * @param entityClass
     * A class that extends {@link GodEntity}, for registering the {@link EntityType}.
     */
    public GodEntitySupplier(String name,
                             DeferredRegister<EntityType<?>> entityRegister,
                             DeferredRegister.Items itemRegister,
                             Class<E> entityClass) {
        SUPPLIER = entityRegister.register(name, () -> EntityType.Builder.of(
                (EntityType<GodEntity> type, Level world) -> {
                    try {
                        Constructor<E> constructor = entityClass.getDeclaredConstructor(EntityType.class, Level.class);
                        return constructor.newInstance(type, world);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + entityClass.getName(), e);
                    }
                },
                MobCategory.MISC
        ).noSummon().build(name));

        ENTITY_CLASS = entityClass;
        INVOKER = itemRegister.register(name + "_invoker", () -> {
            try {
                return new Invoker(
                        entityClass.getConstructor().newInstance()
                );
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public DeferredHolder<EntityType<?>, EntityType<GodEntity>> getEntity() {
        return SUPPLIER;
    }

    public Class<E> getEntityClass() {
        return ENTITY_CLASS;
    }

    public DeferredItem<Item> getInvoker() {
        return INVOKER;
    }

}
