package com.cccstudio.sky_core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.cccstudio.sky_core.Core.gods;
import static com.cccstudio.sky_core.God.*;

public class GodPointsCommand {

    private static final List<String> ACTIONS = List.of("add", "sub", "set", "reset", "get");

    private static final SuggestionProvider<CommandSourceStack> ACTION_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(ACTIONS, builder);

    private static final SuggestionProvider<CommandSourceStack> GOD_SUGGESTIONS = (context, builder) -> {
        List<String> names = gods.stream()
                .map(God::getName)
                .collect(Collectors.toList());
        return SharedSuggestionProvider.suggest(names, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("godpoints")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("get")
                                .then(Commands.argument("god", StringArgumentType.word())
                                        .suggests(GOD_SUGGESTIONS)
                                        .executes(ctx -> {
                                            String dudeName = StringArgumentType.getString(ctx, "god").toLowerCase();
                                            God god = getDudeByName(dudeName);
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            if (god == null) {
                                                ctx.getSource().sendFailure(Component.translatable("command.sky_core.godpoints.unknown_god"));
                                                return 0;
                                            }

                                            AttachmentType<Integer> attachment = god.getPoints().get();

                                            ctx.getSource().sendSuccess(() -> Component.literal("[" + god.getName() + "] → " + player.getData(attachment) + " for " + player.getDisplayName().getString()), false);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    resetPoints(player);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Points reset for one player."), false);
                                    return Command.SINGLE_SUCCESS;
                                })                        )
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(ACTION_SUGGESTIONS)
                                .then(Commands.argument("dude", StringArgumentType.word())
                                        .suggests(GOD_SUGGESTIONS)
                                        .then(Commands.argument("arg", IntegerArgumentType.integer())
                                                .executes(ctx -> exec(
                                                        ctx,
                                                        Collections.singleton(ctx.getSource().getPlayerOrException()),
                                                        StringArgumentType.getString(ctx, "action"),
                                                        StringArgumentType.getString(ctx, "dude").toLowerCase(),
                                                        IntegerArgumentType.getInteger(ctx, "arg"),
                                                        false
                                                ))
                                                .then(Commands.argument("updateRelated", BoolArgumentType.bool())
                                                        .executes(ctx -> exec(
                                                                ctx,
                                                                Collections.singleton(ctx.getSource().getPlayerOrException()),                                                                StringArgumentType.getString(ctx, "action"),
                                                                StringArgumentType.getString(ctx, "dude").toLowerCase(),
                                                                IntegerArgumentType.getInteger(ctx, "arg"),
                                                                BoolArgumentType.getBool(ctx, "updateRelated")
                                                        ))
                                                )
                                        )

                                )
                        )
                        .then(Commands.argument("target", EntityArgument.players())

                                .then(Commands.literal("reset")
                                        .executes(ctx -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
                                            for (ServerPlayer target : targets) {
                                                resetPoints(target);
                                            }
                                            ctx.getSource().sendSuccess(() -> Component.literal("Points reset for " + targets.size() + " player(s)."), false);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )


                                .then(Commands.literal("get")
                                        .then(Commands.argument("dude", StringArgumentType.word())
                                                .suggests(GOD_SUGGESTIONS)
                                                .executes(ctx -> {
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
                                                    String dudeName = StringArgumentType.getString(ctx, "god").toLowerCase();
                                                    God god = getDudeByName(dudeName);

                                                    if (god == null) {
                                                        ctx.getSource().sendFailure(Component.literal("Unknown god: " + dudeName));
                                                        return 0;
                                                    }

                                                    AttachmentType<Integer> attachment = god.getPoints().get();

                                                    for (ServerPlayer target : targets) {
                                                        int value = target.getData(attachment);
                                                        ctx.getSource().sendSuccess(() -> Component.literal("[" + god.getName() + "] → " + value + " for " + target.getDisplayName().getString()), false);
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )

                                .then(Commands.argument("action", StringArgumentType.word())
                                        .suggests(ACTION_SUGGESTIONS)
                                        .then(Commands.argument("dude", StringArgumentType.word())
                                                .suggests(GOD_SUGGESTIONS)
                                                .then(Commands.argument("arg", IntegerArgumentType.integer())
                                                        .executes(ctx -> exec(
                                                                ctx,
                                                                EntityArgument.getPlayers(ctx, "target"),
                                                                StringArgumentType.getString(ctx, "action"),
                                                                StringArgumentType.getString(ctx, "dude").toLowerCase(),
                                                                IntegerArgumentType.getInteger(ctx, "arg"),
                                                                false
                                                        ))
                                                        .then(Commands.argument("updateRelated", BoolArgumentType.bool())
                                                                .executes(ctx -> exec(
                                                                        ctx,
                                                                        EntityArgument.getPlayers(ctx, "target"),
                                                                        StringArgumentType.getString(ctx, "action"),
                                                                        StringArgumentType.getString(ctx, "dude").toLowerCase(),
                                                                        IntegerArgumentType.getInteger(ctx, "arg"),
                                                                        BoolArgumentType.getBool(ctx, "updateRelated")
                                                                ))
                                                        )
                                                )

                                        )
                                )
                        )
        );

    }

    private static int exec(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets,
                            String action, String dudeName, int value, boolean updateRelated) {

        God god = getDudeByName(dudeName);
        if (god == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown god: " + dudeName));
            return 0;
        }

        AttachmentType<Integer> attachment = god.getPoints().get();

        switch (action.toLowerCase()) {
            case "add" -> targets.forEach(p -> p.setData(attachment, p.getData(attachment) + value));
            case "sub" -> targets.forEach(p -> p.setData(attachment, p.getData(attachment) - value));
            case "set" -> targets.forEach(p -> p.setData(attachment, value));
            default -> {
                ctx.getSource().sendFailure(Component.literal("Invalid action (add/sub/set)"));
                return 0;
            }
        }

        if(updateRelated) {
            targets.forEach(p -> god.updateRelated(p, value));
        }

        int updated = targets.iterator().next().getData(attachment);
        ctx.getSource().sendSuccess(() -> Component.literal("[" + god.getName() + "] → " + updated), false);

        return Command.SINGLE_SUCCESS;

    }

    private static God getDudeByName(String name) {
        for (God d : gods) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }
}
