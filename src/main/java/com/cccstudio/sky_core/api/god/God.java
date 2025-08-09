package com.cccstudio.sky_core.api.god;

import com.cccstudio.sky_core.Core;
import com.cccstudio.sky_core.api.god.entity.GodEntitySupplier;
import com.cccstudio.sky_core.api.quest.Quest;
import com.mojang.serialization.Codec;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cccstudio.sky_core.Core.*;

/**
 * This is the most important class of the API.
 * It provides the constructor and all the related methods.
 */
public class God {

    private final ResourceLocation PATH;

    private static Supplier<AttachmentType<Integer>> POINT;

    private final Collection<God> FRIENDS = new ArrayList<>();
    private final Collection<God> ENEMIES = new ArrayList<>();

    private static Consumer<Integer> BONUS_HANDLER;

    MutableComponent DISPLAY_NAME;

    private final GodEntitySupplier<?> GOD_ENTITY;

    private final HashMap<Item, Float> OFFERINGS;

    private final Collection<Quest> QUESTS;


    /**
     * Called by a direct {@code new God()}
     * @param path
     * A {@link ResourceLocation} for where is "stored" the god.
     * @param attachmentTypeRegister
     * A {@link DeferredRegister} for registration of {@link #POINT}.
     * @param handleBonus
     * The {@link #BONUS_HANDLER} function described above.
     * @param name
     * The {@link MutableComponent} for the display name, can be a translation key
     * or a literal.
     * @param entity
     * Lead to a single {@link Entity}, the god;
     * @param offerings
     * A {@link HashMap} with all possible offerings, each time with an {@link Item}, what can be offered
     * and a float between 0 and 1, the probability to win a point after offering.
     * @param quests
     * All the quests that this god can purpose.
     */
    public God(ResourceLocation path, DeferredRegister<AttachmentType<?>> attachmentTypeRegister,
               Consumer<Integer> handleBonus, MutableComponent name,
               GodEntitySupplier<?> entity, HashMap<Item, Float> offerings,
               Collection<Quest> quests) {

        POINT = attachmentTypeRegister.register(
                name + "_points", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
        );

        PATH = path;
        BONUS_HANDLER = handleBonus;
        GODS.add(this);
        GOD_LOCATIONS.put(PATH, this);
        DISPLAY_NAME = name;
        GOD_ENTITY = entity;
        OFFERINGS = offerings;
        QUESTS = quests;

        for(Quest quest : quests) {
            quest.USING_GODS.add(this);
        }

    }

    /**
     * Those two function must be used before first {@link #updateRelated}
     * @param gods
     * A {@link List} of {@link God}, all the friends of these god.
     * Win point for a god will win also point for his friends and lose points
     * for his enemies and vice versa
     */
    public void addFriends(Collection<God> gods) {
        FRIENDS.addAll(gods);
    }
    public void addEnemies(Collection<God> gods) {
        ENEMIES.addAll(gods);
    }

    /**
     * Can be used for listening.
     */
    public Collection<God> getFriends() {
        return FRIENDS;
    }
    public Collection<God> getEnemies() {
        return ENEMIES;
    }

    /**
     * @return the registry name of the god.
     */
    public String getName() {
        return PATH.getPath();
    }

    public String getDisplay(Language lang) {
        return lang.getOrDefault(DISPLAY_NAME.getContents().toString());
    }

    /**
     * @return a {@link Supplier}, the source of the {@link #POINT} {@link AttachmentType}
     * <br>
     * <b style="color:#F22;">WARNING: this will NOT return the amount of points. To get it, do</b>
     * {@code Player.getData(god.getPoints())}
     */
    public Supplier<AttachmentType<Integer>> getPoints() {
        return POINT;
    }


    /**
     * Method to modify the amount of point of a player for a god.
     * @param player (mandatory)
     * The {@link Player} that win points.
     * @param amount (optional)
     * How many point he wins. Default to 1
     * Use negative number to make the player lose points.
     * @param updateRelated (optional)
     * If it calls {@link #updateRelated}. Default to true;
     */
    public void addPoints(Player player, int amount, boolean updateRelated) {
        player.setData(POINT, player.getData(POINT) + amount);
        if(updateRelated) {updateRelated(player, amount);}
    }
    public void addPoints(Player player, int amount) {
        player.setData(POINT, player.getData(POINT) + amount);
        updateRelated(player, amount);
    }
    public void addPoint(Player player) {
        player.setData(POINT, player.getData(POINT) + 1);
        updateRelated(player, 1);
    }

    /**
     * Update the {@link #POINT} of {@link #FRIENDS} and {@link #ENEMIES}
     * @param player
     * Which player won the points
     * @param points
     * How many points he won
     */
    public void updateRelated(Player player, int points) {
        for(God god : FRIENDS) {
            player.setData(god.getPoints(), (int) (player.getData(POINT) + points * 0.7f));
        }
        for(God god : ENEMIES) {
            player.setData(god.getPoints(), (int) (player.getData(POINT) + points * -0.7f));
        }

        checkBonus(player, Stream.concat(Stream.of(this),
                Stream.concat(FRIENDS.stream(), ENEMIES.stream())).collect(Collectors.toList()));
    }

    /**
     * @return
     * The entity supplier passed as a parameter in the constructor.
     */
    public GodEntitySupplier<?> getEntitySupplier() {
        return GOD_ENTITY;
    }

    /**
     * @return
     * The offerings passed as a parameter in the constructor.
     */
    public HashMap<Item, Float> getOfferings() {
        return OFFERINGS;
    }

    /**
     * @return
     * The quests passed as a parameter in the constructor.
     */
    public Collection<Quest> getQuests() {
        return QUESTS;
    }

    /**
     * To use to create other blocks like altar.
     * @param item
     * An item offered to the god.
     * @return
     * If the player that making this offer wins a point.
     */
    public boolean rollOffering(Item item) {
        try {
            float probability = OFFERINGS.get(item);
            return Math.random() < probability;
        } catch (Exception e) {
            throw new RuntimeException(e + "Not a valid/existent offering.");
        }
    }

    /**
     * @return
     * A relation level between a player and a god, based on his point given as {@code point}.
     */
    public static int levelOf(int points) {
        int lvl;

        if(points < -10) {lvl = -2;}
        else if(points < -5) {lvl = -1;}
        else if(points < 5) {lvl = 0;}
        else if(points < 10) {lvl = 1;}
        else if(points < 20) {lvl = 2;}
        else if(points < 160) {lvl = (points/20) + 1;}
        else{lvl = 10;}

        return lvl;
    }

    /**
     * Call the {@link #BONUS_HANDLER} function, giving parameter "level".
     * @param player
     * The player on whom we are performing the check.
     * @param god
     * On which {@link God}?
     */
    public static void checkBonus(Player player, God god) {
        BONUS_HANDLER.accept(levelOf(player.getData(god.getPoints())));
    }

    /**
     * @param gods
     * To perform on several gods.
     * If {@link null}, check for all gods (including other mods).
     */
    public static void checkBonus(Player player, @Nullable List<God> gods) {
        if(gods == null) {
            for(God god : Core.GODS) {
                checkBonus(player, god);
            }
        } else {
            for(God god : Core.GODS) {
                checkBonus(player, god);
            }
        }
    }

    /**
     * Return if a probability.
     * @param chance
     * Between 0 and 1. <br>
     * Example: 0.5 will create a 50% probability.
     * @return
     * If the probability has "win".
     */
    public static boolean luck(float chance) {return Math.random() < chance;}

    /**
     * Reset points of all gods. Please don't use it for bad things.
     * @param player
     * The aimed {@link Player}.
     */
    public static void resetPoints(Player player) {
        for(God god : Core.GODS) {
            player.setData(god.getPoints(), 0);
        }
    }

}
