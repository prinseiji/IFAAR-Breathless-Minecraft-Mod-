package com.kodari.breathless.client;

/** Animation data converted from the Stagnated.bbmodel (generated; linear keyframes, rotations in radians, positions in model pixels). */
public final class StagnatedAnims {
   public record Track(String bone, int channel, float[] times, float[] values) {
      /** channel: 0 = rotation, 1 = position, 2 = scale. */
      public void sample(float t, float[] out) {
         int n = times.length;
         if (t <= times[0] || n == 1) {
            System.arraycopy(values, 0, out, 0, 3);
            return;
         }
         if (t >= times[n - 1]) {
            System.arraycopy(values, (n - 1) * 3, out, 0, 3);
            return;
         }
         int i = 0;
         while (i < n - 2 && t > times[i + 1]) {
            i++;
         }
         float a = (t - times[i]) / (times[i + 1] - times[i]);
         for (int k = 0; k < 3; k++) {
            out[k] = values[i * 3 + k] + (values[(i + 1) * 3 + k] - values[i * 3 + k]) * a;
         }
      }
   }

   public record Clip(float length, boolean loop, Track... tracks) {
      public float time(float seconds) {
         if (loop) {
            return seconds % length;
         }
         return Math.max(0.0F, Math.min(length, seconds));
      }
   }

   public static final Clip WALK = new Clip(1.625F, true,
         new Track("head", 0, new float[]{0F, 0.625F, 1.29167F}, new float[]{0F, 0F, 0F, -0.17453F, 0F, 0F, -0.08727F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.5F, 0.95833F}, new float[]{0F, 0F, 0F, -0.04363F, 0F, 0F, -0.00013F, 0.04326F, -0.0057F}),
         new Track("right_arm", 1, new float[]{0F, 0.95833F, 1.625F}, new float[]{0F, 1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F, 0.75F, 1.58333F}, new float[]{0F, 1F, 0F, 0F, -1F, 0F, 0F, 1F, 0F}),
         new Track("right_leg", 0, new float[]{0F, 0.16667F, 0.45833F, 0.75F, 0.91667F, 1.20833F}, new float[]{0F, 0F, 0F, -0.08727F, 0F, 0F, 0.04363F, 0F, 0F, 0F, 0F, 0F, -0.08727F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("left_leg", 0, new float[]{0F, 0.29167F, 0.625F, 1.04167F, 1.41667F}, new float[]{0F, 0F, 0F, -0.69813F, 0F, 0F, -0.26365F, 0.0387F, -0.02024F, -0.00185F, 0.0387F, -0.02024F, 0.25988F, 0.03875F, -0.02025F}));
   public static final Clip IDLE = new Clip(2.91667F, true,
         new Track("head", 0, new float[]{0F, 0.95833F, 1.875F, 2.91667F}, new float[]{0F, 0F, 0F, 0F, -0.21817F, 0F, -0.30543F, -0.21817F, 0F, 0.08727F, 0F, 0F}),
         new Track("body", 2, new float[]{0F, 0.58333F, 1.375F, 1.54167F, 2.125F, 2.91667F}, new float[]{1F, 1F, 1F, 1F, 1F, 1.3F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F}),
         new Track("left_leg", 0, new float[]{0F, 0.875F, 1.54167F, 2.29167F, 2.79167F}, new float[]{0F, 0F, 0F, 0F, 0F, 0.17453F, -0.1309F, 0F, 0.17453F, 0.08727F, 0F, 0.17453F, 0.08066F, 0.00738F, 0.08733F}));
   public static final Clip CHASE = new Clip(5.625F, true,
         new Track("head", 0, new float[]{0F, 0.375F, 0.66667F, 0.95833F, 1.375F, 1.70833F, 2.08333F, 2.375F, 2.66667F, 3.08333F, 3.45833F, 3.83333F, 4.125F, 4.41667F, 4.83333F, 5.29167F}, new float[]{-0.96921F, -0.2388F, -0.10795F, -1.10011F, -0.2388F, -0.10795F, -0.88183F, -0.10795F, -0.10439F, -1.01273F, -0.10795F, -0.10439F, -0.8382F, -0.10795F, -0.10439F, -0.96921F, -0.2388F, -0.10795F, -1.10011F, -0.2388F, -0.10795F, -0.88183F, -0.10795F, -0.10439F, -1.01273F, -0.10795F, -0.10439F, -0.8382F, -0.10795F, -0.10439F, -0.96921F, -0.2388F, -0.10795F, -1.10011F, -0.2388F, -0.10795F, -0.88183F, -0.10795F, -0.10439F, -1.01273F, -0.10795F, -0.10439F, -0.8382F, -0.10795F, -0.10439F, -0.96921F, -0.2388F, -0.10795F}),
         new Track("head", 1, new float[]{0F, 1.91667F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.20833F, 0.66667F, 1.08333F, 1.66667F, 2.04167F, 2.25F, 2.70833F, 3.125F, 3.70833F, 4F, 4.20833F}, new float[]{0.30543F, 0.04363F, 0F, 0F, 0.04363F, 0F, 0.21817F, 0.04363F, 0F, -0.00002F, -0.04156F, -0.01889F, 0.17452F, -0.04156F, -0.01889F, 0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F, 0.21817F, 0.04363F, 0F, -0.00002F, -0.04156F, -0.01889F, 0.17452F, -0.04156F, -0.01889F, 0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.5F, 1.04167F, 1.29167F, 1.66667F, 2.125F, 2.625F, 3.16667F, 3.41667F, 3.79167F, 4F, 4.5F, 5.04167F, 5.29167F, 5.58333F}, new float[]{0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.25F, 0.5F, 0.70833F, 1F, 1.29167F, 1.54167F, 1.79167F, 2F, 2.29167F, 2.58333F, 2.83333F, 3.04167F, 3.33333F, 3.625F, 3.875F, 4.125F, 4.33333F, 4.625F, 4.83333F}, new float[]{-1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.74533F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F}, new float[]{0F, 0F, -2F}),
         new Track("right_leg", 0, new float[]{0F, 0.33333F, 1.33333F, 1.58333F, 1.91667F, 2.125F, 2.45833F, 2.75F, 3.08333F, 3.29167F, 3.45833F, 3.625F, 3.70833F, 4.04167F, 4.20833F, 4.41667F, 4.625F, 4.875F, 5.16667F}, new float[]{-0.1309F, 0F, 0F, 0F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0.04363F, 0F, 0F, 0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F, 2.125F, 2.75F, 3.29167F}, new float[]{0F, -1F, 4F, 0F, -1F, 3F, 0F, -1F, 3F, 0F, -1F, 3F}),
         new Track("left_leg", 0, new float[]{0F, 0.08333F, 0.20833F, 0.33333F, 0.5F, 0.66667F, 0.79167F, 0.91667F, 1.08333F, 1.20833F, 1.33333F, 1.5F, 1.625F, 1.83333F, 1.95833F, 2.125F, 2.25F, 2.375F, 2.54167F, 2.75F, 2.875F, 3F, 3.125F, 3.29167F, 3.5F, 3.66667F, 3.79167F, 3.91667F, 4.08333F, 4.29167F, 4.375F, 4.5F, 4.625F, 4.79167F, 5F}, new float[]{0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.08333F, 0.33333F, 0.79167F, 1.08333F, 1.5F, 1.83333F, 2.25F, 2.54167F, 3F, 3.29167F, 3.79167F, 4.08333F, 4.5F, 4.79167F}, new float[]{0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F}));
   public static final Clip CHASE_HIT = new Clip(0.29167F, false,
         new Track("head", 0, new float[]{0F}, new float[]{-0.70741F, -0.2388F, -0.10795F}),
         new Track("head", 1, new float[]{0F}, new float[]{0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.20833F}, new float[]{0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, -1F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.08333F, 0.25F}, new float[]{-2.01779F, -1.34653F, 0.23182F, -1.80994F, 0.36448F, 0.01854F, -1.7017F, 0F, 0F}),
         new Track("right_leg", 0, new float[]{0F}, new float[]{-0.1309F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F}, new float[]{0F, -1F, 3F}),
         new Track("left_leg", 0, new float[]{0F}, new float[]{0F, 0F, 0F}));
   public static final Clip HURT = new Clip(0.41667F, false,
         new Track("head", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -2.0944F, 0F, 0F, -0.61087F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.30543F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, -1F}),
         new Track("left_arm", 1, new float[]{0F, 0.20833F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 0F}),
         new Track("left_leg", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.34907F, 0F, 0F, -0.17453F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 0F}));
   public static final Clip ARMORING = new Clip(1.04167F, false,
         new Track("head", 0, new float[]{0F, 0.16667F, 0.79167F}, new float[]{0F, 0F, 0F, -2.0944F, 0F, 0F, -0.74717F, -0.08096F, -0.03302F}),
         new Track("body", 0, new float[]{0F, 0.16667F, 0.66667F}, new float[]{0F, 0F, 0F, -0.30543F, 0F, 0F, -0.08727F, 0F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F}, new float[]{0F, 0F, 0F, 0F, 1F, -1F}),
         new Track("left_arm", 0, new float[]{0F, 0.33333F, 0.5F, 0.54167F, 0.66667F, 1.04167F}, new float[]{0F, 0F, 0F, -0.62202F, 0.2964F, -1.27297F, -0.61508F, 0.28682F, -1.27137F, -0.98223F, 0.44399F, -1.1091F, -2.37609F, 0.50823F, -1.4234F, 0F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F, 0.20833F, 0.54167F, 0.83333F}, new float[]{0F, 0F, 0F, 0F, 0F, 2F, 1F, 0F, 2F, 0F, 0F, 2F}),
         new Track("right_leg", 1, new float[]{0F, 0.16667F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, -1F}),
         new Track("left_leg", 0, new float[]{0F, 0.16667F}, new float[]{0F, 0F, 0F, -0.34907F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.16667F, 0.41667F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, -1F}));
   public static final Clip SPOT = new Clip(0.83333F, false,
         new Track("head", 0, new float[]{0F, 0.125F, 0.33333F, 0.5F}, new float[]{0F, 0F, 0F, -0.43633F, 0F, 0F, -0.56723F, 0F, -0.21817F, -0.95993F, 0F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.16667F, 0.41667F, 0.625F, 0.79167F}, new float[]{0F, 0F, 0F, -0.56723F, 0F, 0F, -1.1781F, 0F, 0F, -1.5708F, 0F, 0F, -1.7017F, 0F, 0F}),
         new Track("left_arm", 1, new float[]{0F, 0.79167F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F}));
   public static final Clip SPAWN = new Clip(7.91667F, false,
         new Track("waist", 0, new float[]{0F, 3.45833F, 5F, 5.29167F, 5.58333F, 5.79167F, 6.08333F, 7.375F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F, 0.30543F, 0F, 0F, 0.34907F, 0F, 0F, 0.1309F, 0F, 0F, 0.5791F, 0F, 0F, 1.00356F, 0F, 0F, 0.08727F, 0F, 0F}),
         new Track("waist", 1, new float[]{0F, 0.91667F, 1.54167F, 1.91667F, 3.29167F, 3.54167F, 3.875F, 4.25F, 4.375F, 4.75F, 5.45833F, 5.66667F, 6.75F, 7.16667F}, new float[]{0F, 32F, 0F, 0F, 29F, 0F, 0F, 23F, 0F, 0F, 19F, 0F, 0F, 19F, 0F, 0F, 16F, 0F, 0F, 16F, 0F, 0F, 12F, 0F, 0F, 12F, 0F, 0F, 8F, 0F, 0F, 7F, 0F, 0F, 6.44F, 0F, 0F, 6.44F, 0F, 0F, 2.44F, 0F}),
         new Track("head", 0, new float[]{0F, 0.54167F, 1.125F, 1.29167F, 1.54167F, 2F, 2.75F, 3.375F, 3.58333F, 3.83333F, 4.04167F, 4.41667F, 4.83333F, 5.66667F, 5.83333F, 6.125F, 6.83333F, 7.54167F}, new float[]{0F, 0F, 0F, -1.39626F, 0F, 0F, -1.39082F, -0.25267F, 0.06894F, -0.91085F, -0.25267F, 0.06894F, -1.6876F, -0.25267F, 0.06894F, -0.7713F, -0.25267F, 0.06894F, -1.6876F, -0.25267F, 0.06894F, -0.72767F, -0.25267F, 0.06894F, -0.66143F, -0.73017F, -0.15781F, -0.72545F, -0.09189F, 0.13697F, -0.89999F, -0.09189F, 0.13697F, -0.33275F, -0.09189F, 0.13697F, -1.03089F, -0.09189F, 0.13697F, -0.50729F, -0.09189F, 0.13697F, -0.21487F, -0.09189F, 0.13697F, -1.14304F, -0.09189F, 0.13697F, -1.26695F, -0.48092F, 0.19566F, -1.00069F, -0.09184F, 0.14198F}),
         new Track("body", 1, new float[]{0F}, new float[]{0F, 0F, 0F}),
         new Track("body", 2, new float[]{0F, 0.29167F, 0.54167F, 0.83333F, 1.125F, 1.375F, 1.75F, 2.04167F, 2.29167F, 2.66667F, 2.95833F, 3.20833F, 3.625F, 3.91667F, 4.16667F, 4.625F, 4.91667F, 5.16667F, 5.45833F, 5.75F, 6F, 6.29167F, 6.58333F, 6.83333F, 7.08333F, 7.375F, 7.625F, 7.83333F}, new float[]{1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1F, 1F, 1F, 1F}),
         new Track("right_arm", 0, new float[]{0F, 1.125F, 1.54167F, 1.91667F, 2.33333F, 2.54167F, 3.08333F, 3.20833F, 3.45833F, 3.79167F, 4.04167F, 4.79167F, 5.45833F, 6.08333F, 7.41667F}, new float[]{0F, 0F, 0F, -2.96706F, 0F, 0F, -2.35619F, 0F, 0F, -2.64433F, 0.33983F, -0.69548F, -1.75505F, 0.04668F, -0.29596F, -1.82767F, 1.06788F, -0.17796F, -2.25642F, 1.06788F, -0.17796F, -1.6775F, 1.06788F, -0.17796F, -1.26141F, 1.06788F, -0.17796F, -0.8171F, 0.89403F, -0.27626F, -0.95722F, 0.77813F, -0.3418F, -0.52089F, 0.77813F, -0.3418F, -0.7497F, 0.16602F, 0.38103F, -1.229F, 0.67097F, 0.2887F, -0.27705F, -0.10526F, -0.02484F}),
         new Track("right_arm", 1, new float[]{0F, 3.20833F, 3.79167F, 4.08333F, 4.375F, 4.75F, 6.08333F, 7.41667F}, new float[]{0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, -2.91F, 0F, 0F, 1.09F}),
         new Track("left_arm", 0, new float[]{0F, 0.58333F, 0.79167F, 0.91667F, 1.79167F, 1.91667F, 2.125F, 2.45833F, 2.70833F, 3F, 3.20833F, 3.45833F, 3.625F, 4.04167F, 4.79167F, 5.29167F, 6.08333F, 6.58333F, 7.125F, 7.41667F}, new float[]{0F, 0F, 0F, -1.49889F, 0F, -0.0927F, -3.68055F, 0F, -0.0927F, -3.07665F, 0F, -0.0927F, -3.1879F, -0.27421F, 0.93194F, -3.32235F, -0.31676F, 1.38625F, -3.08022F, -0.1688F, 0.45148F, -1.42585F, -0.03755F, 0.02583F, -0.9827F, -0.6439F, -0.24148F, -1.47721F, -0.6439F, -0.24148F, -1.79586F, -0.6439F, -0.24148F, -0.54637F, -0.6439F, -0.24148F, -0.70685F, -0.45072F, 0.16152F, -0.46321F, -0.61422F, -0.31025F, -0.05467F, -0.66518F, -0.04549F, -0.97097F, -0.66518F, -0.04549F, -1.53837F, -0.68065F, -0.04296F, -1.53837F, -0.68065F, -0.04296F, -1.59285F, -0.03993F, 0.10349F, -0.45839F, -0.03993F, 0.10349F}),
         new Track("left_arm", 1, new float[]{0F, 1.75F, 3.625F, 3.83333F, 3.875F, 4.29167F, 4.375F, 4.75F, 6.58333F, 7.125F, 7.41667F}, new float[]{0F, 0F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F, 0F, 2F, 0F, 0F, -1F, 0F, 0F, 2F, 0F, 0F, 2F, 1F, 0F, 2F, -5F, 0F, 1F, 0F}),
         new Track("right_leg", 0, new float[]{0F, 4.83333F, 7.125F}, new float[]{0F, 0F, 0F, -1.4399F, 0F, 0F, -0.5236F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F, 4.25F, 4.83333F, 7.125F}, new float[]{0F, 31F, 0F, 0F, 11F, 0F, 0F, 6F, 2F, 0F, 1F, 2F}),
         new Track("left_leg", 0, new float[]{0F, 5F}, new float[]{0F, 0F, 0F, -0.17453F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 4.25F, 4.45833F, 4.70833F, 5.33333F, 7.125F, 7.41667F}, new float[]{0F, 32F, 0F, 0F, 11F, 0F, 0F, 9F, 0F, 0F, 7F, 0F, 0F, 4F, -4F, 0F, 2F, -4F, 0F, 2F, -1F}));
   public static final Clip BREAKING_BLOCK = new Clip(1.29167F, true,
         new Track("head", 0, new float[]{0F, 0.375F, 0.66667F, 0.79167F, 0.95833F}, new float[]{-0.92076F, -0.21347F, -0.04533F, -1.28923F, -0.21347F, -0.04533F, -1.25534F, -0.30459F, -0.65049F, -1.28937F, -0.19938F, 0.04079F, -0.85304F, -0.19938F, 0.04079F}),
         new Track("body", 2, new float[]{0F, 0.16667F, 0.29167F, 0.58333F, 0.75F, 0.875F}, new float[]{1F, 1F, 1F, 1F, 1F, 1.2F, 1F, 1F, 1.1F, 1F, 1F, 1F, 1F, 1F, 1.4F, 1F, 1F, 1.1F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F, 0.70833F}, new float[]{0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.125F, 0.25F, 0.375F, 0.5F, 0.66667F, 0.83333F, 0.95833F, 1.08333F, 1.20833F}, new float[]{-0.20738F, 0.17818F, 0.02557F, 0F, 0F, 0.04363F, -0.74176F, 0F, 0.04363F, -1.309F, 0F, 0.04363F, -2.87979F, 0F, 0.04363F, -1.65806F, 0F, 0.04363F, -2.05076F, 0F, 0.04363F, -2.61799F, 0F, 0.04363F, -2.96706F, 0F, 0.04363F, -1.309F, 0F, 0.04363F}),
         new Track("right_leg", 1, new float[]{0F, 0.20833F}, new float[]{0F, 0F, 0F, 0F, 0F, -2F}),
         new Track("left_leg", 0, new float[]{0F, 0.5F}, new float[]{0F, 0F, 0F, 0.21817F, 0F, 0F}));
   public static final Clip PLACE = new Clip(0.625F, true,
         new Track("head", 0, new float[]{0F}, new float[]{-0.48238F, -0.25986F, -0.3374F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, 2F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.16667F, 0.33333F, 0.5F}, new float[]{-1.7017F, 0F, 0.04363F, -0.82903F, 0F, 0.04363F, -0.47997F, 0F, 0.04363F, -1.7017F, 0F, 0.04363F}),
         new Track("left_leg", 1, new float[]{0F, 0.08333F, 0.29167F}, new float[]{0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -1F}));

   public static final Clip SHIELD_BREAK = new Clip(0.70833F, false,
         new Track("head", 0, new float[]{0F, 0.33333F, 0.41667F, 0.5F, 0.66667F}, new float[]{-0.91984F, -0.29888F, -0.36871F, -1.6616F, -0.29888F, -0.36871F, -2.14157F, -0.29888F, -0.36871F, -0.43987F, -0.29888F, -0.36871F, -0.65804F, -0.29888F, -0.36871F}),
         new Track("body", 0, new float[]{0F, 0.41667F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, -0.30543F, 0F, 0F, 0.21817F, 0F, 0F, 0.08727F, 0F, 0F}),
         new Track("right_arm", 0, new float[]{0F, 0.41667F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, -2.50327F, -0.33261F, 0.3527F, -1.3215F, -0.83052F, 0.16107F, -0.04235F, -0.26753F, -0.00787F}),
         new Track("left_arm", 0, new float[]{0F, 0.41667F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, -2.64593F, 0.35806F, -0.39063F, -1.3605F, 0.40235F, -0.18104F, -0.15194F, 0.39877F, 0.0085F}),
         new Track("right_leg", 1, new float[]{0F, 0.41667F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 1F}),
         new Track("left_leg", 0, new float[]{0F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, -0.3927F, 0F, 0F, 0.17453F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.41667F, 0.5F, 0.66667F}, new float[]{0F, 0F, 0F, 0F, 0F, -5F, 0F, 1F, 1F, 0F, 0F, -1F}));

   public static final Clip UNARM = new Clip(0.875F, false,
         new Track("head", 0, new float[]{0F, 0.125F, 0.375F, 0.45833F}, new float[]{-0.95993F, 0F, 0F, -0.5236F, 0F, 0F, -0.53414F, -0.10066F, -0.06933F, -0.75231F, -0.10066F, -0.06933F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F, 0.45833F}, new float[]{0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.33333F, 0.58333F, 0.83333F}, new float[]{-1.74533F, 0F, 0F, -0.97134F, 1.00117F, 0.28062F, -1.3697F, 0.48375F, -0.73369F, -0.38342F, 1.37265F, -0.2458F}),
         new Track("left_arm", 1, new float[]{0F, 0.33333F}, new float[]{0F, 0F, 0F, 0F, 1F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.125F, 0.5F}, new float[]{0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F}));
   public static final Clip SPOT_ARMED = new Clip(1.83333F, false,
         new Track("head", 0, new float[]{0F, 0.33333F, 0.70833F, 1.125F, 1.66667F}, new float[]{0F, 0F, 0F, -0.56723F, 0F, -0.21817F, -0.95294F, -0.12034F, 0.06818F, -0.99658F, -0.12034F, 0.06818F, -0.97572F, -0.16991F, 0.41437F}),
         new Track("left_arm", 0, new float[]{0F, 0.16667F, 0.41667F, 0.625F, 0.79167F, 1.04167F, 1.20833F}, new float[]{0F, 0F, 0F, -0.56723F, 0F, 0F, -1.1781F, 0F, 0F, -1.5708F, 0F, 0F, -1.7017F, 0F, 0F, 0.27252F, 1.30037F, 1.44474F, -1.00164F, -0.63584F, -0.8042F}),
         new Track("left_arm", 1, new float[]{0F, 0.79167F, 1.04167F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 1F, -3F}));

   private StagnatedAnims() {
   }
}
