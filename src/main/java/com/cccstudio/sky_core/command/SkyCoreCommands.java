package com.cccstudio.sky_core.command;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import com.cccstudio.sky_core.api.quest.Quest;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Create the two SkyCore-related commands:
 * {@code /godpoints} and {@code /godquests}.
 * @see com.cccstudio.sky_core.event.Event#registerCommands
 */
public class SkyCoreCommands {

    private static final List<String> GOD_ACTIONS = List.of("add", "sub", "set", "reset", "get");

    private static final SuggestionProvider<CommandSourceStack> GOD_ACTION_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(GOD_ACTIONS, builder);

    private static final SuggestionProvider<CommandSourceStack> QUEST_ACTION_SUGGESTION =
            (context, builder) ->
                    SharedSuggestionProvider.suggest(List.of("grant", "revoke"), builder);

    private static final DynamicCommandExceptionType INVALID_GOD_EXCEPTION =
            new DynamicCommandExceptionType((god) -> Component.translatable("command.sky_core.godpoints.unknown_god"));

    private static final DynamicCommandExceptionType INVALID_ACTION_EXCEPTION =
            new DynamicCommandExceptionType((action) -> Component.translatable("command.sky_core.godpoints.unknown_action", action));

    private static final DynamicCommandExceptionType INVALID_QUEST_EXCEPTION =
            new DynamicCommandExceptionType((quest) -> Component.translatable("command.sky_core.godquests.unknown_quest"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("godpoints")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("get")
                                .then(Commands.argument("god", ResourceOrTagKeyArgument.resourceOrTagKey(Core.GOD_REGISTRY_KEY))
                                        .executes(ctx -> getPoints(ctx, ctx.getSource().getPlayerOrException()))
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(ctx -> resetPoints(ctx, ctx.getSource().getPlayerOrException()))                        )
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(GOD_ACTION_SUGGESTIONS)
                                .then(Commands.argument("god", ResourceOrTagKeyArgument.resourceOrTagKey(Core.GOD_REGISTRY_KEY))
                                        .then(Commands.argument("arg", IntegerArgumentType.integer())
                                                .executes(ctx -> execGodpoints(
                                                        ctx,
                                                        false
                                                ))
                                                .then(Commands.argument("updateRelated", BoolArgumentType.bool())
                                                        .executes(ctx -> execGodpoints(
                                                                ctx,
                                                                BoolArgumentType.getBool(ctx, "updateRelated")
                                                        ))
                                                )
                                        )

                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.players())

                                .then(Commands.literal("reset")
                                        .executes(ctx -> resetPoints(ctx, EntityArgument.getPlayer(ctx, "target")))
                                )


                                .then(Commands.literal("get")
                                        .then(Commands.argument("god", ResourceOrTagKeyArgument.resourceOrTagKey(Core.GOD_REGISTRY_KEY))
                                                .executes(ctx -> getPoints(ctx, EntityArgument.getPlayer(ctx, "target")))
                                        )
                                )

                                .then(Commands.argument("action", StringArgumentType.word())
                                        .suggests(GOD_ACTION_SUGGESTIONS)
                                        .then(Commands.argument("god", ResourceOrTagKeyArgument.resourceOrTagKey(Core.GOD_REGISTRY_KEY))
                                                .then(Commands.argument("arg", IntegerArgumentType.integer())
                                                        .executes(ctx -> execGodpoints(
                                                                ctx,
                                                                false
                                                        ))
                                                        .then(Commands.argument("updateRelated", BoolArgumentType.bool())
                                                                .executes(ctx -> execGodpoints(
                                                                        ctx,
                                                                        BoolArgumentType.getBool(ctx, "updateRelated")
                                                                ))
                                                        )
                                                )

                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("godquests")
                        .then(Commands.argument("players", EntityArgument.players()))
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(QUEST_ACTION_SUGGESTION))
                        .then(Commands.argument("quest", ResourceOrTagKeyArgument.resourceOrTagKey(Core.QUEST_REGISTRY_KEY)))
                        .then(Commands.argument("points", IntegerArgumentType.integer()))
                        .executes(SkyCoreCommands::execQuests)
                        .then(Commands.argument("gods", ResourceOrTagKeyArgument.resourceOrTagKey(Core.GOD_REGISTRY_KEY))
                                .executes(SkyCoreCommands::execQuests))
        );

    }

    private static Collection<God> getGodsFrom(TagKey<God> key) {
        return Core.GODS.stream().filter(god -> god.is(key)).toList();
    }

    private static int execGodpoints(CommandContext<CommandSourceStack> ctx, boolean updateRelated) throws CommandSyntaxException {

        String action = StringArgumentType.getString(ctx, "action");
        int arg = IntegerArgumentType.getInteger(ctx, "arg");
        Collection<ServerPlayer> targets;
        try {
            targets = EntityArgument.getPlayers(ctx, "targets");
        } catch (CommandSyntaxException e) {
            targets = List.of(ctx.getSource().getPlayerOrException());
        }
        Collection<God> gods;
        Either<ResourceKey<God>, TagKey<God>> god = ResourceOrTagKeyArgument.getResourceOrTagKey
                (ctx, "god", Core.GOD_REGISTRY_KEY, INVALID_GOD_EXCEPTION).unwrap();
        if(god.left().isPresent()) {
            gods = List.of(Core.GOD_LOCATIONS.get(god.left().get().location()));
        } else {
            gods = getGodsFrom(god.right().get());
        }

        int successes = 0;
        for(ServerPlayer player : targets) {
            for(God selectedGod : gods) {
                switch (action) {
                    case "add" -> selectedGod.addPoints(player, arg, updateRelated);
                    case "sub" -> selectedGod.addPoints(player, -arg, updateRelated);
                    case "set" -> selectedGod.addPoints
                            (player, player.getData(selectedGod.getPoints()) - arg, updateRelated);
                    default -> {
                        successes--;
                        throw new CommandSyntaxException(INVALID_ACTION_EXCEPTION, Component.empty());
                    }
                }
                successes++;
            }
        }

        return Math.max(successes, 0);
    }

    private static int resetPoints(CommandContext<CommandSourceStack> ctx, Player player) {
        God.resetPoints(player);

        ctx.getSource().sendSuccess(() -> Component.translatable("command.sky_core.godpoints.reset_success"), false);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static int getPoints(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException {
        Either<ResourceKey<God>, TagKey<God>> god = ResourceOrTagKeyArgument.getResourceOrTagKey
                (ctx, "god", Core.GOD_REGISTRY_KEY, INVALID_GOD_EXCEPTION).unwrap();

        if(god.left().isPresent()) {
            int points = player.getData(Core.GOD_LOCATIONS.get(god.left().get().location()).getPoints());
            ctx.getSource().sendSuccess((Supplier<Component>) Component.literal
                    ("[" + god.left().get().location() + "] → " + points), false);
            return Command.SINGLE_SUCCESS;
        } else {
            int successes = 0;
            TagKey<God> tag = god.right().get();
            for(God selectedGod : Core.GODS) {
                if(selectedGod.is(tag)) {
                    int points = player.getData(selectedGod.getPoints().get());
                    ctx.getSource().sendSuccess((Supplier<Component>) Component.literal
                            ("[" + selectedGod.getName() + "] → " + points), false);
                    successes++;
                }
            }
            return successes;
        }
    }

    private static int execQuests(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        final int point = IntegerArgumentType.getInteger(context, "points");
        final Collection<ServerPlayer> PLAYERS = EntityArgument.getPlayers(context, "players");
        final String ACTION = StringArgumentType.getString(context, "action");

        final Either<ResourceKey<Quest>, TagKey<Quest>> tag = ResourceOrTagKeyArgument.getResourceOrTagKey
                (context, "quest", Core.QUEST_REGISTRY_KEY, INVALID_QUEST_EXCEPTION).unwrap();
        final Quest QUEST = tag.left().isPresent()
                ? Core.QUEST_LOCATIONS.get(tag.left().get().location())
                : Core.QUEST_LOCATIONS.values().stream()
                .filter(q -> q.is(tag.right().get()))
                .findFirst()
                .orElseThrow(() -> INVALID_QUEST_EXCEPTION.create(tag));

        Collection<God> GODS;
        try {
            Either<ResourceKey<God>, TagKey<God>> god = ResourceOrTagKeyArgument.getResourceOrTagKey
                    (context, "gods", Core.GOD_REGISTRY_KEY, INVALID_GOD_EXCEPTION).unwrap();
            if(god.left().isPresent()) {
                GODS = List.of(Core.GOD_LOCATIONS.get(god.left().get().location()));
            } else {
                GODS = getGodsFrom(god.right().get());
            }
        } catch (CommandSyntaxException e) {
            GODS = QUEST.USING_GODS;
        }

        for(Player player : PLAYERS) {
            if(ACTION.equals("grant")) {
                QUEST.grant(player, point, GODS);
            } else {
                QUEST.revoke(player, point, GODS);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
