package com.cccstudio.sky_core.api.quest;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Dialog {

    private final HashMap<Dialog.Branch, Function<Dialog.Context, Integer>> BRANCHES =
            new HashMap<>();

    protected Dialog() {
    }

    // This ensures listening of all dialogs

    /**
     * Use this instead of {@code new Dialog()}.
     */
    public static Dialog create() {
        Dialog dialog = new Dialog();
        Core.DIALOGS.add(dialog);
        return dialog;
    }

    /**
     * Dialogs are trees of {@link Branch}. Every branch is a way that the dialog can take.
     * @param text
     * What says this branch. A String can also be used instead of {@linkplain Component#literal}.
     * @param weight
     * A simple function to compute how rare is this branch, based on the {@link Context}. <br/>
     * Code like {@code () -> 3} can be used is the context don't matter. <br/>
     * You can use the two ways for the same dialog.
     * @return
     * The branch you just created. Use it as a starting point for other branches that exist only when
     * the dialog "pass" this way.
     */
    public Dialog.Branch createBranch(MutableComponent text, Function<Dialog.Context, Integer> weight,
                                      @Nullable Consumer<Dialog.Context> additional_effect) {
        Dialog.Branch branch = new Dialog.Branch(text, additional_effect);
        BRANCHES.put(branch, weight);
        return branch;
    }
    public Dialog.Branch createBranch(String text, Function<Dialog.Context, Integer> weight,
                                      @Nullable Consumer<Dialog.Context> additional_effect) {
        return createBranch(Component.literal(text), weight, additional_effect);
    }

    @Nullable
    protected static Dialog.Branch roll(HashMap<Dialog.Branch, Function<Dialog.Context, Integer>> branches,
                     Dialog.Context context) {
        HashMap<Dialog.Branch, Integer> possibilities = new HashMap<>();
        int totalWeight = 0;
        for (Dialog.Branch branch : branches.keySet()) {
            int weight = branches.get(branch).apply(context);
            possibilities.put(branch, weight);
            totalWeight += weight;
        }

        int roll = (int) (Math.random() * (totalWeight + 1));
        int current = 0;
        Dialog.Branch rolledBranch = null;
        for (Dialog.Branch branch : branches.keySet()) {
            current += possibilities.get(branch);
            if(current > roll) {
                rolledBranch = branch;
                break;
            }
        }
        assert rolledBranch != null;
        context.chat.add(rolledBranch.TEXT);
        // apply additional effects
        assert rolledBranch.ADDITIONAL_EFFECT != null;
        rolledBranch.ADDITIONAL_EFFECT.accept(context);

        return rolledBranch;
    }

    /**
     * Start a functional dialog.
     * @param player
     * The player starting the dialog.
     * @param god
     * The god who talks to the player.
     */
    public void launch(Player player, God god) {
        Context context = new Context(player, god);
        roll(BRANCHES, context).apply(context);
    }

    /**
     * A part or way of a dialog.
     */
    public static class Branch {

        private final MutableComponent TEXT;

        private final @Nullable Consumer<Dialog.Context> ADDITIONAL_EFFECT;

        private final HashMap<Dialog.Branch, Function<Dialog.Context, Integer>> BRANCHES =
                new HashMap<>();

        protected Branch(MutableComponent text, @Nullable Consumer<Dialog.Context> additional_effect) {
            TEXT = text;
            ADDITIONAL_EFFECT = additional_effect;
        }

        /**
         * To create branches of branch using the same parameter as for root dialogs.
         */
        public Dialog.Branch createBranch(MutableComponent text, Function<Dialog.Context, Integer> weight,
                                          @Nullable Consumer<Dialog.Context> additional_effect) {
            Dialog.Branch branch = new Dialog.Branch(text, additional_effect);
            BRANCHES.put(branch, weight);
            return branch;
        }
        public Dialog.Branch createBranch(String text, Function<Dialog.Context, Integer> weight,
                                          @Nullable Consumer<Dialog.Context> additional_effect) {
            return createBranch(Component.literal(text), weight, additional_effect);
        }

        /**
         * Equivalent of {@link Dialog#launch} but for branches.
         */
        @Nullable
        public Dialog.Branch apply(Context context) {
            assert ADDITIONAL_EFFECT != null;
            ADDITIONAL_EFFECT.accept(context);

            Dialog.Branch branch = roll(BRANCHES, context);
            assert branch != null;
            branch.apply(context);

            return branch;
        }

    }

    /**
     * Used to provide variables depending on the context of the dialog.
     */
    public static class Context {

        private final Player PLAYER;
        private final God GOD;

        private final Level LEVEL;

        private final List<MutableComponent> chat = new ArrayList<>();

        protected Context(Player player, God god) {
            PLAYER = player;
            GOD = god;
            LEVEL = player.level();
        }

        /**
         * @return
         * The engaged player
         */
        public Player getPlayer() {
            return PLAYER;
        }

        /**
         * @return
         * The engaged god
         */
        public God getGod() {
            return GOD;
        }

        /**
         * @return
         * The level where this dialog happens.
         */
        public Level getLevel() {
            return LEVEL;
        }

        /**
         * @return
         * What has been already says by the god.
         */
        public List<MutableComponent> getChat() {
            return chat;
        }
    }

}
