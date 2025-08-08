package com.cccstudio.sky_core.api.god.entity.renderer;

import com.cccstudio.sky_core.SkyCore;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GodModel<T extends Entity> extends EntityModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyCore.MODID, "layers"), "main");
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart Right_Arm;
	private final ModelPart Left_Arm;
	private final ModelPart Right_Leg;
	private final ModelPart Left_Leg;

	public GodModel(ModelPart root) {
		this.Head = root.getChild("Head");
		this.Body = root.getChild("Body");
		this.Right_Arm = root.getChild("Right_Arm");
		this.Left_Arm = root.getChild("Left_Arm");
		this.Right_Leg = root.getChild("Right_Leg");
		this.Left_Leg = root.getChild("Left_Leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0873F, 0.0F));

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Right_Arm = partdefinition.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition Left_Arm = partdefinition.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition Right_Leg = partdefinition.addOrReplaceChild("Right_Leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, 0.192F, 0.0F, 0.0349F));

		PartDefinition Left_Leg = partdefinition.addOrReplaceChild("Left_Leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, -0.1745F, 0.0F, -0.0349F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Right_Arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Left_Arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Right_Leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Left_Leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}