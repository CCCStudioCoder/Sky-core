package com.cccstudio.sky_core.api;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;


import org.jetbrains.annotations.NotNull;

public class GodProperty extends Property<String> {

    public GodProperty(String name, Class<String> clazz) {
        super(name, clazz);
    }

    @Override
    public @NotNull Collection<String> getPossibleValues() {
        Collection<String> godNames = Core.GODS.stream().map(God::getName).toList();
        // "disabled" is only for ensure that there's always more than one possibility.
        return Stream.concat(godNames.stream(), Stream.concat(Stream.of("none"), Stream.of("disabled"))).toList();
    }

    public static GodProperty create(String name) {
        return new GodProperty(name, String.class);
    }

    @Override
    public @NotNull String getName(@NotNull String value) {
        return value;
    }

    @Override
    public @NotNull Optional<String> getValue(@NotNull String value) {
        return this.getPossibleValues().contains(value) ? Optional.of(value) : Optional.empty();
    }
}
