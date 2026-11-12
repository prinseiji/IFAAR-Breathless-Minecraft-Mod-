package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Odium;
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

/** The Odium: bones and cubes straight from Odium.bbmodel (generated), the clip chosen from its state (see Odium.STATE_*). */
public class OdiumModel extends EntityModel<OdiumRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("odium"), "main");
   private static final String[][] PATHS = {
         {"waist"},
         {"waist", "body"},
         {"waist", "body", "ribs_3"},
         {"waist", "body", "ribs_3", "bone"},
         {"waist", "body", "ribs_3", "bone2"},
         {"waist", "body", "ribs_3", "bone3"},
         {"waist", "body", "ribs_3", "bone4"},
         {"waist", "body", "ribs_3", "bone5"},
         {"waist", "body", "ribs_3", "bone6"},
         {"waist", "body", "ribs_3", "bone7"},
         {"waist", "body", "ribs_3", "bone8"},
         {"waist", "right_arm"},
         {"waist", "left_arm"},
         {"right_leg"},
         {"left_leg"}
   };
   private final Map<String, ModelPart> bones = new HashMap<>();
   private final float[] tmp = new float[3];

   public OdiumModel(ModelPart root) {
      super(root);
      for (String[] path : PATHS) {
         ModelPart part = root;
         for (String segment : path) {
            part = part.getChild(segment);
         }
         this.bones.put(path[path.length - 1], part);
      }
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition g_waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition g_body = g_waist.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c1", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c2", CubeListBuilder.create().texOffs(16, 32).addBox(-4F, -24F, -2F, 8F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c3", CubeListBuilder.create().texOffs(29, 0).addBox(-1F, -2F, -1F, 2F, 2F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
      PartDefinition g_ribs_3 = g_body.addOrReplaceChild("ribs_3", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
      PartDefinition g_bone = g_ribs_3.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, 4F, -1F, 0F, 0F, 0F));
      g_bone.addOrReplaceChild("bone_c1", CubeListBuilder.create().texOffs(29, 0).addBox(0.10315F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.69813F, 0F));
      PartDefinition g_bone2 = g_ribs_3.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, 4F, -2F, 0F, 3.05433F, 0F));
      g_bone2.addOrReplaceChild("bone2_c1", CubeListBuilder.create().texOffs(28, 0).addBox(-0.89685F, -1F, -1.17014F, 4F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.6545F, 0F));
      PartDefinition g_bone3 = g_ribs_3.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offsetAndRotation(-6F, 4F, -1F, 0F, -2.87979F, 0F));
      g_bone3.addOrReplaceChild("bone3_c1", CubeListBuilder.create().texOffs(29, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-4F, 2F, 1F, 0F, 0.43633F, 0F));
      PartDefinition g_bone4 = g_ribs_3.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, 4F, -2F, 0F, -3.09796F, 0F));
      g_bone4.addOrReplaceChild("bone4_c1", CubeListBuilder.create().texOffs(29, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, -0.69813F, 0F));
      PartDefinition g_bone5 = g_ribs_3.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(-5F, 4F, -2F, 0F, 3.09796F, 0F));
      g_bone5.addOrReplaceChild("bone5_c1", CubeListBuilder.create().texOffs(28, 1).addBox(-0.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-3F, 4F, 1F, 0F, 0.7854F, 0F));
      PartDefinition g_bone6 = g_ribs_3.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, 4F, -2F, 0F, 0F, 0F));
      g_bone6.addOrReplaceChild("bone6_c1", CubeListBuilder.create().texOffs(28, 1).addBox(-1.89685F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 4F, 1F, 0F, -0.69813F, 0F));
      PartDefinition g_bone7 = g_ribs_3.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, 4F, -2F, 0F, 0F, 0F));
      g_bone7.addOrReplaceChild("bone7_c1", CubeListBuilder.create().texOffs(29, 1).addBox(0.10315F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-4F, 6F, 2F, 0F, 0.82903F, 0F));
      PartDefinition g_bone8 = g_ribs_3.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, 4F, -2F, 0F, 0F, 0F));
      g_bone8.addOrReplaceChild("bone8_c1", CubeListBuilder.create().texOffs(29, 1).addBox(0.10315F, -1F, -1.17014F, 3F, 1F, 1F, new CubeDeformation(0F)), PartPose.offsetAndRotation(2F, 6F, 1F, 0F, 2.18166F, 0F));
      PartDefinition g_right_arm = g_waist.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-6F, -9.01522F, 0.34862F, 0F, 0F, 0F));
      g_right_arm.addOrReplaceChild("right_arm_c1", CubeListBuilder.create().texOffs(40, 16).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(6F, 22.98097F, -0.43578F, 0F, 0F, 0F));
      g_right_arm.addOrReplaceChild("right_arm_c2", CubeListBuilder.create().texOffs(40, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(6F, 22.98097F, -0.43578F, 0F, 0F, 0F));
      PartDefinition g_left_arm = g_waist.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, -10F, 0F, 0.20944F, 0F, 0F));
      g_left_arm.addOrReplaceChild("left_arm_c1", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm.addOrReplaceChild("left_arm_c2", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      PartDefinition g_right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.9F, 13F, -2F, 0.54105F, 0F, 0.03491F));
      g_right_leg.addOrReplaceChild("right_leg_c1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      g_right_leg.addOrReplaceChild("right_leg_c2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 6F, 2F, 0.26204F, 0.04215F, 0.0113F));
      PartDefinition g_left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(2.9F, 12F, 0F, 0.00065F, 0.07213F, -0.1208F));
      g_left_leg.addOrReplaceChild("left_leg_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg.addOrReplaceChild("left_leg_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 14F, 0F, 0F, 0F, 0F));
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
   public void setupAnim(OdiumRenderState state) {
      super.setupAnim(state);
      this.resetPose();
      float s = state.stateSeconds;
      if (state.deathSeconds >= 0.0F) {
         this.apply(OdiumAnims.DEATH, Math.min(state.deathSeconds, OdiumAnims.DEATH.length()));
         return;
      }
      // A hit flinch, only while it is just walking about or chasing.
      if (state.hitSeconds >= 0.0F && state.hitSeconds < OdiumAnims.HIT.length() && (state.state == Odium.PASSIVE || state.state == Odium.HUNT)) {
         this.apply(OdiumAnims.HIT, state.hitSeconds);
         return;
      }
      switch (state.state) {
         case Odium.DORMANT -> this.apply(OdiumAnims.EMERGE, 0.0F);
         case Odium.EMERGE -> this.apply(OdiumAnims.EMERGE, Math.min(s, OdiumAnims.EMERGE.length()));
         case Odium.SPOT -> this.apply(OdiumAnims.SPOT, Math.min(s, OdiumAnims.SPOT.length()));
         case Odium.HUNT -> this.apply(OdiumAnims.CHASING, state.ageInTicks / 20.0F);
         // The lunge winds up, then freezes at 0.63 s until it is known whether it caught you.
         case Odium.ATTEMPT -> this.apply(OdiumAnims.TRAP_ATTEMPT, Math.min(s, 0.625F));
         case Odium.SUCCESS -> this.apply(OdiumAnims.TRAP_SUCCESS, Math.min(s, OdiumAnims.TRAP_SUCCESS.length()));
         case Odium.FAIL -> this.apply(OdiumAnims.TRAP_FAIL, Math.min(s, OdiumAnims.TRAP_FAIL.length()));
         case Odium.TRAPPED -> this.apply(OdiumAnims.TRAPPED, s);
         case Odium.ESCAPE -> this.apply(OdiumAnims.TRAP_ESCAPE, Math.min(s, OdiumAnims.TRAP_ESCAPE.length()));
         default -> {
            if (state.walkAnimationSpeed > 0.02F) {
               this.apply(OdiumAnims.WANDER, state.ageInTicks / 20.0F);
            } else {
               this.apply(OdiumAnims.IDLE, state.ageInTicks / 20.0F);
            }
         }
      }
   }
}
