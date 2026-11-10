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

/** The Carrion: bones and cubes straight from Carrion.bbmodel (generated), poses from its clips (idle, death = the swell). */
public class CarrionModel extends EntityModel<CarrionRenderState> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(Breathless.id("carrion"), "main");
   private static final String[][] PATHS = {
         {"heads"},
         {"heads", "head2"},
         {"heads", "smallerhead1"},
         {"heads", "smallerhead2"},
         {"heads", "smallerhead3"},
         {"heads", "smallerhead4"},
         {"heads", "smallerhead5"},
         {"heads", "head5"},
         {"heads", "head6"},
         {"heads", "head3"},
         {"heads", "head4"},
         {"stalkfoldy"},
         {"waist"},
         {"waist", "root"},
         {"waist", "root2"},
         {"waist", "root3"},
         {"waist", "root4"},
         {"waist", "root5"},
         {"waist", "right_leg"},
         {"waist", "right_leg2"},
         {"waist", "left_leg"},
         {"waist", "left_leg2"},
         {"waist", "left_leg3"}
   };
   private final Map<String, ModelPart> bones = new HashMap<>();
   private final float[] tmp = new float[3];

   public CarrionModel(ModelPart root) {
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
      PartDefinition g_heads = root.addOrReplaceChild("heads", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      PartDefinition g_head2 = g_heads.addOrReplaceChild("head2", CubeListBuilder.create(), PartPose.offsetAndRotation(4F, -16F, -1F, 0.27299F, -1.38445F, -0.22336F));
      g_head2.addOrReplaceChild("head2_c1", CubeListBuilder.create().texOffs(-1, -1).addBox(-4F, -32F, -1F, 8F, 8F, 9F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head2.addOrReplaceChild("head2_c2", CubeListBuilder.create().texOffs(32, -1).addBox(-3F, -4F, -1F, 8F, 8F, 9F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_smallerhead1 = g_heads.addOrReplaceChild("smallerhead1", CubeListBuilder.create(), PartPose.offsetAndRotation(2F, -20F, 7F, 2.87162F, -0.26653F, -3.08281F));
      g_smallerhead1.addOrReplaceChild("smallerhead1_c1", CubeListBuilder.create().texOffs(6, 4).addBox(-2F, -30F, 1F, 4F, 4F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_smallerhead1.addOrReplaceChild("smallerhead1_c2", CubeListBuilder.create().texOffs(39, 4).addBox(-1F, -2F, 1F, 4F, 4F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_smallerhead2 = g_heads.addOrReplaceChild("smallerhead2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4F, -9F, 10F, 2.50501F, 0.34332F, 2.771F));
      g_smallerhead2.addOrReplaceChild("smallerhead2_c1", CubeListBuilder.create().texOffs(6, 4).addBox(-2F, -30F, 1F, 4F, 4F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_smallerhead2.addOrReplaceChild("smallerhead2_c2", CubeListBuilder.create().texOffs(39, 4).addBox(-1F, -2F, 1F, 4F, 4F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_smallerhead3 = g_heads.addOrReplaceChild("smallerhead3", CubeListBuilder.create(), PartPose.offsetAndRotation(-8F, -14F, 3F, 2.75017F, 1.19814F, 2.84829F));
      g_smallerhead3.addOrReplaceChild("smallerhead3_c1", CubeListBuilder.create().texOffs(6, 4).addBox(-2F, -30F, 1F, 4F, 4F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_smallerhead3.addOrReplaceChild("smallerhead3_c2", CubeListBuilder.create().texOffs(39, 4).addBox(-1F, -2F, 1F, 4F, 4F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_smallerhead4 = g_heads.addOrReplaceChild("smallerhead4", CubeListBuilder.create(), PartPose.offsetAndRotation(1F, -16F, -9F, -0.34012F, 0.08413F, 0.08551F));
      g_smallerhead4.addOrReplaceChild("smallerhead4_c1", CubeListBuilder.create().texOffs(6, 4).addBox(-2F, -30F, 1F, 4F, 4F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_smallerhead4.addOrReplaceChild("smallerhead4_c2", CubeListBuilder.create().texOffs(39, 4).addBox(-1F, -2F, 1F, 4F, 4F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_smallerhead5 = g_heads.addOrReplaceChild("smallerhead5", CubeListBuilder.create(), PartPose.offsetAndRotation(-5F, -1F, -7F, -0.41863F, 0.61334F, -0.13548F));
      g_smallerhead5.addOrReplaceChild("smallerhead5_c1", CubeListBuilder.create().texOffs(5, 6).addBox(-2F, -30F, 1F, 4F, 4F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_smallerhead5.addOrReplaceChild("smallerhead5_c2", CubeListBuilder.create().texOffs(38, 6).addBox(-1F, -2F, 1F, 4F, 4F, 4F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_head5 = g_heads.addOrReplaceChild("head5", CubeListBuilder.create(), PartPose.offsetAndRotation(7F, -7F, -1F, 0.27299F, -1.38445F, -0.22336F));
      g_head5.addOrReplaceChild("head5_c1", CubeListBuilder.create().texOffs(-1, -1).addBox(-4F, -32F, -1F, 8F, 8F, 9F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head5.addOrReplaceChild("head5_c2", CubeListBuilder.create().texOffs(32, -1).addBox(-3F, -4F, -1F, 8F, 8F, 9F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_head6 = g_heads.addOrReplaceChild("head6", CubeListBuilder.create(), PartPose.offsetAndRotation(-5F, -7F, 2F, 2.06447F, 1.52057F, 1.71702F));
      g_head6.addOrReplaceChild("head6_c1", CubeListBuilder.create().texOffs(-1, -1).addBox(-4F, -32F, -1F, 8F, 8F, 9F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head6.addOrReplaceChild("head6_c2", CubeListBuilder.create().texOffs(32, -1).addBox(-3F, -4F, -1F, 8F, 8F, 9F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_head3 = g_heads.addOrReplaceChild("head3", CubeListBuilder.create(), PartPose.offsetAndRotation(3F, -9F, 6F, -3.01514F, -0.47199F, 2.81957F));
      g_head3.addOrReplaceChild("head3_c1", CubeListBuilder.create().texOffs(-1, -1).addBox(-4F, -32F, -1F, 8F, 8F, 9F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head3.addOrReplaceChild("head3_c2", CubeListBuilder.create().texOffs(32, -1).addBox(-3F, -4F, -1F, 8F, 8F, 9F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_head4 = g_heads.addOrReplaceChild("head4", CubeListBuilder.create(), PartPose.offsetAndRotation(-2F, -9F, -6F, 0.06062F, 0.00712F, 0.62525F));
      g_head4.addOrReplaceChild("head4_c1", CubeListBuilder.create().texOffs(-1, -1).addBox(-4F, -32F, -1F, 8F, 8F, 9F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
      g_head4.addOrReplaceChild("head4_c2", CubeListBuilder.create().texOffs(32, -1).addBox(-3F, -4F, -1F, 8F, 8F, 9F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-1F, -4F, 0F, -0.04367F, 0.04359F, -0.0019F));
      PartDefinition g_stalkfoldy = root.addOrReplaceChild("stalkfoldy", CubeListBuilder.create(), PartPose.offsetAndRotation(-3F, 16F, -1F, 0F, 0F, 0F));
      g_stalkfoldy.addOrReplaceChild("stalkfoldy_c1", CubeListBuilder.create().texOffs(33, 42).addBox(0F, 10F, -13F, 6F, 10F, 6F, new CubeDeformation(0F)), PartPose.offsetAndRotation(6F, 12F, 11F, 0F, 0F, -3.14159F));
      PartDefinition g_waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 11F, 0F, 0F, 0F, 0F));
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
      PartDefinition g_right_leg = g_waist.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9F, 0F, -2F, -0.1858F, 0.07637F, 0.42049F));
      g_right_leg.addOrReplaceChild("right_leg_c1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      g_right_leg.addOrReplaceChild("right_leg_c2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.9F, 7F, 2F, 0.26204F, 0.04215F, 0.0113F));
      PartDefinition g_right_leg2 = g_waist.addOrReplaceChild("right_leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.1F, 0F, 1F, 0.19217F, -0.0087F, -0.00785F));
      g_right_leg2.addOrReplaceChild("right_leg2_c1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9F, -3F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(1.9F, 5F, 2F, 0.2618F, 0F, 0F));
      g_right_leg2.addOrReplaceChild("right_leg2_c2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.9F, -4F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.1F, 6F, 1F, 1.13063F, 1.3467F, 1.06598F));
      PartDefinition g_left_leg = g_waist.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, 4F, 0F, 0.31629F, 0.06427F, -0.60184F));
      g_left_leg.addOrReplaceChild("left_leg_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg.addOrReplaceChild("left_leg_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_leg2 = g_waist.addOrReplaceChild("left_leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.9F, 4F, 0F, -0.72294F, -0.16201F, -0.29336F));
      g_left_leg2.addOrReplaceChild("left_leg2_c1", CubeListBuilder.create().texOffs(16, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0.04363F, 0F, 0F));
      g_left_leg2.addOrReplaceChild("left_leg2_c2", CubeListBuilder.create().texOffs(0, 48).addBox(-0.1F, -12F, -2F, 4F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12F, 0F, 0F, 0F, 0F));
      PartDefinition g_left_leg3 = g_waist.addOrReplaceChild("left_leg3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, 4F, -1F, -1.01286F, 0.61638F, -0.41797F));
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
   public void setupAnim(CarrionRenderState state) {
      super.setupAnim(state);
      this.resetPose();
      if (state.swell >= 0.0F) {
         // The swell is the death clip; it holds its last pose until the blast.
         this.apply(CarrionAnims.SWELL, Math.min(state.swell, CarrionAnims.SWELL.length()));
      } else {
         this.apply(CarrionAnims.IDLE, state.ageInTicks / 20.0F);
      }
   }
}
