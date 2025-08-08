package com.cccstudio.sky_core.api.argument_type;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import com.cccstudio.sky_core.api.quest.Quest;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestArgument implements ArgumentType<Quest> {

    private QuestArgument() {}

    public static QuestArgument quest() {
        return new QuestArgument();
    }

    public static Quest getQuest(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Quest.class);
    }

    @Override
    public Quest parse(StringReader reader) throws CommandSyntaxException {
        return Core.QUEST_LOCATIONS.get(ResourceLocation.read(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Core.QUEST_LOCATIONS.values().stream().map(quest -> quest.getPath().getPath()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("sky_core:kill_enderdragon", "sky_core:get_elytra");
    }
}
