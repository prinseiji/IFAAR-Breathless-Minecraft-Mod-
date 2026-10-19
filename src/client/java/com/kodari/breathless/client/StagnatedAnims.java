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
         new Track("body", 0, new float[]{0F, 0.20833F, 0.66667F, 1.08333F, 1.66667F, 2.04167F, 2.25F, 2.70833F, 3.125F, 3.70833F, 4F, 4.20833F}, new float[]{0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F, 0.21817F, 0.04363F, 0F, -0.00002F, -0.04156F, -0.01889F, 0.17452F, -0.04156F, -0.01889F, 0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F, 0.21817F, 0.04363F, 0F, -0.00002F, -0.04156F, -0.01889F, 0.17452F, -0.04156F, -0.01889F, 0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.5F, 1.04167F, 1.29167F, 1.66667F, 2.125F, 2.625F, 3.16667F, 3.41667F, 3.79167F, 4F, 4.5F, 5.04167F, 5.29167F, 5.58333F}, new float[]{0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -3F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.25F, 0.5F, 0.70833F, 1F, 1.29167F, 1.54167F, 1.79167F, 2F, 2.29167F, 2.58333F, 2.83333F, 3.04167F, 3.33333F, 3.625F, 3.875F, 4.125F, 4.33333F, 4.625F, 4.83333F}, new float[]{-1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.8326F, 0F, 0F, -1.7017F, 0F, 0F, -1.87623F, 0F, 0F, -1.74533F, 0F, 0F, -1.8326F, 0F, 0F, -1.74533F, 0F, 0F}),
         new Track("right_leg", 0, new float[]{0F, 0.33333F, 1.33333F, 1.58333F, 1.91667F, 2.125F, 2.45833F, 2.75F, 3.08333F, 3.29167F, 3.45833F, 3.625F, 3.70833F, 4.04167F, 4.20833F, 4.41667F, 4.625F, 4.875F, 5.16667F}, new float[]{-0.1309F, 0F, 0F, 0F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0F, 0F, 0F, -0.1309F, 0F, 0F, 0.04363F, 0F, 0F, 0F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0.04363F, 0F, 0F, -0.21817F, 0F, 0F, 0F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F, 2.125F, 2.75F, 3.29167F}, new float[]{0F, -1F, 3F, 0F, -1F, 3F, 0F, -1F, 3F, 0F, -1F, 3F}),
         new Track("left_leg", 0, new float[]{0F, 0.125F, 0.25F, 0.41667F, 0.625F, 0.75F, 0.875F, 1F, 1.16667F, 1.375F, 1.54167F, 1.66667F, 1.79167F, 1.95833F, 2.16667F, 2.33333F, 2.45833F, 2.58333F, 2.75F, 2.95833F, 3.08333F, 3.20833F, 3.33333F, 3.5F, 3.70833F, 3.875F, 4F, 4.125F, 4.29167F, 4.5F, 4.58333F, 4.70833F, 4.83333F, 5F, 5.20833F}, new float[]{0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.95993F, 0F, 0F, -0.6545F, 0F, 0F, -0.43633F, 0F, 0F, 0F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0.125F, 0.41667F, 0.875F, 1.16667F, 1.66667F, 1.95833F, 2.45833F, 2.75F, 3.20833F, 3.5F, 4F, 4.29167F, 4.70833F, 5F}, new float[]{0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 1F, 0F}));
   public static final Clip CHASE_HIT = new Clip(0.25F, false,
         new Track("head", 0, new float[]{0F}, new float[]{-0.70741F, -0.2388F, -0.10795F}),
         new Track("head", 1, new float[]{0F}, new float[]{0F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.20833F}, new float[]{0.08727F, 0.04363F, 0F, 0F, 0.04363F, 0F}),
         new Track("right_arm", 1, new float[]{0F}, new float[]{0F, -1F, 0F}),
         new Track("left_arm", 0, new float[]{0F, 0.08333F, 0.25F}, new float[]{-1.87135F, -0.95797F, 0.0746F, -1.80994F, 0.36448F, 0.01854F, -1.7017F, 0F, 0F}),
         new Track("right_leg", 0, new float[]{0F}, new float[]{-0.1309F, 0F, 0F}),
         new Track("right_leg", 1, new float[]{0F}, new float[]{0F, -1F, 3F}),
         new Track("left_leg", 0, new float[]{0F}, new float[]{0F, 0F, 0F}));
   public static final Clip HURT = new Clip(0.41667F, false,
         new Track("head", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -2.0944F, 0F, 0F, -0.61087F, 0F, 0F}),
         new Track("body", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.30543F, 0F, 0F, 0.04363F, 0F, 0F}),
         new Track("right_arm", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, -1F}),
         new Track("right_leg", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 0F}),
         new Track("left_leg", 0, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, -0.34907F, 0F, 0F, -0.17453F, 0F, 0F}),
         new Track("left_leg", 1, new float[]{0F, 0.16667F, 0.375F}, new float[]{0F, 0F, 0F, 0F, 0F, -3F, 0F, 0F, 0F}));
   public static final Clip SPOT = new Clip(0.83333F, false,
         new Track("head", 0, new float[]{0F, 0.125F, 0.33333F, 0.5F}, new float[]{0F, 0F, 0F, -0.43633F, 0F, 0F, -0.56723F, 0F, -0.21817F, -0.95993F, 0F, 0F}));

   private StagnatedAnims() {
   }
}
