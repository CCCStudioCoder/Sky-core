package com.cccstudio.sky_core.api.mythical_event;

import com.cccstudio.sky_core.Core;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class MythicalEventHandler {

    @Nullable
    private static IMythicalEvent currentEvent = null;

    @Nullable
    private static MythicalEventContext eventContext = null;

    public static IMythicalEvent rollEvent(MinecraftServer server) {
        final List<IMythicalEvent> availableEvents = new ArrayList<>();
        int totalWeight = 0;

        for(IMythicalEvent event : Core.MYTHICAL_EVENTS.stream().map(clazz -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("All IMythicalEvent classes needs always an constructor without parameters:"
                        + e.getMessage());
            }
        }).toList()) {
            if(event.isApplicable(server)) {
                availableEvents.add(event);
                totalWeight += event.getWeight();
            }
        }

        IMythicalEvent chosen = availableEvents.getFirst();
        int rand = (int) (Math.random() * totalWeight);
        for(int i = 0, k = 0; k < totalWeight; i ++) {
            IMythicalEvent event = availableEvents.get(i);
            k += event.getWeight();
            if(k > rand) {
                chosen = event;
            }
        }
        currentEvent = chosen;
        eventContext = new MythicalEventContext(server);
        return chosen;
    }

    public static boolean handleEvent() {
        if(currentEvent != null) {
            currentEvent.tick(eventContext);
            return true;
        } else {
            return false;
        }
    }

    public static void stopEvent() {
        assert currentEvent != null;
        currentEvent.stop(eventContext);

        currentEvent = null;
        eventContext = null;
    }

    public static boolean isExecuting() {
        return currentEvent != null;
    }

}
