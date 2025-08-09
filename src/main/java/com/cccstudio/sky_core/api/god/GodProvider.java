package com.cccstudio.sky_core.api.god;

import com.cccstudio.sky_core.api.god.entity.GodEntity;
import com.cccstudio.sky_core.api.god.entity.GodEntitySupplier;
import com.cccstudio.sky_core.api.quest.Quest;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The "GodProvider" class provides a shorter way to create gods, replacing redundant parameters
 * to avoid copying the same things several times.
 */
public class GodProvider {

    private final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE;
    private final DeferredRegister<EntityType<?>> ENTITY_TYPE;
    private final DeferredRegister.Items ITEM;

    private final String NAMESPACE;


    private static final HashMap<String, GodEntitySupplier<?>> SAVED_SUPPLIERS = new HashMap<>();

    /**
     * This constructor is very extensible, you can create instances as many as you want,
     * no needs to create both {@linkplain God} and {@linkplain GodEntitySupplier} each time, you
     * can instance one manually and the other with this.
     * Also, you don't need to register the GodProvider.
     * @param namespace
     * The namespace of your mod.
     * @param attachmentTypeRegister
     * To use for {@link God} instantiation.
     * @param entityTypeRegister
     * To use for {@link GodEntitySupplier} instantiation.
     * @param itemRegister
     * To use for {@link GodEntitySupplier} instantiation.
     */
    public GodProvider(String namespace,
                       DeferredRegister<AttachmentType<?>> attachmentTypeRegister,
                       DeferredRegister<EntityType<?>> entityTypeRegister,
                       DeferredRegister.Items itemRegister) {
        ATTACHMENT_TYPE = attachmentTypeRegister;
        ENTITY_TYPE = entityTypeRegister;
        ITEM = itemRegister;
        NAMESPACE = namespace;
    }

    /**
     * The equivalent of {@code new God()} but without some parameters.
     * @param name
     * Replace {@linkplain ResourceLocation} and {@linkplain MutableComponent}.
     * @param handleBonuses
     * See God#God param "handleBonuses"
     * @param offerings
     * See God#God param "offerings"
     * @param quests
     * See God#God param "quests"
     * @param entitySupplier
     * If {@code null}, it's going to check if you already create an {@link GodEntitySupplier} with
     * the same "name" parameter (it can be with another GodProvider but not with direct instantiation).
     * See God#God param "entity"
     * @return
     * The {@link God} you just created.
     * @throws NullPointerException
     * If "entitySupplier" parameter is null and there's no {@linkplain GodEntitySupplier} with
     * the same "name" param.
     */
    public God createGod(String name, Consumer<Integer> handleBonuses,
                         HashMap<Item, Float> offerings, Collection<Quest> quests,
                         @Nullable GodEntitySupplier<?> entitySupplier) throws NullPointerException{
        try {
            return new God(
                    ResourceLocation.fromNamespaceAndPath(NAMESPACE, name),
                    ATTACHMENT_TYPE, handleBonuses,
                    Component.translatable("god.%s.%s.name".formatted(NAMESPACE, name)),
                    entitySupplier == null ? SAVED_SUPPLIERS.get(name) : entitySupplier,
                    offerings, quests
            );
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + ": please provide GodEntitySupplier!");
        }

    }

    /**
     * The equivalent of {@code new GodEntitySupplier()} but with only two parameters.
     * Use it before {@linkplain #createGod} for shorter process.
     * @param name
     * The name of the entity. Need to be the same as for {@linkplain #createGod} if you want to
     * short the process.
     * @param clazz
     * The same as for direct instantiation.
     * @return
     * The {@link GodEntitySupplier} you just created.
     */
    public <T extends GodEntity> GodEntitySupplier<T> createEntitySupplier(String name, Class<T> clazz) {
        GodEntitySupplier<T> result = new GodEntitySupplier<>(name, ENTITY_TYPE, ITEM, clazz);
        SAVED_SUPPLIERS.put(name, result);
        return result;
    }

}
