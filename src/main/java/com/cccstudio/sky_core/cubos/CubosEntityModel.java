package com.cccstudio.sky_core.cubos;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;

public class CubosEntityModel extends HierarchicalModel<CubosEntity> {
    @Override
    public ModelPart root() {
        return null;
    }

    @Override
    public void setupAnim(CubosEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
