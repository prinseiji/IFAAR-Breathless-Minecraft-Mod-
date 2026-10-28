package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The Stagnated: bind pose and bones come straight from the Stagnated.bbmodel (generated), poses from its animations. */
public class StagnatedModel extends EntityModel<StagnatedRenderState> implements ArmedModel<StagnatedRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("stagnated"), "main");
   public static final ModelLayerLocation[] ARMOR_LAYERS = {
      new ModelLayerLocation(Breathless.id("stagnated"), "armor_head"),
      new ModelLayerLocation(Breathless.id("stagnated"), "armor_chest"),
      new ModelLayerLocation(Breathless.id("stagnated"), "armor_legs"),
      new ModelLayerLocation(Breathless.id("stagnated"), "armor_feet")
   };

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

   private record Bones(PartDefinition waist, PartDefinition head, PartDefinition body, PartDefinition rightArm, PartDefinition leftArm,
      PartDefinition rightLeg, PartDefinition leftLeg) {
   }

   /** The bone hierarchy, straight from the bbmodel; the body and the armor models share it so they pose identically. */
   private static Bones bones(PartDefinition root) {
      PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition head = waist.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 1.1272F, 0.20478F, 0.27761F));
      PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
      PartDefinition right_arm = waist.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-5F, -8F, 0F, 0.08727F, 0F, 0F));
      PartDefinition left_arm = waist.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, -10F, 0F, 0.20944F, 0F, 0F));
      PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.9F, 12F, -2F, 0.36652F, 0F, 0.03491F));
      PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(2.9F, 12F, 0F, 0.00065F, 0.07213F, -0.1208F));
      return new Bones(waist, head, body, right_arm, left_arm, right_leg, left_leg);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      Bones b = bones(mesh.getRoot());
      b.head().addOrReplaceChild("c1", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -32F, -4F, 8F, 8F, 8F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      b.head().addOrReplaceChild("c2", CubeListBuilder.create().texOffs(32, 0).addBox(-4F, -4F, -4F, 8F, 8F, 8F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0F, -4F, 0F, -0.04363F, 0F, 0F));
      PartDefinition strips = b.body().addOrReplaceChild("strips", CubeListBuilder.create(), PartPose.ZERO);
      strips.addOrReplaceChild("c3", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 6F, -2F, 0F, -0.5236F, 0F));
      strips.addOrReplaceChild("c4", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-3F, 6F, -1F, 0F, 0.43633F, 0F));
      strips.addOrReplaceChild("c5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-2F, 8F, -1F, 0F, 0.7854F, 0F));
      strips.addOrReplaceChild("c6", CubeListBuilder.create().texOffs(3, 1).addBox(1.10315F, -1F, -1.17014F, 1F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-3F, 10F, 0F, 0F, 0.7854F, 0F));
      strips.addOrReplaceChild("c7", CubeListBuilder.create().texOffs(2, 1).addBox(0.10315F, -1F, -1.17014F, 2F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(3F, 8F, -2F, 0F, 2.61799F, 0F));
      b.body().addOrReplaceChild("c8", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      b.body().addOrReplaceChild("c9", CubeListBuilder.create().texOffs(16, 32).addBox(-4F, -24F, -2F, 8F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      b.rightArm().addOrReplaceChild("c10", CubeListBuilder.create().texOffs(40, 16).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      b.rightArm().addOrReplaceChild("c11", CubeListBuilder.create().texOffs(40, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
      b.leftArm().addOrReplaceChild("c12", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      b.leftArm().addOrReplaceChild("c13", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      b.rightLeg().addOrReplaceChild("c14", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      b.rightLeg().addOrReplaceChild("c15", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 6F, 2F, 0.26204F, 0.04215F, 0.0113F));
      b.leftLeg().addOrReplaceChild("c16", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      b.leftLeg().addOrReplaceChild("c17", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 14F, 0F, 0F, 0F, 0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   /** Armor shell for one slot: 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots. Cubes copy the body cubes, inflated, with vanilla armor UVs. */
   public static LayerDefinition createArmorLayer(int kind) {
      MeshDefinition mesh = new MeshDefinition();
      Bones b = bones(mesh.getRoot());
      switch (kind) {
         case 0 -> {
         b.head().addOrReplaceChild("a0", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -32F, -4F, 8F, 8F, 8F, new CubeDeformation(1F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
         }
         case 1 -> {
         b.body().addOrReplaceChild("a10", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(1F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
         b.rightArm().addOrReplaceChild("a11", CubeListBuilder.create().texOffs(40, 16).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(1F)), PartPose.offsetAndRotation(5F, 22F, 0F, 0F, 0F, 0F));
         b.leftArm().addOrReplaceChild("a12", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(1F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
         }
         case 2 -> {
         b.body().addOrReplaceChild("a20", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
         b.rightLeg().addOrReplaceChild("a21", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
         b.leftLeg().addOrReplaceChild("a22", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
         }
         case 3 -> {
         b.rightLeg().addOrReplaceChild("a30", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(1F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
         b.leftLeg().addOrReplaceChild("a31", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(1F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
         }
         default -> {
         }
      }
      return LayerDefinition.create(mesh, 64, 32);
   }

   @Override
   public void translateToHand(StagnatedRenderState state, HumanoidArm arm, PoseStack poseStack) {
      this.waist.translateAndRotate(poseStack);
      (arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm).translateAndRotate(poseStack);
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
      this.apply(clip, seconds, false);
   }

   /** The shield poses only drive the upper body, laid over whatever he is doing (so his legs keep walking). */
   private void overlay(StagnatedAnims.Clip clip, float seconds) {
      this.head.resetPose();
      this.body.resetPose();
      this.rightArm.resetPose();
      this.leftArm.resetPose();
      this.apply(clip, seconds, true);
   }

   private void apply(StagnatedAnims.Clip clip, float seconds, boolean upperOnly) {
      float t = clip.time(seconds);
      for (StagnatedAnims.Track track : clip.tracks()) {
         if (upperOnly && !(track.bone().equals("head") || track.bone().equals("body") || track.bone().equals("right_arm") || track.bone().equals("left_arm"))) {
            continue;
         }
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
      if (this.body.hasChild("strips")) {
         this.body.getChild("strips").visible = !state.elijah;
      }
      float time = state.ageInTicks / 20.0F;
      if (state.emerge >= 0.0F) {
         this.apply(StagnatedAnims.SPAWN, state.emerge);
      } else if (state.enrage >= 0.0F) {
         this.apply(StagnatedAnims.ENRAGED, state.enrage);
      } else if (state.xbKind >= 0) {
         switch (state.xbKind) {
            case 0 -> this.apply(StagnatedAnims.XB_RELOAD, state.xbTime);
            case 1 -> this.apply(StagnatedAnims.XB_RELOAD_FAST, state.xbTime);
            case 2 -> this.apply(StagnatedAnims.XB_RELOAD_AIR, state.xbTime);
            case 3 -> this.apply(StagnatedAnims.XB_RELOAD_FAST_AIR, state.xbTime);
            case 5 -> this.apply(StagnatedAnims.XB_SHOOT, state.xbTime);
            case 6 -> this.apply(StagnatedAnims.XB_SHOOT_AIR, state.xbTime);
            case 7 -> this.apply(StagnatedAnims.XB_BARRAGE, state.xbTime);
            case 8 -> this.apply(StagnatedAnims.XB_BARRAGE_AIR, state.xbTime);
            default -> this.apply(StagnatedAnims.XB_AIM, 0.0F);
         }
      } else if (state.smash >= 0.0F) {
         this.apply(StagnatedAnims.MACE_SMASH, state.smash);
      } else if (state.clutch >= 0.0F) {
         this.apply(StagnatedAnims.WATER_CLUTCH, state.clutch);
      } else if (state.wind >= 0.0F) {
         this.apply(StagnatedAnims.WINDBURST, state.wind);
      } else if (state.flying) {
         this.apply(StagnatedAnims.AIRBORNE, 0.0F);
      } else if (state.shieldDis >= 0.0F) {
         this.apply(StagnatedAnims.SHIELD_DISABLED, state.shieldDis);
      } else if (state.shield >= 0.0F) {
         this.apply(StagnatedAnims.SHIELD_BREAK, state.shield);
      } else if (state.armoring >= 0.0F) {
         this.apply(StagnatedAnims.ARMORING, state.armoring);
      } else if (state.mining >= 0.0F) {
         this.apply(StagnatedAnims.BREAKING_BLOCK, state.mining);
      } else if (state.placing >= 0.0F) {
         this.apply(StagnatedAnims.PLACE, state.placing);
      } else if (state.hurt >= 0.0F) {
         this.apply(StagnatedAnims.HURT, state.hurt * StagnatedAnims.HURT.length());
      } else if (state.attack > 0.0F) {
         this.apply(StagnatedAnims.CHASE_HIT, state.attack * StagnatedAnims.CHASE_HIT.length());
      } else if (state.unarm >= 0.0F) {
         this.apply(StagnatedAnims.UNARM, state.unarm);
      } else if (state.spotArmed >= 0.0F) {
         this.apply(StagnatedAnims.SPOT_ARMED, state.spotArmed);
      } else if (state.spot >= 0.0F) {
         this.apply(state.elijah ? StagnatedAnims.ELIJAH_SPOT : StagnatedAnims.SPOT, state.spot);
      } else if (state.aggressive) {
         this.apply(StagnatedAnims.CHASE, time);
      } else if (state.walkAnimationSpeed > 0.04F) {
         this.apply(StagnatedAnims.WALK, time);
      } else {
         this.apply(StagnatedAnims.IDLE, time);
      }
      // Shield up: the upper body takes the shield pose on top of the movement.
      if (state.swordBlock) {
         this.overlay(StagnatedAnims.SWORD_BLOCK, 0.0F);
      } else if (state.shieldHit >= 0.0F) {
         this.overlay(StagnatedAnims.SHIELD_HIT, state.shieldHit);
      } else if (state.shieldUp >= 0.0F) {
         this.overlay(StagnatedAnims.SHIELD_UP, state.shieldUp);
      } else if (state.guard) {
         this.overlay(StagnatedAnims.SHIELD_POSE, 0.0F);
      }
   }
}
