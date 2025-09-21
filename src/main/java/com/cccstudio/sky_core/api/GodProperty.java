package com.cccstudio.sky_core.api;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;


import org.jetbrains.annotations.NotNull;


public class GodProperty extends Property<God> {

    public GodProperty(String name) {
        super(name, God.class);
    }

    @Override
    public @NotNull Collection<God> getPossibleValues() {
        Collection<God> godNames = Core.GODS.stream().toList();
        return Stream.concat(godNames.stream(), Stream.of((God) null)).toList();
    }

    @Override
    public String getName(God god) {
        return god.getName();
    }

    @Override
    public Optional<God> getValue(String value) {
        return Optional.ofNullable(Core.GOD_LOCATIONS.get(ResourceLocation.parse(value)));
    }

    public static GodProperty create(String name) {
        return new GodProperty(name);
    }

}
