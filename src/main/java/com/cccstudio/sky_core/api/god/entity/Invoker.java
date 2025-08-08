package com.cccstudio.sky_core.api.god.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Invoker extends Item {

    private final GodEntity ENTITY;

    public Invoker(GodEntity godEntity) {
        super(new Item.Properties().rarity(Rarity.RARE));
        ENTITY = godEntity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getMainHandItem();
        invoke(player, item, hand, level);
        return InteractionResultHolder.success(item);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.SUCCESS;
    }

    private void invoke(Player player, ItemStack item, InteractionHand hand, Level level) {
        HitResult result = player.pick(4d, 0f, false);
        Vec3 vec = result.getLocation();

        Class<? extends GodEntity> godClass = ENTITY.getClass();

        List<? extends GodEntity> gods = level.getEntitiesOfClass(godClass, player.getBoundingBox().inflate(128));

        if (!gods.isEmpty()) {
            GodEntity god = gods.get(0);
            god.teleportTo(vec.x, vec.y + 1, vec.z);
        }

        if (item.getDamageValue() < item.getMaxDamage() - 1) {
            item.setDamageValue(item.getDamageValue() + 1);
        } else {
            item.hurtAndBreak(1, player, (hand.equals(InteractionHand.MAIN_HAND)) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
    }

}
