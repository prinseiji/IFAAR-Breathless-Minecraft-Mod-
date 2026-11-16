package com.kodari.breathless.client;

import com.kodari.breathless.client.StagnatedAnims.Clip;
import com.kodari.breathless.client.StagnatedAnims.Track;

/** The Ire clips, converted from Ire.bbmodel (same format as StagnatedAnims). */
public final class IreAnims {
   private IreAnims() {
   }

   public static final Clip IDLE = new Clip(1.29167F, true,
         new Track("waist", 0, new float[]{0F}, new float[]{0.3927F, 0F, 0F}),
         new Track("body", 0, new float[]{0F}, new float[]{0.1309F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.5F, 1.20833F}, new float[]{-1.17266F, 0.16113F, 0.24191F, -1.17266F, 0.16113F, 0.24191F, -1.17266F, 0.16113F, 0.24191F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, 2F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.5F, 1.20833F}, new float[]{-1.3234F, 0.01147F, -0.13119F, -1.32616F, -0.06794F, -0.16751F, -1.3234F, 0.01147F, -0.13119F}),
         new Track("head", 0, new float[]{0F, 0.5F, 1.25F}, new float[]{0.34907F, 0F, 0F, 0.34876F, -0.01492F, 0.041F, 0.34907F, 0F, 0F}),
         new Track("head", 1, new float[]{0F}, new float[]{0F, -1.7F, -1.2F}));

   public static final Clip REACHING = new Clip(5.20833F, true,
         new Track("body", 0, new float[]{0F, 0.29167F, 0.70833F, 1.375F, 1.70833F, 2F, 2.41667F, 3.08333F, 3.66667F, 3.95833F, 4.375F, 5.04167F}, new float[]{0.30543F, 0.04363F, 0F, 0.2618F, 0.04363F, 0F, 0.26061F, 0.05039F, -0.02532F, 0.26313F, 0.03409F, 0.03551F, 0.30543F, 0.04363F, 0F, 0.2618F, 0.04363F, 0F, 0.26061F, 0.05039F, -0.02532F, 0.26313F, 0.03409F, 0.03551F, 0.30543F, 0.04363F, 0F, 0.2618F, 0.04363F, 0F, 0.26061F, 0.05039F, -0.02532F, 0.26313F, 0.03409F, 0.03551F}),
         new Track("right_arm", 0, new float[]{0F, 0.08333F, 0.25F, 0.5F, 0.625F, 0.79167F, 1F, 1.20833F, 1.58333F, 2.04167F, 2.25F, 2.66667F, 2.91667F, 3.29167F, 3.45833F, 3.66667F, 3.875F, 4.25F, 4.70833F, 4.91667F, 5.16667F}, new float[]{-1.61443F, 0F, 0F, -1.78896F, 0F, 0F, -1.1781F, 0F, 0F, -1.54165F, 0F, 0F, -1.78896F, 0F, 0F, -1.74533F, 0F, 0F, -1.7017F, 0F, 0F, -1.70174F, 0.04359F, -0.0019F, -1.789F, 0.04359F, -0.0019F, -1.70207F, -0.13077F, 0.00574F, -1.70173F, 0.04359F, -0.0019F, -1.789F, 0.04359F, -0.0019F, -1.61443F, 0F, 0F, -1.78896F, 0F, 0F, -1.74533F, 0F, 0F, -1.7017F, 0F, 0F, -1.70174F, 0.04359F, -0.0019F, -1.789F, 0.04359F, -0.0019F, -1.70207F, -0.13077F, 0.00574F, -1.70173F, 0.04359F, -0.0019F, -1.789F, 0.04359F, -0.0019F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, -1F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.25F, 0.5F, 0.70833F, 1F, 1.29167F, 1.54167F, 1.79167F, 2F, 2.29167F, 2.58333F, 2.83333F, 3.04167F, 3.33333F, 3.58333F, 3.875F, 4.125F, 4.33333F, 4.625F, 4.83333F}, new float[]{-1.8326F, 0F, 0F, -1.70195F, -0.13073F, -0.00667F, -1.79473F, 0.43601F, -0.0178F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -2.0944F, 0F, 0F, -1.74533F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.74533F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F}, new float[]{0F, 0F, -2F}),
         new Track("head", 0, new float[]{0F, 0.45833F, 1.25F, 1.875F, 2.79167F, 3.66667F, 5.16667F}, new float[]{0.08727F, 0F, 0F, 0.04363F, 0F, 0F, 0.08727F, 0F, 0F, 0.13098F, -0.04347F, -0.0038F, 0.13098F, 0.04305F, 0.00759F, 0.09076F, 0.03529F, 0.09458F, 0.08735F, 0.04305F, 0.00759F}));

   public static final Clip AMBUSH = new Clip(1F, false,
         new Track("waist", 0, new float[]{0F, 0.29167F, 1F}, new float[]{0.3927F, 0F, 0F, 0.3927F, 0F, 0F, 0F, 0F, 0F}),
         new Track("waist", 1, new float[]{0F, 0.16667F, 0.29167F, 0.83333F, 1F}, new float[]{0F, 0F, 0F, 0F, -0.5F, -3.8F, 0F, 0F, 0F, 0F, -0.5F, 6.2F, 0F, 0F, 0F}),
         new Track("waist", 2, new float[]{1F}, new float[]{1F, 1F, 1F}),
         new Track("body", 0, new float[]{0F, 0.29167F, 0.83333F, 1F}, new float[]{0.1309F, 0F, 0F, -0.61087F, 0F, 0F, -0.32847F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("body", 1, new float[]{1F}, new float[]{0F, 0F, 0F}),
         new Track("body", 2, new float[]{1F}, new float[]{1F, 1F, 1F}),
         new Track("right_arm", 0, new float[]{0F, 0.16667F, 0.29167F, 0.83333F, 1F}, new float[]{-1.17266F, 0.16113F, 0.24191F, -0.79544F, 0.99236F, 0.86175F, -1.87374F, 0.99236F, 0.86175F, -1.86215F, -0.02561F, 1.15622F, -1.83049F, 0.21732F, 0.01932F}),
         new Track("right_arm", 1, new float[]{0F, 1F}, new float[]{0F, 2F, 0F, 0F, 0F, -2F}),
         new Track("left_arm", 0, new float[]{0F, 0.16667F, 0.29167F, 0.83333F, 1F}, new float[]{-1.3234F, 0.01147F, -0.13119F, -1.14289F, -1.01535F, -0.80982F, -2.58488F, -0.94725F, -0.79194F, -1.86212F, 0.75352F, -0.24594F, -2.08516F, 0.58949F, 0.18332F}),
         new Track("left_arm", 1, new float[]{0F, 0.29167F, 1F}, new float[]{0F, 0F, 0F, 0F, 0.6F, 0F, 0F, 0F, -1F}),
         new Track("head", 0, new float[]{0F, 0.16667F, 0.29167F, 1F}, new float[]{0.34907F, 0F, 0F, -0.6545F, 0F, 0F, 0.17453F, 0F, 0F, -0.08727F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.16667F, 0.29167F}, new float[]{0F, -1.7F, -1.2F, 0F, 3.2F, -1.6F, 0F, -0.5F, -0.6F}));

   public static final Clip PULLING = new Clip(1.29167F, true,
         new Track("waist", 0, new float[]{0F, 1.20833F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F}),
         new Track("waist", 1, new float[]{0F, 1.20833F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F}),
         new Track("waist", 2, new float[]{0F, 1.20833F}, new float[]{1F, 1F, 1F, 1F, 1F, 1F}),
         new Track("body", 0, new float[]{0F, 0.125F, 0.58333F, 0.75F, 1.20833F}, new float[]{0.04363F, 0F, 0F, 0.21817F, 0F, 0F, 0.3927F, 0F, 0F, 0.21804F, -0.04326F, 0.0057F, 0.04363F, 0F, 0F}),
         new Track("body", 1, new float[]{0F, 1.20833F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F}),
         new Track("body", 2, new float[]{0F, 1.20833F}, new float[]{1F, 1F, 1F, 1F, 1F, 1F}),
         new Track("right_arm", 0, new float[]{0F, 0.625F, 0.95833F, 1.20833F}, new float[]{-1.83049F, 0.21732F, 0.01932F, -1.37713F, -0.60821F, -0.06095F, -1.37882F, -0.69544F, -0.05817F, -1.83049F, 0.21732F, 0.01932F}),
         new Track("right_arm", 1, new float[]{0F, 0.625F, 0.95833F, 1.20833F}, new float[]{0F, 0F, -2F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F}),
         new Track("left_arm", 0, new float[]{0F, 0.54167F, 0.75F, 0.95833F, 1.20833F}, new float[]{-2.08516F, 0.58949F, 0.18332F, -1.66651F, 0.74202F, -0.01572F, -1.57925F, 0.74202F, -0.01572F, -1.53635F, 0.78564F, -0.01678F, -2.08516F, 0.58949F, 0.18332F}),
         new Track("left_arm", 1, new float[]{0F, 1.20833F}, new float[]{0F, 0F, -1F, 0F, 0F, -1F}),
         new Track("bone", 0, new float[]{0F, 0.20833F, 0.29167F, 0.70833F, 1.20833F}, new float[]{0F, 0.1309F, 0F, 0.08727F, 0.1309F, 0F, 0.04363F, 0.1309F, 0F, 0.04326F, 0.00012F, -0.00569F, 0F, 0.1309F, 0F}),
         new Track("bone2", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, -0.17453F, 0F, 0F, 0F, 0F, 0F, -0.17453F, 0F, 0F, 0F, 0F, 0F, -0.17453F, 0F}),
         new Track("bone3", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, 0.08727F, 0F, 0F, 0F, 0F, 0F, 0.08727F, 0F, 0F, 0F, 0F, 0F, 0.08727F, 0F}),
         new Track("bone4", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, -0.30543F, 0F, 0F, -0.04363F, 0F, 0F, -0.30543F, 0F, 0F, -0.04363F, 0F, 0F, -0.30543F, 0F}),
         new Track("bone5", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, 0.30543F, 0F, 0F, 0.08727F, 0F, 0F, 0.30543F, 0F, 0F, 0.08727F, 0F, 0F, 0.30543F, 0F}),
         new Track("bone6", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, -0.1309F, 0F, 0F, -0.30543F, 0F, 0F, -0.1309F, 0F, 0F, -0.30543F, 0F, 0F, -0.1309F, 0F}),
         new Track("bone6", 1, new float[]{0F, 0.79167F, 1.20833F}, new float[]{0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F}),
         new Track("bone7", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, -0.21817F, 0F}),
         new Track("bone8", 0, new float[]{0F, 0.70833F, 0.79167F, 0.95833F, 1.20833F}, new float[]{0F, -0.08727F, 0F, 0F, -0.30543F, 0F, 0F, -0.08727F, 0F, 0F, -0.30543F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone8", 1, new float[]{0F, 0.79167F, 1.20833F}, new float[]{0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F}),
         new Track("head", 0, new float[]{0F, 0.91667F, 1.20833F}, new float[]{-0.08727F, 0F, 0F, 0.17388F, 0.01514F, -0.08595F, -0.08727F, 0F, 0F}));

   public static final Clip ATTACK1 = new Clip(0.625F, false,
         new Track("body", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0.04363F, 0F, -0.2618F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("body", 1, new float[]{0F}, new float[]{0F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.33333F, 0.41667F, 0.58333F}, new float[]{-1.54651F, -0.06964F, 0.06622F, -0.62376F, 1.16154F, 0.99569F, -1.57701F, -0.609F, -0.25665F, -1.76852F, -0.16784F, 0.05877F}),
         new Track("right_arm", 1, new float[]{0.33333F}, new float[]{0F, -1F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.33333F, 0.41667F, 0.58333F}, new float[]{-1.8328F, -0.08715F, 0.00458F, -2.01779F, -1.34653F, 0.23182F, -1.80994F, 0.36448F, 0.01854F, -1.7017F, 0F, 0F}),
         new Track("bone", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.21817F, 0F, 0F, -0.04363F, 0F}),
         new Track("bone2", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.30543F, 0F, 0F, 0.1309F, 0F}),
         new Track("bone3", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.08727F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone4", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.43633F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone5", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.17453F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone6", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.2618F, 0F, 0F, 0.04363F, 0F}),
         new Track("bone7", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0.00532F, -0.16105F, -0.04205F, 0.00525F, 0.01348F, -0.04113F}),
         new Track("bone8", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.43633F, 0F, 0F, 0.30543F, 0F}),
         new Track("head", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.8F, 0F, 0F, 0F, 0F}));

   public static final Clip ATTACK2 = new Clip(0.83333F, false,
         new Track("body", 0, new float[]{0F, 0.33333F, 0.41667F, 0.5F, 0.625F, 0.75F}, new float[]{0F, 0.04363F, 0F, -0.2618F, 0.04363F, 0F, 0F, 0.04363F, 0F, 0F, 0.04363F, 0F, -0.2618F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("body", 1, new float[]{0F, 0.5F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.33333F, 0.375F, 0.41667F, 0.58333F}, new float[]{-1.54651F, -0.06964F, 0.06622F, -0.62376F, 1.16154F, 0.99569F, -1.58035F, 0.27627F, 0.36952F, -1.57701F, -0.609F, -0.25665F, -1.76852F, -0.16784F, 0.05877F}),
         new Track("right_arm", 1, new float[]{0.33333F}, new float[]{0F, -1F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.33333F, 0.45833F, 0.625F, 0.75F, 0.83333F}, new float[]{-1.8328F, -0.08715F, 0.00458F, -1.16965F, 0.05907F, 0.10036F, -1.8328F, -0.08715F, 0.00458F, -3.44995F, 0.03781F, 0.68756F, -1.00649F, 0.03781F, 0.68756F, -1.8328F, -0.08715F, 0.00458F}),
         new Track("bone", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.21817F, 0F, 0F, -0.04363F, 0F}),
         new Track("bone2", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.30543F, 0F, 0F, 0.1309F, 0F}),
         new Track("bone3", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.08727F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone4", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.43633F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone5", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0.17453F, 0F, 0F, -0.08727F, 0F}),
         new Track("bone6", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.2618F, 0F, 0F, 0.04363F, 0F}),
         new Track("bone7", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0.00532F, -0.16105F, -0.04205F, 0.00525F, 0.01348F, -0.04113F}),
         new Track("bone8", 0, new float[]{0F, 0.33333F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, -0.43633F, 0F, 0F, 0.30543F, 0F}),
         new Track("head", 0, new float[]{0F, 0.33333F, 0.41667F, 0.625F, 0.70833F}, new float[]{0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.33333F, 0.41667F, 0.625F, 0.70833F}, new float[]{0F, 0F, 0F, 0F, 0.8F, 0F, 0F, 0F, 0F, 0F, 0.8F, 0F, 0F, 0F, 0F}));

   public static final Clip TRAP_ATTEMPT = new Clip(0.54167F, false,
         new Track("waist", 0, new float[]{0F, 0.125F, 0.375F}, new float[]{0F, 0F, 0F, 0.3927F, 0F, 0F, 0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.125F, 0.25F, 0.45833F}, new float[]{0F, 0F, 0F, -0.2618F, 0F, 0F, 0.34907F, 0F, 0F, -0.1309F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.125F, 0.25F, 0.5F}, new float[]{-1.48353F, 0F, 0F, -1.44202F, 0.82489F, 0.09483F, -1.46097F, -0.65158F, -0.06677F, -1.3737F, -0.65158F, -0.06677F}),
         new Track("left_arm", 0, new float[]{0F, 0.125F, 0.25F, 0.5F}, new float[]{-1.78896F, 0F, 0F, -1.79315F, -0.82899F, 0.00952F, -1.79011F, 0.52291F, -0.03908F, -1.79622F, 0.6973F, -0.04839F}),
         new Track("bone", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 0.43633F, 0F}),
         new Track("bone2", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, -0.87266F, 0F}),
         new Track("bone3", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 0.21817F, 0F}),
         new Track("bone8", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, -0.47997F, 0F}),
         new Track("bone7", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, -0.04363F, 0F}),
         new Track("bone6", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, -0.56723F, 0F}),
         new Track("bone6", 1, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 0F, -1F}),
         new Track("bone5", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 0.30543F, 0F}),
         new Track("bone4", 0, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, -0.7854F, 0F}),
         new Track("head", 0, new float[]{0F, 0.125F, 0.25F, 0.375F, 0.54167F}, new float[]{0F, 0F, 0F, -0.47997F, 0F, 0F, 0.38397F, 0F, 0F, 0.29744F, -0.02746F, 0.128F, -0.01745F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.125F, 0.25F, 0.375F, 0.54167F}, new float[]{0F, 0F, 0F, 0F, 2.5F, 0F, 0F, -1.5F, -1.8F, 0F, -1.34F, -0.9F, 0F, -0.2F, 0.3F}));

   public static final Clip TRAP_ESCAPE = new Clip(0.5F, false,
         new Track("waist", 1, new float[]{0F, 0.125F, 0.45833F}, new float[]{0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.125F, 0.29167F}, new float[]{-0.04363F, 0F, 0F, -0.34907F, 0F, 0F, -0.04363F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.125F, 0.29167F, 0.375F}, new float[]{-1.37882F, -0.69544F, -0.05817F, -1.59618F, 0.65687F, -0.09046F, -1.59108F, 0.13339F, -0.07765F, -1.59091F, -0.0411F, -0.07413F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, 0F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.125F, 0.29167F, 0.375F}, new float[]{-1.53635F, 0.78564F, -0.01678F, -1.79993F, -0.87225F, 0.01097F, -1.79295F, 0.08758F, -0.00522F, -1.7931F, 0.17484F, -0.00634F}),
         new Track("bone", 0, new float[]{0.08333F}, new float[]{0.04326F, 0.00012F, -0.00569F}),
         new Track("bone2", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, 0F, 0F, 0F, -0.17453F, 0F, 0F, 0F, 0F}),
         new Track("bone3", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, 0F, 0F, 0F, 0.08727F, 0F, 0F, 0F, 0F}),
         new Track("bone4", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, -0.04363F, 0F, 0F, -0.30543F, 0F, 0F, -0.04363F, 0F}),
         new Track("bone5", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, 0.08727F, 0F, 0F, 0.30543F, 0F, 0F, 0.08727F, 0F}),
         new Track("bone6", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, -0.30543F, 0F, 0F, -0.1309F, 0F, 0F, -0.30543F, 0F}),
         new Track("bone6", 1, new float[]{0.16667F}, new float[]{0F, 0F, -1F}),
         new Track("bone7", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, 0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F}),
         new Track("bone8", 0, new float[]{0.08333F, 0.16667F, 0.33333F}, new float[]{0F, -0.30543F, 0F, 0F, -0.08727F, 0F, 0F, -0.30543F, 0F}),
         new Track("bone8", 1, new float[]{0.16667F}, new float[]{0F, 0F, -1F}),
         new Track("head", 0, new float[]{0F, 0.125F, 0.45833F}, new float[]{0.21817F, 0F, 0F, -0.17453F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.125F, 0.45833F}, new float[]{0F, -0.6F, 0F, 0F, 0.7F, 0F, 0F, -0.1F, 0F}));

   public static final Clip TRAPPED = new Clip(1.25F, true,
         new Track("right_arm", 0, new float[]{0.04167F, 0.54167F, 1.20833F}, new float[]{-1.37882F, -0.69544F, -0.05817F, -1.23215F, -0.78098F, -0.08159F, -1.37882F, -0.69544F, -0.05817F}),
         new Track("right_arm", 1, new float[]{0.04167F}, new float[]{0F, 0F, 0F}),
         new Track("left_arm", 0, new float[]{0.04167F, 0.54167F, 1.20833F}, new float[]{-1.53635F, 0.78564F, -0.01678F, -1.55659F, 0.70022F, -0.04377F, -1.52392F, 0.8282F, -0.00105F}),
         new Track("head", 0, new float[]{0F, 0.5F, 1.20833F}, new float[]{0.17388F, 0.01514F, -0.08595F, 0.17437F, -0.00757F, 0.04297F, 0.17388F, 0.01514F, -0.08595F}),
         new Track("head", 1, new float[]{0F, 0.5F, 1.20833F}, new float[]{0F, -0.3F, 0F, 0F, -0.6F, 0F, 0F, -0.2F, 0F}));

   public static final Clip HIT = new Clip(0.41667F, false,
         new Track("waist", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.1309F, 0F, 0.1309F, 0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.34907F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -1.52716F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, -1F}),
         new Track("left_arm", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -2.18166F, 0F, 0F, -0.04363F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F, 0.20833F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 0F}),
         new Track("head", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.3927F, 0F, 0F, 0F, 0F, 0F}),
         new Track("head", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 1.7F, 0F, 0F, 0F, 0F}));

   public static final Clip DEATH = new Clip(0.5F, false,
         new Track("waist", 0, new float[]{0F, 0.125F, 0.45833F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F, -1.1781F, 0F, 0F}),
         new Track("waist", 1, new float[]{0F, 0.125F, 0.45833F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F, 0F, 3F, 4F}),
         new Track("body", 0, new float[]{0F, 0.04167F, 0.125F, 0.33333F}, new float[]{0F, 0F, 0F, -0.29095F, 0F, 0F, -0.34907F, 0F, 0F, -0.2618F, 0F, 0F}),
         new Track("body", 2, new float[]{0F, 0.125F, 0.33333F}, new float[]{1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 0.8F}),
         new Track("right_arm", 0, new float[]{0F, 0.20833F, 0.45833F}, new float[]{0F, 0F, 0F, -1.52716F, 0F, 0F, 0.1309F, 0F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 1F, -1F}),
         new Track("left_arm", 0, new float[]{0F, 0.20833F, 0.45833F}, new float[]{0F, 0F, 0F, -1.91934F, -0.17212F, 0.02894F, -0.34907F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F, 0.125F}, new float[]{0F, 0F, 0F, 0F, 0F, 2F}),
         new Track("bone7", 0, new float[]{0F, 0.20833F}, new float[]{0F, 0F, 0F, -0.04363F, 0F, 0F}),
         new Track("head", 0, new float[]{0F, 0.04167F, 0.125F, 0.20833F}, new float[]{0F, 0F, 0F, -0.1309F, 0F, 0F, -0.30543F, 0F, 0F, -0.08727F, 0F, 0F}));

}
