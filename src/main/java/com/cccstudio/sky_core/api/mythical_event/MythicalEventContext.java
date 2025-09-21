package com.cccstudio.sky_core.api.mythical_event;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class MythicalEventContext {

    private final MinecraftServer SERVER;

    private final HashMap<String, Object> CONTEXT = new HashMap<>();

    public MythicalEventContext(MinecraftServer server) {
        SERVER = server;
    }

    public MinecraftServer getServer() {
        return SERVER;
    }

    public void write(String key, Object value) {
        if(CONTEXT.containsKey(key)) {
            CONTEXT.replace(key, value);
        } else {
            CONTEXT.put(key, value);
        }
    }

    public Object get(String key) {
        return CONTEXT.get(key);
    }

    public void remove(String key) {
        CONTEXT.remove(key);
    }

}
