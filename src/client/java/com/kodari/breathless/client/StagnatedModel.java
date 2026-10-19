package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The Stagnated: bind pose and bones come straight from the Stagnated.bbmodel (generated), poses from its animations. */
public class StagnatedModel extends EntityModel<StagnatedRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("stagnated"), "main");

   private final ModelPart waist;
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;
   private final float[] tmp = new float[3];

   public StagnatedModel(ModelPart root) {
      super(root);
      this.waist = root.getChild("waist");
      this.head = this.waist.getChild("head");
      this.body = this.waist.getChild("body");
      this.rightArm = this.waist.getChild("right_arm");
      this.leftArm = this.waist.getChild("left_arm");
      this.rightLeg = root.getChild("right_leg");
      this.leftLeg = root.getChild("left_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition head = waist.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 1.1272F, 0.20478F, 0.27761F));
      head.addOrReplaceChild("c1", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -32F, -4F, 8F, 8F, 8F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      head.addOrReplaceChild("c2", CubeListBuilder.create().texOffs(32, 0).addBox(-4F, -4F, -4F, 8F, 8F, 8F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0F, -4F, 0F, -0.04363F, 0F, 0F));
      PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
      body.addOrReplaceChild("c3", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 6F, -2F, 0F, -0.5236F, 0F));
      body.addOrReplaceChild("c4", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-3F, 6F, -1F, 0F, 0.43633F, 0F));
      body.addOrReplaceChild("c5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-2F, 8F, -1F, 0F, 0.7854F, 0F));
      body.addOrReplaceChild("c6", CubeListBuilder.create().texOffs(3, 1).addBox(1.10315F, -1F, -1.17014F, 1F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-3F, 10F, 0F, 0F, 0.7854F, 0F));
      body.addOrReplaceChild("c7", CubeListBuilder.create().texOffs(2, 1).addBox(0.10315F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(3F, 8F, -2F, 0F, 2.61799F, 0F));
      body.addOrReplaceChild("c8", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      body.addOrReplaceChild("c9", CubeListBuilder.create().texOffs(16, 32).addBox(-4F, -24F, -2F, 8F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      PartDefinition right_arm = waist.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-5F, -8F, 0F, 0.08727F, 0F, 0F));
      right_arm.addOrReplaceChild("c10", CubeListBuilder.create().texOffs(40, 16).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      right_arm.addOrReplaceChild("c11", CubeListBuilder.create().texOffs(40, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition left_arm = waist.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, -10F, 0F, 0.20944F, 0F, 0F));
      left_arm.addOrReplaceChild("c12", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      left_arm.addOrReplaceChild("c13", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.9F, 12F, -2F, 0.36652F, 0F, 0.03491F));
      right_leg.addOrReplaceChild("c14", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      right_leg.addOrReplaceChild("c15", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 6F, 2F, 0.26204F, 0.04215F, 0.0113F));
      PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(2.9F, 12F, 0F, 0.00065F, 0.07213F, -0.1208F));
      left_leg.addOrReplaceChild("c16", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      left_leg.addOrReplaceChild("c17", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 14F, 0F, 0F, 0F, 0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   private ModelPart bone(String name) {
      return switch (name) {
         case "waist" -> this.waist;
         case "head" -> this.head;
         case "body" -> this.body;
         case "right_arm" -> this.rightArm;
         case "left_arm" -> this.leftArm;
         case "right_leg" -> this.rightLeg;
         case "left_leg" -> this.leftLeg;
         default -> null;
      };
   }

   private void apply(StagnatedAnims.Clip clip, float seconds) {
      float t = clip.time(seconds);
      for (StagnatedAnims.Track track : clip.tracks()) {
         ModelPart part = this.bone(track.bone());
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
   public void setupAnim(StagnatedRenderState state) {
      super.setupAnim(state);
      this.resetPose();
      float time = state.ageInTicks / 20.0F;
      if (state.emerge >= 0.0F) {
         this.apply(StagnatedAnims.SPAWN, state.emerge);
      } else if (state.hurt >= 0.0F) {
         this.apply(StagnatedAnims.HURT, state.hurt * StagnatedAnims.HURT.length());
      } else if (state.attack > 0.0F) {
         this.apply(StagnatedAnims.CHASE_HIT, state.attack * StagnatedAnims.CHASE_HIT.length());
      } else if (state.spot >= 0.0F) {
         this.apply(StagnatedAnims.SPOT, state.spot);
      } else if (state.aggressive) {
         this.apply(StagnatedAnims.CHASE, time);
      } else if (state.walkAnimationSpeed > 0.04F) {
         this.apply(StagnatedAnims.WALK, time);
      } else {
         this.apply(StagnatedAnims.IDLE, time);
      }
   }
}
