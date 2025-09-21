package com.cccstudio.sky_core.api.quest;

import com.cccstudio.sky_core.SkyCore;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides a capabilities handler for the quests.
 */
@SuppressWarnings("deprecated")
public class QuestHandler {

    public static final EntityCapability<QuestCollection, Void> ENGAGED_QUESTS = EntityCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "engaged_quests"),
            QuestCollection.class
    );

    public static final EntityCapability<QuestCollection, Void> FINISHED_QUESTS = EntityCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "finished_quests"),
            QuestCollection.class
    );

    /**
     * Capabilities don't support classes with generic types, so a special class is needed.
     */
    public static class QuestCollection implements Collection<Quest> {

        private final Collection<Quest> CONTENT = new ArrayList<>();

        @Override
        public int size() {
            return CONTENT.size();
        }

        @Override
        public boolean isEmpty() {
            return CONTENT.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return CONTENT.contains(o);
        }

        @Override
        public @NotNull Iterator<Quest> iterator() {
            return CONTENT.iterator();
        }

        @Override
        public @NotNull Object @NotNull [] toArray() {
            return CONTENT.toArray();
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull <T> T @NotNull [] toArray(T @NotNull [] a) {
            return (T[]) Arrays.stream(a).toArray();
        }

        @Override
        public boolean add(Quest quest) {
            return CONTENT.add(quest);
        }

        @Override
        public boolean remove(Object o) {
            return CONTENT.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return CONTENT.containsAll(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends Quest> c) {
            return CONTENT.addAll(c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return CONTENT.removeAll(c);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return CONTENT.retainAll(c);
        }

        @Override
        public void clear() {
            CONTENT.clear();
        }

        @SuppressWarnings("unused")
        public Collection<Quest> cast() {
            return CONTENT;
        }
    }

}
