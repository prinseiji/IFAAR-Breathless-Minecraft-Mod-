package com.kodari.breathless.client;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Ire;
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

/** The Ire: bones and cubes straight from Ire.bbmodel (generated), the clip chosen from its state (see Ire.STATE constants). */
public class IreModel extends EntityModel<IreRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("ire"), "main");
   private static final String[][] PATHS = {
         {"waist"},
         {"waist", "head"},
         {"waist", "head", "teeth"},
         {"waist", "head", "teeth2"},
         {"waist", "head", "teeth3"},
         {"waist", "head", "teeth4"},
         {"waist", "head", "teeth5"},
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
         {"waist", "left_arm"}
   };
   private final Map<String, ModelPart> bones = new HashMap<>();
   private final float[] tmp = new float[3];

   public IreModel(ModelPart root) {
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
      PartDefinition g_waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 25.5F, 0F, 0F, 0F, 0F));
      PartDefinition g_head = g_waist.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -13.7F, 5F, 0F, 0F, 0F));
      g_head.addOrReplaceChild("head_c1", CubeListBuilder.create().texOffs(33, 53).addBox(-1F, -2F, -2F, 2F, 1F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(2.5F, 0.2F, -6F, -0.08727F, 0F, 0F));
      g_head.addOrReplaceChild("head_c2", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -1F, -9F, 7F, 2F, 7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
      PartDefinition g_teeth = g_head.addOrReplaceChild("teeth", CubeListBuilder.create(), PartPose.offsetAndRotation(0.7F, 0.6F, -9.1F, 0F, 0F, 0F));
      g_teeth.addOrReplaceChild("teeth_c1", CubeListBuilder.create().texOffs(35, 1).addBox(0.3F, -3F, 0F, 0.7F, 2.2F, 0.7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.3F, 0F, 0F, -0.21804F, 0.00755F, 0.03408F));
      PartDefinition g_teeth2 = g_head.addOrReplaceChild("teeth2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.7F, 0.4F, -9.1F, 0F, 0F, 0F));
      g_teeth2.addOrReplaceChild("teeth2_c1", CubeListBuilder.create().texOffs(35, 1).addBox(0.3F, -3F, 0F, 0.7F, 2.2F, 0.7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1F, 0.9F, 0F, -0.25307F, 0F, 0F));
      PartDefinition g_teeth3 = g_head.addOrReplaceChild("teeth3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.7F, 0.6F, -9.1F, 0F, 0F, 0F));
      g_teeth3.addOrReplaceChild("teeth3_c1", CubeListBuilder.create().texOffs(35, 1).addBox(0.3F, -3F, 0F, 0.7F, 2.2F, 0.7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1F, 0.5F, 0F, -0.21777F, -0.01321F, -0.05964F));
      PartDefinition g_teeth4 = g_head.addOrReplaceChild("teeth4", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, 25.7F, -5F, 0F, 0F, 0F));
      g_teeth4.addOrReplaceChild("teeth4_c1", CubeListBuilder.create().texOffs(35, 1).addBox(0.3F, -3F, 0F, 0.7F, 2.2F, 0.7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0.9F, -25F, -4.1F, -0.21143F, 0.05422F, 0.24732F));
      PartDefinition g_teeth5 = g_head.addOrReplaceChild("teeth5", CubeListBuilder.create(), PartPose.offsetAndRotation(2.8F, 1.3F, -9.1F, 0F, 0F, 0F));
      g_teeth5.addOrReplaceChild("teeth5_c1", CubeListBuilder.create().texOffs(35, 1).addBox(0.3F, -3F, 0F, 0.7F, 2.2F, 0.7F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, -0.25307F, 0F, 0F));
      PartDefinition g_body = g_waist.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -9.25F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c1", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, -6F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 3.25F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c2", CubeListBuilder.create().texOffs(16, 32).addBox(-4F, -24F, -2F, 8F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0F, 21.25F, 0F, 0F, 0F, 0F));
      g_body.addOrReplaceChild("body_c3", CubeListBuilder.create().texOffs(29, 0).addBox(-1F, -2F, -1F, 2F, 2F, 2F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, -2.75F, 0F, 0F, 0F, 0F));
      PartDefinition g_ribs_3 = g_body.addOrReplaceChild("ribs_3", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -2.75F, 0F, 0F, 0F, 0F));
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
      PartDefinition g_right_arm = g_waist.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-6F, -9.01522F, 0.34862F, 0F, 0F, 0.9163F));
      g_right_arm.addOrReplaceChild("right_arm_c1", CubeListBuilder.create().texOffs(40, 16).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(6F, 22.98097F, -0.43578F, 0F, 0F, 0F));
      g_right_arm.addOrReplaceChild("right_arm_c2", CubeListBuilder.create().texOffs(40, 32).addBox(-8F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(6F, 22.98097F, -0.43578F, 0F, 0F, 0F));
      PartDefinition g_left_arm = g_waist.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, -10F, 0F, 0.18204F, 0.10414F, -0.51408F));
      g_left_arm.addOrReplaceChild("left_arm_c1", CubeListBuilder.create().texOffs(32, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
      g_left_arm.addOrReplaceChild("left_arm_c2", CubeListBuilder.create().texOffs(48, 48).addBox(4F, -24F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5F, 22F, 0F, 0F, 0F, 0F));
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
   public void setupAnim(IreRenderState state) {
      super.setupAnim(state);
      this.resetPose();
      float s = state.stateSeconds;
      if (state.deathSeconds >= 0.0F) {
         this.apply(IreAnims.DEATH, Math.min(state.deathSeconds, IreAnims.DEATH.length()));
         return;
      }
      // A flinch, only while it is just lying in wait or reaching.
      if (state.hitSeconds >= 0.0F && state.hitSeconds < IreAnims.HIT.length() && (state.state == Ire.IDLE || state.state == Ire.REACH)) {
         this.apply(IreAnims.HIT, state.hitSeconds);
         return;
      }
      switch (state.state) {
         // The fake: one unmoving pose taken from the idle clip.
         case Ire.HUSK -> this.apply(IreAnims.IDLE, 0.5F);
         case Ire.AMBUSH -> this.apply(IreAnims.AMBUSH, Math.min(s, IreAnims.AMBUSH.length()));
         case Ire.PULL -> this.apply(IreAnims.PULLING, s);
         case Ire.TRAPPED -> this.apply(IreAnims.TRAPPED, s);
         case Ire.REACH -> this.apply(IreAnims.REACHING, s);
         case Ire.TRAP_ATTEMPT -> this.apply(IreAnims.TRAP_ATTEMPT, Math.min(s, IreAnims.TRAP_ATTEMPT.length()));
         case Ire.ATTACK1 -> this.apply(IreAnims.ATTACK1, Math.min(s, IreAnims.ATTACK1.length()));
         case Ire.ATTACK2 -> this.apply(IreAnims.ATTACK2, Math.min(s, IreAnims.ATTACK2.length()));
         case Ire.ESCAPE -> this.apply(IreAnims.TRAP_ESCAPE, Math.min(s, IreAnims.TRAP_ESCAPE.length()));
         default -> this.apply(IreAnims.IDLE, state.ageInTicks / 20.0F);
      }
   }
}
