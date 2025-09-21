package com.cccstudio.sky_core.api.quest;

import com.cccstudio.sky_core.Core;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.cccstudio.sky_core.api.god.God;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Quest {

    private final ResourceLocation PATH;

    @Nullable
    private final Consumer<Player> ADDITIONAL_EFFECT;

    public Collection<God> USING_GODS;

    private final Holder.Reference<Quest> REF = Core.QUEST_REGISTRY.createIntrusiveHolder(this);

    public Quest(ResourceLocation path, @Nullable Consumer<Player> additional_effect) {
        ADDITIONAL_EFFECT = additional_effect;
        PATH = path;
        ResourceKey<Quest> KEY = ResourceKey.create(Core.QUEST_REGISTRY_KEY, path);

        Core.QUEST_LOCATIONS.put(PATH, this);
    }

    public ResourceLocation getPath() {
        return PATH;
    }

    /**
     * Use it to tell the system "this player has completed this quest", assuming they started it.
     * @param player
     * Who complete this quest.
     * @param point
     * How many points he gets for completing the quest. You can use different values for the same quest,
     * so that the player receives points based on how well the quest is completed.
     * @param god
     * What specific god it affects. If not specified, it will affect all gods that use this quest.
     * Can be a {@link List} of gods.
     * If the specified god(s) do not use this quest, an {@link IllegalArgumentException} will be thrown.
     */
    public void grant(Player player, int point, God god, boolean check) {
        if(USING_GODS.contains(god)) {
            if (Objects.requireNonNull(player.getCapability(QuestHandler.ENGAGED_QUESTS)).contains(this)) {
                god.addPoints(player, point);
                Objects.requireNonNull(player.getCapability(QuestHandler.FINISHED_QUESTS)).add(this);
                assert ADDITIONAL_EFFECT != null;
                ADDITIONAL_EFFECT.accept(player);
            }
        } else if(check) {
            throw new IllegalArgumentException("This god doesn't use this quest!");
        }
    }
    public void grant(Player player, int point, God god) {
        grant(player, point, god, true);
    }
    public void grant(Player player, int point, @Nullable Collection<God> gods) {
        if(gods == null){
            for(God god : USING_GODS) { grant(player, point, god); }
        } else {
            for(God god : gods) { grant(player, point, god); }
        }
    }

    public void revoke(Player player, int point, God god) {
        if(USING_GODS.contains(god) && player.getCapability(QuestHandler.FINISHED_QUESTS).contains(this)) {
            god.addPoints(player, -point);
            Objects.requireNonNull(player.getCapability(QuestHandler.FINISHED_QUESTS)).remove(this);
        } else {
            throw new IllegalArgumentException("This god doesn't use this quest or this quest hasn't be accomplished by this player.");
        }
    }
    public void revoke(Player player, int point, @Nullable Collection<God> gods) {
        if(gods == null){
            for(God god : USING_GODS) { revoke(player, point, god); }
        } else {
            for(God god : gods) { revoke(player, point, god); }
        }
    }

    public boolean is(TagKey<Quest> tag) {
        return REF.is(tag);
    }

}
