package com.cccstudio.sky_core;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cccstudio.sky_core.Core.*;

public class God {

    private final ResourceLocation PATH;

    private static Supplier<AttachmentType<Integer>> POINT;

    private final List<God> FRIENDS = new ArrayList<>();
    private final List<God> ENEMIES = new ArrayList<>();

    private final Consumer<Integer> BONUS_HANDLER;

    public God(ResourceLocation path, DeferredRegister<AttachmentType<?>> register,
               Consumer<Integer> handleBonus, MutableComponent name) {

        POINT = register.register(
                name + "points", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
        );

        PATH = path;
        BONUS_HANDLER = handleBonus;
        gods.add(this);
        GOD_LOCATIONS.put(PATH, this);

    }

    public void addFriends(List<God> gods) {
        FRIENDS.addAll(gods);
    }
    public void addEnemies(List<God> gods) {
        ENEMIES.addAll(gods);
    }
    public List<God> getFriends() {
        return FRIENDS;
    }
    public List<God> getEnemies() {
        return ENEMIES;
    }

    public String getName() {
        return PATH.getPath();
    }

    public Supplier<AttachmentType<Integer>> getPoints() {
        return POINT;
    }

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

    public void updateRelated(Player player, int points) {
        for(God god : FRIENDS) {
            player.setData(POINT, (int) (player.getData(POINT) + points * 0.7f));
        }
        for(God god : ENEMIES) {
            player.setData(POINT, (int) (player.getData(POINT) + points * -0.7f));
        }

        checkBonus(player, Stream.concat(Stream.of(this),
                Stream.concat(FRIENDS.stream(), ENEMIES.stream())).collect(Collectors.toList()));
    }

    public static int levelOf(int points) {
        int lvl;

        if(points < -10) {lvl = -2;}
        else if(points < -5) {lvl = -1;}
        else if(points < 5) {lvl = 0;}
        else if(points < 10) {lvl = 1;}
        else if(points < 20) {lvl = 2;}
        else if(points < 160) {lvl = (int) ((points/20) + 1);}
        else{lvl = 10;}

        return lvl;
    }

    public void checkBonus(Player player, God god) {
        BONUS_HANDLER.accept(levelOf(player.getData(POINT)));
    }
    public void checkBonus(Player player, @Nullable List<God> gods) {
        if(gods == null) {
            for(God god : Core.gods) {
                checkBonus(player, god);
            }
        } else {
            for(God god : gods) {
                checkBonus(player, god);
            }
        }
    }


    public static boolean luck(float chance) {return Math.random() < chance;}

    public static void resetPoints(Player player) {
        for(God god : gods) {
            player.setData(god.getPoints(), 0);
        }
    }

}
