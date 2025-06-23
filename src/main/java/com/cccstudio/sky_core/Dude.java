package com.cccstudio.sky_core;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.cccstudio.sky_core.Core.*;

public class Dude {

    private String NAME;

    private static Supplier<AttachmentType<Integer>> POINT;

    private List<Dude> FRIENDS = new ArrayList<>();
    private List<Dude> ENEMIES = new ArrayList<>();

    private Consumer<Integer> BONUS_HANDLER;

    public Dude(String name, DeferredRegister<AttachmentType<?>> register,
                Consumer<Integer> handleBonus) {

        POINT = register.register(
                name + "points", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
        );

        BONUS_HANDLER = handleBonus;
        Dudes.add(this);

    }

    public void addFriends(List<Dude> dudes) {
        FRIENDS.addAll(dudes);
    }
    public void addEnemies(List<Dude> dudes) {
        ENEMIES.addAll(dudes);
    }
    public List<Dude> getFriends() {
        return FRIENDS;
    }
    public List<Dude> getEnemies() {
        return ENEMIES;
    }

    public String getName() {
        return NAME;
    }

    public Supplier<AttachmentType<Integer>> getPoints() {
        return POINT;
    }

    public void addPoints(Player player, int amount) {
        player.setData(POINT, player.getData(POINT) + amount);
    }

    public void updateRelated(Player player, int points) {
        for(Dude dude : FRIENDS) {
            player.setData(POINT, (int) (player.getData(POINT) + points * 0.7f));
        }
        for(Dude dude : ENEMIES) {
            player.setData(POINT, (int) (player.getData(POINT) + points * -0.7f));
        }
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

    public void checkBonus(Player player, Dude dude) {
        BONUS_HANDLER.accept(levelOf(player.getData(POINT)));
    }
    public void checkBonus(Player player, @Nullable List<Dude> dudes) {
        if(dudes == null) {
            for(Dude dude : Dudes) {
                checkBonus(player, dude);
            }
        } else {
            for(Dude dude : dudes) {
                checkBonus(player, dude);
            }
        }
    }


    public static boolean luck(float chance) {return Math.random() < chance;}

    public static void resetPoints(Player player) {
        for(Dude dude : Dudes) {
            player.setData(dude.getPoints(), 0);
        }
    }

}
