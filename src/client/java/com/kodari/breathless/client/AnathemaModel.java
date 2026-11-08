package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The Sculk Anathema: bones and cubes straight from Anathema.bbmodel (generated), poses from its clips. */
public class AnathemaModel extends EntityModel<AnathemaRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("anathema"), "main");
   private final Map<String, ModelPart> bones = new HashMap<>();
   private final float[] tmp = new float[3];

   public AnathemaModel(ModelPart root) {
      super(root);
      ModelPart m_waist = root.getChild("waist");
      this.bones.put("waist", m_waist);
      ModelPart m_head = m_waist.getChild("head");
      this.bones.put("head", m_head);
      ModelPart m_head2 = m_waist.getChild("head2");
      this.bones.put("head2", m_head2);
      ModelPart m_body = m_waist.getChild("body");
      this.bones.put("body", m_body);
      ModelPart m_body3 = m_waist.getChild("body3");
      this.bones.put("body3", m_body3);
      ModelPart m_body2 = m_waist.getChild("body2");
      this.bones.put("body2", m_body2);
      ModelPart m_right_arm = m_waist.getChild("right_arm");
      this.bones.put("right_arm", m_right_arm);
      ModelPart m_right_arm2 = m_waist.getChild("right_arm2");
      this.bones.put("right_arm2", m_right_arm2);
      ModelPart m_left_arm = m_waist.getChild("left_arm");
      this.bones.put("left_arm", m_left_arm);
      ModelPart m_left_arm2 = m_waist.getChild("left_arm2");
      this.bones.put("left_arm2", m_left_arm2);
      ModelPart m_left_arm3 = m_waist.getChild("left_arm3");
      this.bones.put("left_arm3", m_left_arm3);
      ModelPart m_root = m_waist.getChild("root");
      this.bones.put("root", m_root);
      ModelPart m_root2 = m_waist.getChild("root2");
      this.bones.put("root2", m_root2);
      ModelPart m_root3 = m_waist.getChild("root3");
      this.bones.put("root3", m_root3);
      ModelPart m_root4 = m_waist.getChild("root4");
      this.bones.put("root4", m_root4);
      ModelPart m_root5 = m_waist.getChild("root5");
      this.bones.put("root5", m_root5);
      ModelPart m_left_arm5 = m_waist.getChild("left_arm5");
      this.bones.put("left_arm5", m_left_arm5);
      ModelPart m_left_arm4 = m_waist.getChild("left_arm4");
      this.bones.put("left_arm4", m_left_arm4);
      ModelPart m_left_arm6 = m_waist.getChild("left_arm6");
      this.bones.put("left_arm6", m_left_arm6);
      ModelPart m_right_leg = root.getChild("right_leg");
      this.bones.put("right_leg", m_right_leg);
      ModelPart m_right_leg2 = root.getChild("right_leg2");
      this.bones.put("right_leg2", m_right_leg2);
      ModelPart m_left_leg = root.getChild("left_leg");
      this.bones.put("left_leg", m_left_leg);
      ModelPart m_left_leg2 = root.getChild("left_leg2");
      this.bones.put("left_leg2", m_left_leg2);
      ModelPart m_left_leg3 = root.getChild("left_leg3");
      this.bones.put("left_leg3", m_left_leg3);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition g_waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 11F, 0F, 0F, 0F, 0F));
      PartDefinition g_head = g_waist.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, 5F, -1F, -0.3098F, 1.41423F, -0.666F));
      g_head.addOrReplaceChild("head_c1", CubeListBuilder.create().texOffs(5, 3).addBox(-2F, -31F, -1F, 6F, 5F, 5F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head.addOrReplaceChild("head_c2", CubeListBuilder.create().texOffs(37, 3).addBox(-2F, -3F, -1F, 6F, 5F, 5F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0F, -4F, 0F, -0.04363F, 0F, 0F));
      PartDefinition g_head2 = g_waist.addOrReplaceChild("head2", CubeListBuilder.create(), PartPose.offsetAndRotation(7F, -10F, 4F, -3.07819F, -0.72244F, 2.73988F));
      g_head2.addOrReplaceChild("head2_c1", CubeListBuilder.create().texOffs(5, 3).addBox(-2F, -31F, -1F, 6F, 5F, 5F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head2.addOrReplaceChild("head2_c2", CubeListBuilder.create().texOffs(37, 3).addBox(-2F, -3F, -1F, 6F, 5F, 5F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0F, -4F, 0F, -0.04363F, 0F, 0F));
      PartDefinition g_body = g_waist.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, -14F, -2F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c1", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 6F, -2F, 0F, -0.5236F, 0F));
      g_body.addOrReplaceChild("body_c2", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 6F, -2F, 0F, 0.56723F, 0F));
      g_body.addOrReplaceChild("body_c3", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 8F, -2F, 0F, 0.7854F, 0F));
      g_body.addOrReplaceChild("body_c4", CubeListBuilder.create().texOffs(2, 1).addBox(0.10315F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-2F, 10F, -1F, 0F, 0.7854F, 0F));
      g_body.addOrReplaceChild("body_c5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(2F, 8F, -3F, 0F, 2.61799F, 0F));
      g_body.addOrReplaceChild("body_c6", CubeListBuilder.create().texOffs(17, 16).addBox(-3F, -6F, -2F, 6F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c7", CubeListBuilder.create().texOffs(17, 32).addBox(-3F, -24F, -2F, 6F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      PartDefinition g_body3 = g_waist.addOrReplaceChild("body3", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, -11F, 1F, 2.9395F, 0.99027F, 2.9013F));
      g_body3.addOrReplaceChild("body3_c1", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -2F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 6F, -2F, 0F, -0.69813F, 0F));
      g_body3.addOrReplaceChild("body3_c2", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -2F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 6F, -2F, 0F, 0.56723F, 0F));
      g_body3.addOrReplaceChild("body3_c3", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -2F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 8F, -2F, 0F, 0.7854F, 0F));
      g_body3.addOrReplaceChild("body3_c4", CubeListBuilder.create().texOffs(2, 1).addBox(0.10315F, -2F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 10F, -1F, 0F, 0.7854F, 0F));
      g_body3.addOrReplaceChild("body3_c5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(2F, 8F, -3F, 0F, 2.61799F, 0F));
      g_body3.addOrReplaceChild("body3_c6", CubeListBuilder.create().texOffs(17, 16).addBox(-3F, -6F, -2F, 6F, 10F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      g_body3.addOrReplaceChild("body3_c7", CubeListBuilder.create().texOffs(17, 32).addBox(-3F, -24F, -2F, 6F, 10F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 23F, 0F, 0F, 0F, 0F));
      PartDefinition g_body2 = g_waist.addOrReplaceChild("body2", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, -9F, -1F, 0.33413F, -1.33856F, -0.3695F));
      g_body2.addOrReplaceChild("body2_c1", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 6F, -2F, 0F, -0.5236F, 0F));
      g_body2.addOrReplaceChild("body2_c2", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 5F, -2F, 0F, 0.56723F, 0F));
      g_body2.addOrReplaceChild("body2_c3", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 7F, -2F, 0F, 0.7854F, 0F));
      g_body2.addOrReplaceChild("body2_c4", CubeListBuilder.create().texOffs(2, 1).addBox(0.10315F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 9F, -1F, 0F, 0.7854F, 0F));
      g_body2.addOrReplaceChild("body2_c5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(2F, 8F, -3F, 0F, 2.61799F, 0F));
      g_body2.addOrReplaceChild("body2_c6", CubeListBuilder.create().texOffs(17, 16).addBox(-3F, -6F, -2F, 6F, 10F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      g_body2.addOrReplaceChild("body2_c7", CubeListBuilder.create().texOffs(17, 32).addBox(-3F, -24F, -2F, 6F, 10F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      PartDefinition g_right_arm = g_waist.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, -17F, 2F, 0.44019F, -0.17172F, 2.09491F));
      g_right_arm.addOrReplaceChild("right_arm_c1", CubeListBuilder.create().texOffs(31, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      g_right_arm.addOrReplaceChild("right_arm_c2", CubeListBuilder.create().texOffs(31, 48).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_right_arm2 = g_waist.addOrReplaceChild("right_arm2", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, -16F, -4F, -0.61421F, -0.07357F, 2.13976F));
      g_right_arm2.addOrReplaceChild("right_arm2_c1", CubeListBuilder.create().texOffs(38, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      g_right_arm2.addOrReplaceChild("right_arm2_c2", CubeListBuilder.create().texOffs(38, 48).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_arm = g_waist.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, -18F, 2F, 0.54078F, 0.17626F, -2.14809F));
      g_left_arm.addOrReplaceChild("left_arm_c1", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm.addOrReplaceChild("left_arm_c2", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_arm2 = g_waist.addOrReplaceChild("left_arm2", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, -17F, -4F, -0.32797F, 0.09072F, -2.01785F));
      g_left_arm2.addOrReplaceChild("left_arm2_c1", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm2.addOrReplaceChild("left_arm2_c2", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_arm3 = g_waist.addOrReplaceChild("left_arm3", CubeListBuilder.create(), PartPose.offsetAndRotation(12F, -21F, -7F, -0.33981F, 0.00448F, -2.2654F));
      g_left_arm3.addOrReplaceChild("left_arm3_c1", CubeListBuilder.create().texOffs(36, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm3.addOrReplaceChild("left_arm3_c2", CubeListBuilder.create().texOffs(52, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_root = g_waist.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offsetAndRotation(6F, 11F, -7F, -0.51486F, 0.28285F, -1.38166F));
      g_root.addOrReplaceChild("root_c1", CubeListBuilder.create().texOffs(35, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_root.addOrReplaceChild("root_c2", CubeListBuilder.create().texOffs(51, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_root2 = g_waist.addOrReplaceChild("root2", CubeListBuilder.create(), PartPose.offsetAndRotation(-2F, 11F, -9F, -2.59354F, 0.38085F, -1.61338F));
      g_root2.addOrReplaceChild("root2_c1", CubeListBuilder.create().texOffs(35, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_root2.addOrReplaceChild("root2_c2", CubeListBuilder.create().texOffs(51, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_root3 = g_waist.addOrReplaceChild("root3", CubeListBuilder.create(), PartPose.offsetAndRotation(7F, 10F, 4F, 2.50071F, -0.11825F, 1.88863F));
      g_root3.addOrReplaceChild("root3_c1", CubeListBuilder.create().texOffs(35, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      g_root3.addOrReplaceChild("root3_c2", CubeListBuilder.create().texOffs(51, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      PartDefinition g_root4 = g_waist.addOrReplaceChild("root4", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, 10F, 10F, 1.89976F, -0.0278F, -3.03552F));
      g_root4.addOrReplaceChild("root4_c1", CubeListBuilder.create().texOffs(35, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      g_root4.addOrReplaceChild("root4_c2", CubeListBuilder.create().texOffs(51, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      PartDefinition g_root5 = g_waist.addOrReplaceChild("root5", CubeListBuilder.create(), PartPose.offsetAndRotation(-9F, 9F, -1F, 0.19092F, 0.26436F, 1.37653F));
      g_root5.addOrReplaceChild("root5_c1", CubeListBuilder.create().texOffs(35, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      g_root5.addOrReplaceChild("root5_c2", CubeListBuilder.create().texOffs(51, 50).addBox(5F, -24F, -1F, 2F, 12F, 2F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 1F, 0F, 0F, 0F));
      PartDefinition g_left_arm5 = g_waist.addOrReplaceChild("left_arm5", CubeListBuilder.create(), PartPose.offsetAndRotation(-9F, -22F, -10F, -0.77472F, -0.33968F, 2.40229F));
      g_left_arm5.addOrReplaceChild("left_arm5_c1", CubeListBuilder.create().texOffs(36, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm5.addOrReplaceChild("left_arm5_c2", CubeListBuilder.create().texOffs(52, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_arm4 = g_waist.addOrReplaceChild("left_arm4", CubeListBuilder.create(), PartPose.offsetAndRotation(10F, -24F, 8F, 0.58833F, 0.09072F, -2.01785F));
      g_left_arm4.addOrReplaceChild("left_arm4_c1", CubeListBuilder.create().texOffs(36, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm4.addOrReplaceChild("left_arm4_c2", CubeListBuilder.create().texOffs(52, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_arm6 = g_waist.addOrReplaceChild("left_arm6", CubeListBuilder.create(), PartPose.offsetAndRotation(-11F, -22F, 6F, 2.72273F, 0.3138F, -0.92047F));
      g_left_arm6.addOrReplaceChild("left_arm6_c1", CubeListBuilder.create().texOffs(36, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm6.addOrReplaceChild("left_arm6_c2", CubeListBuilder.create().texOffs(52, 51).addBox(5F, -24F, -1F, 2F, 12F, 1F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9F, 11F, -2F, -0.1858F, 0.07637F, 0.42049F));
      g_right_leg.addOrReplaceChild("right_leg_c1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      g_right_leg.addOrReplaceChild("right_leg_c2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 7F, 2F, 0.26204F, 0.04215F, 0.0113F));
      PartDefinition g_right_leg2 = root.addOrReplaceChild("right_leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.1F, 13F, 0F, 0.32363F, -0.01738F, -0.05062F));
      g_right_leg2.addOrReplaceChild("right_leg2_c1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      g_right_leg2.addOrReplaceChild("right_leg2_c2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 6F, 2F, 0.26204F, 0.04215F, 0.0113F));
      PartDefinition g_left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, 15F, 0F, 0.31629F, 0.06427F, -0.60184F));
      g_left_leg.addOrReplaceChild("left_leg_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg.addOrReplaceChild("left_leg_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_leg2 = root.addOrReplaceChild("left_leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.9F, 15F, 0F, -0.72294F, -0.16201F, -0.29336F));
      g_left_leg2.addOrReplaceChild("left_leg2_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg2.addOrReplaceChild("left_leg2_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_leg3 = root.addOrReplaceChild("left_leg3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, 15F, -1F, -1.01286F, 0.61638F, -0.41797F));
      g_left_leg3.addOrReplaceChild("left_leg3_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg3.addOrReplaceChild("left_leg3_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0F, 0F, 0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   private void apply(StagnatedAnims.Clip clip, float seconds) {
      float t = clip.time(seconds);
      for (StagnatedAnims.Track track : clip.tracks()) {
         ModelPart part = this.bones.get(track.bone());
         if (part == null) {
            continue;
         }
         track.sample(t, this.tmp);
         switch (track.channel()) {
            case 0 -> {
               part.xRot += this.tmp[0];
               part.yRot += this.tmp[1];
               part.zRot += this.tmp[2];
            }
            case 1 -> {
               part.x += this.tmp[0];
               part.y += this.tmp[1];
               part.z += this.tmp[2];
            }
            default -> {
               part.xScale *= this.tmp[0];
               part.yScale *= this.tmp[1];
               part.zScale *= this.tmp[2];
            }
         }
      }
   }

   @Override
   public void setupAnim(AnathemaRenderState state) {
      super.setupAnim(state);
      this.resetPose();
      if (state.dying >= 0.0F) {
         // The death clip, then the held pose until it bursts.
         if (state.dying < AnathemaAnims.DEATH.length()) {
            this.apply(AnathemaAnims.DEATH, state.dying);
         } else {
            this.apply(AnathemaAnims.DEATH_POSE, 0.0F);
         }
      } else if (state.hit >= 0.0F) {
         this.apply(AnathemaAnims.HIT, state.hit);
      } else {
         this.apply(AnathemaAnims.IDLE, state.ageInTicks / 20.0F);
      }
   }
}
