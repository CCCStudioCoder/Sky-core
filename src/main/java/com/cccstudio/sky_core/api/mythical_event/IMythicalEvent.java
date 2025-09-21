package com.cccstudio.sky_core.api.mythical_event;

import com.cccstudio.sky_core.Core;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Range;

import java.lang.annotation.Repeatable;

@SuppressWarnings("unused")
public interface IMythicalEvent {

    /**
     * This is an automatic registering of the classes.
     */
    @SuppressWarnings("unchecked")
    default void register() {
        Core.MYTHICAL_EVENTS.add((Class<IMythicalEvent>) this.getClass());
    }

    @Range(from = 1, to = 100)
    default int getWeight() {
        return 50;
    }

    default boolean isApplicable(MinecraftServer server) {
        return true;
    }

    void apply(MythicalEventContext context);

    default void stop(MythicalEventContext context) {}

    void reward(MythicalEventContext context);

    default void tick(MythicalEventContext context) {}

}
