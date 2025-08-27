package com.cccstudio.sky_core.api.god.entity.renderer;

import com.cccstudio.sky_core.api.god.entity.GodEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class GodEntityRenderer<T extends GodEntity> extends LivingEntityRenderer
        <GodEntity, EntityModel<GodEntity>> {

    private final ResourceLocation TEXTURE_LOCATION;

    private final GodEntity GOD;

    public GodEntityRenderer(EntityRendererProvider.Context context, ResourceLocation texture, GodEntity god) {
        super(context, new GodModel<>(GodModel.createBodyLayer().bakeRoot()), 0.5f);
        TEXTURE_LOCATION = texture;
        GOD = god;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GodEntity entity) {
        if(GOD.getEntityData().get(GodEntity.ASPECT) == 0) {
            if(villageNear(GOD.getOnPos(), (ServerLevel) GOD.level())) {
                return ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
            } else {
                return ResourceLocation.withDefaultNamespace("textures/entity/wandering_trader.png");
            }
        } else {
            return TEXTURE_LOCATION;
        }
    }

    private boolean villageNear(BlockPos pos, @NotNull ServerLevel level) {
        HolderSet<Structure> holder_set = level.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .getTag(TagKey.create(Registries.STRUCTURE,
                ResourceLocation.withDefaultNamespace("#villages"))).get();

        return level.getChunkSource().getGenerator().findNearestMapStructure
                (level, holder_set, pos, 5, false) != null;
    }

}
