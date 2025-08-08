package com.cccstudio.sky_core.api.god.entity.renderer;

import com.cccstudio.sky_core.api.god.entity.GodEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class GodEntityRenderer<T extends GodEntity> extends LivingEntityRenderer
        <GodEntity, EntityModel<GodEntity>> {

    private final ResourceLocation TEXTURE_LOCATION;

    protected GodEntityRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new GodModel<>(GodModel.createBodyLayer().bakeRoot()), 0.5f);
        TEXTURE_LOCATION = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(GodEntity entity) {
        return TEXTURE_LOCATION;
    }
}
