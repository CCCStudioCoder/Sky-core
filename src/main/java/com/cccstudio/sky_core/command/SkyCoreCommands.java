package com.cccstudio.sky_core.command;

import com.cccstudio.sky_core.api.argument_type.GodArgument;
import com.cccstudio.sky_core.api.argument_type.QuestArgument;
import com.cccstudio.sky_core.api.god.God;
import com.cccstudio.sky_core.api.quest.Quest;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import java.util.Collection;
import java.util.List;

import static com.cccstudio.sky_core.api.god.God.*;

public class SkyCoreCommands {

    private static final List<String> GOD_ACTIONS = List.of("add", "sub", "set", "reset", "get");

    private static final SuggestionProvider<CommandSourceStack> GOD_ACTION_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(GOD_ACTIONS, builder);

    private static final SuggestionProvider<CommandSourceStack> QUEST_ACTION_SUGGESTION =
            (context, builder) ->
                    SharedSuggestionProvider.suggest(List.of("grant", "revoke"), builder);


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                net.minecraft.commands.Commands.literal("godpoints")
                        .requires(cs -> cs.hasPermission(2))
                        .then(net.minecraft.commands.Commands.literal("get")
                                .then(net.minecraft.commands.Commands.argument("god", GodArgument.god())
                                        .executes(ctx -> {
                                            God god = GodArgument.getGod(ctx, "god");
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
                        .then(net.minecraft.commands.Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    resetPoints(player);
                                    ctx.getSource().sendSuccess(() -> Component.translatable("command.sky_core.godpoints.reset_success"), false);
                                    return Command.SINGLE_SUCCESS;
                                })                        )
                        .then(net.minecraft.commands.Commands.argument("action", StringArgumentType.word())
                                .suggests(GOD_ACTION_SUGGESTIONS)
                                .then(net.minecraft.commands.Commands.argument("god", GodArgument.god())
                                        .then(net.minecraft.commands.Commands.argument("arg", IntegerArgumentType.integer())
                                                .executes(ctx -> execGodpoints(
                                                        ctx,
                                                        false
                                                ))
                                                .then(net.minecraft.commands.Commands.argument("updateRelated", BoolArgumentType.bool())
                                                        .executes(ctx -> execGodpoints(
                                                                ctx,
                                                                BoolArgumentType.getBool(ctx, "updateRelated")
                                                        ))
                                                )
                                        )

                                )
                        )
                        .then(net.minecraft.commands.Commands.argument("target", EntityArgument.players())

                                .then(net.minecraft.commands.Commands.literal("reset")
                                        .executes(ctx -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
                                            for (ServerPlayer target : targets) {
                                                resetPoints(target);
                                            }
                                            ctx.getSource().sendSuccess(() -> Component.translatable("command.sky_core.godpoints.several_reset_success"), false);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )


                                .then(net.minecraft.commands.Commands.literal("get")
                                        .then(net.minecraft.commands.Commands.argument("god", GodArgument.god())
                                                .executes(ctx -> {
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
                                                    God god = GodArgument.getGod(ctx, "god");

                                                    if (god == null) {
                                                        ctx.getSource().sendFailure(Component.translatable("command.sky_core.godpoints.unknown_god"));
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

                                .then(net.minecraft.commands.Commands.argument("action", StringArgumentType.word())
                                        .suggests(GOD_ACTION_SUGGESTIONS)
                                        .then(net.minecraft.commands.Commands.argument("god", GodArgument.god())
                                                .then(net.minecraft.commands.Commands.argument("arg", IntegerArgumentType.integer())
                                                        .executes(ctx -> execGodpoints(
                                                                ctx,
                                                                false
                                                        ))
                                                        .then(net.minecraft.commands.Commands.argument("updateRelated", BoolArgumentType.bool())
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
                Commands.literal("godquest")
                        .then(Commands.argument("players", EntityArgument.players()))
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(QUEST_ACTION_SUGGESTION))
                        .then(Commands.argument("quest", QuestArgument.quest()))
                        .then(Commands.argument("gods", GodArgument.gods())
                                .executes(ctx -> execQuest(ctx, 0)))
                        .then(Commands.argument("points", IntegerArgumentType.integer()))
                        .executes(ctx -> execQuest(ctx, IntegerArgumentType.getInteger(ctx, "point")))
        );

    }

    private static int execGodpoints(CommandContext<CommandSourceStack> ctx, boolean updateRelated) throws CommandSyntaxException {

        Collection<ServerPlayer> targets;
        String action = StringArgumentType.getString(ctx, "action");
        God god = GodArgument.getGod(ctx, "god");
        int value = IntegerArgumentType.getInteger(ctx, "arg");

        try {
            targets = EntityArgument.getPlayers(ctx, "target");
        } catch (CommandSyntaxException e) {
            targets = List.of(ctx.getSource().getPlayerOrException());
        } catch (RuntimeException e) {
            //TODO translations
            ctx.getSource().sendFailure(Component.translatable("command.sky_core.godpoints.not_a_player")); //command source isn't a player. Please give at least one target!
            throw new RuntimeException(e);
        }

        if (god == null) {
            ctx.getSource().sendFailure(Component.translatable("command.sky_core.godpoints.unknown_god"));
            return 0;
        }

        AttachmentType<Integer> attachment = god.getPoints().get();

        switch (action.toLowerCase()) {
            case "add" -> targets.forEach(p -> p.setData(attachment, p.getData(attachment) + value));
            case "sub" -> targets.forEach(p -> p.setData(attachment, p.getData(attachment) - value));
            case "set" -> targets.forEach(p -> p.setData(attachment, value));
            default -> {
                ctx.getSource().sendFailure(Component.translatable("command.sky_core.godpoints.invalid_action"));
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

    private static int execQuest(CommandContext<CommandSourceStack> context, final int point)
            throws CommandSyntaxException {
        final Collection<ServerPlayer> PLAYERS = EntityArgument.getPlayers(context, "players");
        final String ACTION = StringArgumentType.getString(context, "action");
        final Quest QUEST = QuestArgument.getQuest(context, "quest");
        final Collection<God> GODS = GodArgument.getGods(context, "gods");

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
