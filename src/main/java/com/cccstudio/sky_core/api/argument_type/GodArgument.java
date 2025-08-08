package com.cccstudio.sky_core.api.argument_type;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Used by commands when a god is needed.
 */
public class GodArgument implements ArgumentType<God> {

    private final boolean MULTIPLE;

    public GodArgument(boolean multiple) {
        MULTIPLE = multiple;
    }

    public static God getGod(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, God.class);
    }
    public static List<God> getGods(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, List.class);
    }

    public static GodArgument god() {
        return new GodArgument(false);
    }
    public static GodArgument gods() {
        return new GodArgument(true);
    }

    @Override
    public God parse(StringReader reader) throws CommandSyntaxException {
        return Core.GOD_LOCATIONS.get(ResourceLocation.read(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestion = new ArrayList<>(Core.GODS.stream().map(God::getName).toList());
        if (MULTIPLE) {
            suggestion.add("*");
        }
        return SharedSuggestionProvider.suggest(suggestion, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("sky_core:cubos", "*");
    }
}
